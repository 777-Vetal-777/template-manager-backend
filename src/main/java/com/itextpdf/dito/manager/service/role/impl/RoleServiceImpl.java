package com.itextpdf.dito.manager.service.role.impl;

import com.itextpdf.dito.manager.entity.PermissionEntity;
import com.itextpdf.dito.manager.entity.RoleEntity;
import com.itextpdf.dito.manager.entity.RoleTypeEnum;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.exception.permission.PermissionCantBeAttachedToCustomRoleException;
import com.itextpdf.dito.manager.exception.permission.PermissionNotFoundException;
import com.itextpdf.dito.manager.exception.role.AttemptToDeleteSystemRoleException;
import com.itextpdf.dito.manager.exception.role.RoleAlreadyExistsException;
import com.itextpdf.dito.manager.exception.role.RoleNotFoundException;
import com.itextpdf.dito.manager.exception.role.UnableToDeleteSingularRoleException;
import com.itextpdf.dito.manager.exception.role.UnableToUpdateSystemRoleException;
import com.itextpdf.dito.manager.filter.role.RoleFilter;
import com.itextpdf.dito.manager.repository.role.RoleRepository;
import com.itextpdf.dito.manager.service.AbstractService;
import com.itextpdf.dito.manager.service.permission.PermissionService;
import com.itextpdf.dito.manager.service.role.RoleService;
import com.itextpdf.dito.manager.service.user.UserService;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import static com.itextpdf.dito.manager.filter.FilterUtils.getStringFromFilter;

@Component
public class RoleServiceImpl extends AbstractService implements RoleService {
    // Repositories
    private final RoleRepository roleRepository;
    // Services
    private final UserService userService;
    private final PermissionService permissionService;

    public RoleServiceImpl(final RoleRepository roleRepository,
            final UserService userService,
            final PermissionService permissionService) {
        this.roleRepository = roleRepository;
        this.userService = userService;
        this.permissionService = permissionService;
    }

    @Override
    public RoleEntity get(final String name) {
        return roleRepository.findByName(name).orElseThrow(() -> new RoleNotFoundException(name));
    }

    @Override
    public Page<RoleEntity> getByResource(final Pageable pageable, final ResourceEntity resource) {
        return roleRepository.findAllByResources(pageable, resource);
    }

    @Override
    public RoleEntity create(final RoleEntity roleEntity, final List<String> permissions) {
        if (roleRepository.findByName(roleEntity.getName()).isPresent()) {
            throw new RoleAlreadyExistsException(roleEntity.getName());
        }
        setPermissions(roleEntity, permissions);
        setDefaultPermissions(roleEntity);
        roleEntity.setType(RoleTypeEnum.CUSTOM);
        return roleRepository.save(roleEntity);
    }

    @Override
    public Page<RoleEntity> list(final Pageable pageable, final RoleFilter roleFilter, final String searchParam) {
        throwExceptionIfSortedFieldIsNotSupported(pageable.getSort());

        final Pageable pageWithSort = updateSort(pageable);
        final String name = getStringFromFilter(roleFilter.getName());
        final List<RoleTypeEnum> roleTypeEnums = roleFilter.getType();

        return StringUtils.isEmpty(searchParam)
                ? roleRepository.filter(pageWithSort, name, roleTypeEnums)
                : roleRepository.search(pageWithSort, name, roleTypeEnums, searchParam.toLowerCase());
    }

    @Override
    public RoleEntity update(final String name, final RoleEntity updatedRole, final List<String> permissions) {
        RoleEntity existingRole = roleRepository.findByName(name).orElseThrow(() -> new RoleNotFoundException(name));

        if (existingRole.getType() == RoleTypeEnum.SYSTEM) {
            throw new UnableToUpdateSystemRoleException();
        }
        if (!name.equals(updatedRole.getName()) && roleRepository.findByName(updatedRole.getName()).isPresent()) {
            throw new RoleAlreadyExistsException(updatedRole.getName());
        }

        existingRole.setName(updatedRole.getName());
        setPermissions(existingRole, permissions);
        return roleRepository.save(existingRole);
    }

    @Override
    public void delete(final String name) {
        final RoleEntity role = roleRepository.findByName(name).orElseThrow(() -> new RoleNotFoundException(name));

        if (role.getType() == RoleTypeEnum.SYSTEM) {
            throw new AttemptToDeleteSystemRoleException();
        }
        if (userService.calculateCountOfUsersWithOnlyOneRole(name) > 0) {
            throw new UnableToDeleteSingularRoleException();
        }

        roleRepository.delete(role);
    }


    @Override
    protected List<String> getSupportedSortFields() {
        return roleRepository.SUPPORTED_SORT_FIELDS;
    }

    private RoleEntity setPermissions(final RoleEntity role, List<String> permissionsName) {
        role.getPermissions().clear();
        for (final String permissionName : permissionsName) {
            final PermissionEntity permissionEntity = permissionService.get(permissionName);
            if (permissionEntity == null) {
                throw new PermissionNotFoundException(permissionName);
            } else if (!permissionEntity.getOptionalForCustomRole()) {
                throw new PermissionCantBeAttachedToCustomRoleException();
            } else {
                role.getPermissions().add(permissionEntity);
            }
        }
        return role;
    }

    private RoleEntity setDefaultPermissions(final RoleEntity role) {
        final List<PermissionEntity> defaultPermissions = permissionService.defaultPermissions();
        role.getPermissions().addAll(defaultPermissions);
        return role;
    }

    /**
     * W/A for sorting roles by number of users (as sort param cannot be changed on FE side).
     *
     * @param pageable from request
     * @return pageable with updated sort params
     */
    private Pageable updateSort(Pageable pageable) {
        Sort newSort = Sort.by(pageable.getSort().stream()
                .map(sortParam -> {
                    if (sortParam.getProperty().equals("users")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "users.size");
                    }
                    return sortParam;
                })
                .collect(Collectors.toList()));
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), newSort);
    }
}

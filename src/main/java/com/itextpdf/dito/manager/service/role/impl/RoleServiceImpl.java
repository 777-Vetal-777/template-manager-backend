package com.itextpdf.dito.manager.service.role.impl;

import com.itextpdf.dito.manager.entity.PermissionEntity;
import com.itextpdf.dito.manager.entity.RoleEntity;
import com.itextpdf.dito.manager.entity.RoleTypeEnum;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.exception.permission.PermissionCantBeAttachedToCustomRoleException;
import com.itextpdf.dito.manager.exception.permission.PermissionNotFoundException;
import com.itextpdf.dito.manager.exception.role.AttemptToDeleteSystemRoleException;
import com.itextpdf.dito.manager.exception.role.RoleAlreadyExistsException;
import com.itextpdf.dito.manager.exception.role.RoleNotFoundException;
import com.itextpdf.dito.manager.exception.role.UnableToDeleteSingularRoleException;
import com.itextpdf.dito.manager.exception.role.UnableToUpdateSystemRoleException;
import com.itextpdf.dito.manager.filter.datacollection.DataCollectionPermissionFilter;
import com.itextpdf.dito.manager.filter.role.RoleFilter;
import com.itextpdf.dito.manager.filter.template.TemplatePermissionFilter;
import com.itextpdf.dito.manager.repository.role.RoleRepository;
import com.itextpdf.dito.manager.service.AbstractService;
import com.itextpdf.dito.manager.service.permission.PermissionService;
import com.itextpdf.dito.manager.service.role.RoleService;
import com.itextpdf.dito.manager.service.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

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
    public RoleEntity getMasterRole(final String name) {
        return roleRepository.findByNameAndMasterTrue(name).orElseThrow(() -> new RoleNotFoundException(name));
    }

    @Override
    public RoleEntity getSlaveRole(final String name, final ResourceEntity resourceEntity) {
        return roleRepository.findByNameAndMasterFalseAndResources(name, resourceEntity);
    }

    @Override
    public RoleEntity getSlaveRole(final String name, final DataCollectionEntity dataCollectionEntity) {
        return roleRepository.findByNameAndMasterFalseAndDataCollections(name, dataCollectionEntity);
    }

    @Override
    public Page<RoleEntity> getSlaveRolesByResource(final Pageable pageable, final RoleFilter roleFilter, final ResourceEntity resource) {
        throwExceptionIfSortedFieldIsNotSupported(pageable.getSort());

        final String name = getStringFromFilter(roleFilter.getName());
        final List<RoleTypeEnum> roleTypeEnums = roleFilter.getType();

        final Pageable pageWithSort = updateSort(pageable);
        return roleRepository.findAllByResourcesAndMasterFalse(pageWithSort, resource, name, roleTypeEnums);
    }

    @Override
    public Page<RoleEntity> getSlaveRolesByDataCollection(final Pageable pageable, final DataCollectionPermissionFilter filter, final DataCollectionEntity dataCollection) {
        throwExceptionIfSortedFieldIsNotSupported(pageable.getSort());
        final String name = filter.getName() != null ? filter.getName().get(0) : null;
        final Pageable pageWithSort = updateSort(pageable);

        return roleRepository.findAllByDataCollectionsAndMasterFalse(pageWithSort, dataCollection, name);
    }

    @Override
    public RoleEntity create(final String name, final List<String> permissions, final Boolean master) {
        if (roleRepository.countByNameAndMasterTrue(name) > 0) {
            throw new RoleAlreadyExistsException(name);
        }
        final RoleEntity roleEntity = new RoleEntity();
        roleEntity.setName(name);
        setPermissions(roleEntity, permissions);
        setDefaultPermissions(roleEntity);
        roleEntity.setType(RoleTypeEnum.CUSTOM);
        roleEntity.setMaster(master);
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
        RoleEntity existingRole = roleRepository.findByNameAndMasterTrue(name).orElseThrow(() -> new RoleNotFoundException(name));

        if (existingRole.getType() == RoleTypeEnum.SYSTEM) {
            throw new UnableToUpdateSystemRoleException();
        }
        if (!name.equals(updatedRole.getName()) && roleRepository.findByNameAndMasterTrue(updatedRole.getName()).isPresent()) {
            throw new RoleAlreadyExistsException(updatedRole.getName());
        }

        existingRole.setName(updatedRole.getName());
        setPermissions(existingRole, permissions);
        return roleRepository.save(existingRole);
    }

    @Override
    public void deleteMasterRole(final String name) {
        final RoleEntity masterRole = roleRepository.findByNameAndMasterTrue(name).orElseThrow(() -> new RoleNotFoundException(name));

        if (masterRole.getType() == RoleTypeEnum.SYSTEM) {
            throw new AttemptToDeleteSystemRoleException();
        }
        if (userService.calculateCountOfUsersWithOnlyOneRole(name) > 0) {
            throw new UnableToDeleteSingularRoleException();
        }

        final List<RoleEntity> slaveRoles = roleRepository.findByNameAndMasterFalse(name);
        slaveRoles.add(masterRole);

        roleRepository.deleteAll(slaveRoles);
    }

    @Override
    public RoleEntity getSlaveRole(final String name, final TemplateEntity templateEntity) {
        return roleRepository.findByNameAndMasterFalseAndTemplates(name, templateEntity);
    }

    @Override
    public Page<RoleEntity> getSlaveRolesByTemplate(final Pageable pageable, final TemplatePermissionFilter filter, final TemplateEntity templateEntity) {
        //TODO: method is not used in project. Should be it removed later?
        throwExceptionIfSortedFieldIsNotSupported(pageable.getSort());
        final String name = getStringFromFilter(filter.getName().get(0));
        final Pageable pageWithSort = updateSort(pageable);

        return roleRepository.findAllByTemplatesAndMasterFalse(pageWithSort, templateEntity, name);
    }

    @Override
    public void delete(final RoleEntity roleEntity) {
        roleRepository.delete(roleEntity);
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
                throw new PermissionCantBeAttachedToCustomRoleException(permissionEntity.getName());
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
                    if (sortParam.getProperty().equals("permissions")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "permissions.size");
                    }
                    return sortParam;
                })
                .collect(Collectors.toList()));
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), newSort);
    }
}

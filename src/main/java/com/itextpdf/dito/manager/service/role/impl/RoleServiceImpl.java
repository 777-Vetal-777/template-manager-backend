package com.itextpdf.dito.manager.service.role.impl;

import com.itextpdf.dito.manager.component.builder.specification.role.RoleSpecificationBuilder;
import com.itextpdf.dito.manager.entity.PermissionEntity;
import com.itextpdf.dito.manager.entity.RoleEntity;
import com.itextpdf.dito.manager.entity.RoleType;
import com.itextpdf.dito.manager.exception.permission.PermissionCantBeAttachedToCustomRoleException;
import com.itextpdf.dito.manager.exception.permission.PermissionNotFoundException;
import com.itextpdf.dito.manager.exception.role.AttemptToDeleteSystemRoleException;
import com.itextpdf.dito.manager.exception.role.RoleAlreadyExistsException;
import com.itextpdf.dito.manager.exception.role.RoleNotFoundException;
import com.itextpdf.dito.manager.exception.role.UnableToDeleteSingularRoleException;
import com.itextpdf.dito.manager.exception.role.UnableToUpdateSystemRoleException;
import com.itextpdf.dito.manager.filter.role.RoleFilter;
import com.itextpdf.dito.manager.repository.permission.PermissionRepository;
import com.itextpdf.dito.manager.repository.role.RoleRepository;
import com.itextpdf.dito.manager.repository.role.RoleTypeRepository;
import com.itextpdf.dito.manager.repository.user.UserRepository;
import com.itextpdf.dito.manager.service.AbstractService;
import com.itextpdf.dito.manager.service.role.RoleService;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class RoleServiceImpl extends AbstractService implements RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final RoleTypeRepository roleTypeRepository;
    private final RoleSpecificationBuilder roleSpecificationBuilder;

    public RoleServiceImpl(final RoleRepository roleRepository,
            final UserRepository userRepository,
            final PermissionRepository permissionRepository,
            final RoleTypeRepository roleTypeRepository,
            final RoleSpecificationBuilder roleSpecificationBuilder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
        this.roleTypeRepository = roleTypeRepository;
        this.roleSpecificationBuilder = roleSpecificationBuilder;
    }

    @Override
    public RoleEntity create(final RoleEntity roleEntity, final List<String> permissions) {
        if (roleRepository.findByName(roleEntity.getName()).isPresent()) {
            throw new RoleAlreadyExistsException(roleEntity.getName());
        }
        setPermissions(roleEntity, permissions);
        roleEntity.setType(roleTypeRepository.findByName(RoleType.CUSTOM));
        return roleRepository.save(roleEntity);
    }

    @Override
    public Page<RoleEntity> list(final Pageable pageable, final RoleFilter roleFilter, final String searchParam) {
        final Specification<RoleEntity> specification = roleSpecificationBuilder.build(roleFilter, searchParam);
        return roleRepository.findAll(specification, pageable);
    }

    @Override
    public RoleEntity update(final String name, final RoleEntity updatedRole, final List<String> permissions) {
        RoleEntity existingRole = roleRepository.findByName(name).orElseThrow(() -> new RoleNotFoundException(name));

        if (existingRole.getType().getName() == RoleType.SYSTEM) {
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

        if (role.getType().getName() == RoleType.SYSTEM) {
            throw new AttemptToDeleteSystemRoleException();
        }
        if (userRepository.countOfUserWithOnlyOneRole(name) > 0) {
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
            final PermissionEntity permissionEntity = permissionRepository.findByName(permissionName);
            if (permissionEntity == null) {
                throw new PermissionNotFoundException(permissionName);
            } else if (!permissionEntity.getAvailableForCustomRole()) {
                throw new PermissionCantBeAttachedToCustomRoleException();
            } else {
                role.getPermissions().add(permissionEntity);
            }
        }
        return role;
    }
}

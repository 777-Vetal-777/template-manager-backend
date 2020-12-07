package com.itextpdf.dito.manager.service.role.impl;

import com.itextpdf.dito.manager.entity.PermissionEntity;
import com.itextpdf.dito.manager.entity.RoleEntity;
import com.itextpdf.dito.manager.entity.RoleType;
import com.itextpdf.dito.manager.exception.PermissionCantBeAttachedToCustomRole;
import com.itextpdf.dito.manager.exception.PermissionNotFound;
import com.itextpdf.dito.manager.exception.RoleAlreadyExistsException;
import com.itextpdf.dito.manager.exception.RoleCannotBeRemovedException;
import com.itextpdf.dito.manager.exception.RoleNotFoundException;
import com.itextpdf.dito.manager.repository.permission.PermissionRepository;
import com.itextpdf.dito.manager.repository.role.RoleRepository;
import com.itextpdf.dito.manager.repository.role.RoleTypeRepository;
import com.itextpdf.dito.manager.repository.user.UserRepository;
import com.itextpdf.dito.manager.service.AbstractService;
import com.itextpdf.dito.manager.service.role.RoleService;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Component
public class RoleServiceImpl extends AbstractService implements RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final RoleTypeRepository roleTypeRepository;

    public RoleServiceImpl(final RoleRepository roleRepository,
                           final UserRepository userRepository,
                           final PermissionRepository permissionRepository,
                           final RoleTypeRepository roleTypeRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
        this.roleTypeRepository = roleTypeRepository;
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
    public Page<RoleEntity> list(final Pageable pageable, final String searchParam) {
        return StringUtils.isEmpty(searchParam)
                ? roleRepository.findAll(pageable)
                : roleRepository.search(pageable, searchParam);
    }

    @Override
    public RoleEntity update(final String roleName, final RoleEntity updatedRole, final List<String> permissions) {
        RoleEntity existingRole = roleRepository.findByName(roleName).orElseThrow(RoleNotFoundException::new);
        if (roleRepository.findByName(updatedRole.getName()).isPresent()) {
            throw new RoleAlreadyExistsException(updatedRole.getName());
        }
        existingRole.setName(updatedRole.getName());
        setPermissions(existingRole, permissions);
        return roleRepository.save(existingRole);
    }

    @Override
    public void delete(final String name) {
        final RoleEntity role = roleRepository.findByName(name).orElseThrow(RoleNotFoundException::new);
        if (role.getType().getName() == RoleType.SYSTEM) {
            throw new RoleCannotBeRemovedException(
                    new StringBuilder("System role cannot be removed: ")
                            .append(name)
                            .toString());
        }
        if (!CollectionUtils.isEmpty(userRepository.countOfUserWithOnlyOneRole(name))) {
            throw new RoleCannotBeRemovedException(
                    new StringBuilder("Role cannot be removed. There are users with only one role: ")
                            .append(name)
                            .toString());
        }
        roleRepository.delete(role);
    }


    @Override
    protected List<String> getSupportedSortFields() {
        return roleRepository.SUPPORTED_SORT_FIELDS;
    }

    private RoleEntity setPermissions(final RoleEntity role, List<String> permissionsName) {
        for (final String permissionName : permissionsName) {
            final PermissionEntity permissionEntity = permissionRepository.findByName(permissionName);
            if (permissionEntity == null) {
                throw new PermissionNotFound();
            } else if (!permissionEntity.getAvailableForCustomRole()) {
                throw new PermissionCantBeAttachedToCustomRole();
            } else {
                role.getPermissions().add(permissionEntity);
            }
        }
        return role;
    }
}

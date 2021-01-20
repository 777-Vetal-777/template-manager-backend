package com.itextpdf.dito.manager.service;

import com.itextpdf.dito.manager.entity.RoleEntity;
import com.itextpdf.dito.manager.exception.resource.ForbiddenOperationException;
import com.itextpdf.dito.manager.exception.role.UnableToSetPermissionsException;
import com.itextpdf.dito.manager.exception.sort.UnsupportedSortFieldException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;

public abstract class AbstractService {

    protected abstract List<String> getSupportedSortFields();

    protected void throwExceptionIfSortedFieldIsNotSupported(final Sort sort) {
        sort.forEach(sortedField -> {
            if (!getSupportedSortFields().contains(sortedField.getProperty())) {
                throw new UnsupportedSortFieldException(buildUnsupportedSortedFieldMessage(sortedField.getProperty()));

            }
        });
    }

    private String buildUnsupportedSortedFieldMessage(final String sortedField) {
        final StringBuilder result = new StringBuilder("sorting by field ");
        result.append(sortedField);
        result.append(" is not supported");
        return result.toString();
    }

    protected boolean isUserAdmin(final Set<String> userRoleNames) {
        return userRoleNames.contains("GLOBAL_ADMINISTRATOR") || userRoleNames.contains("ADMINISTRATOR");
    }

    protected Set<String> retrieveSetOfRoleNames(final Set<RoleEntity> roleEntities) {
        return roleEntities.stream().map(RoleEntity::getName).collect(Collectors.toSet());
    }

    protected void checkNotAdminRole(final RoleEntity roleEntity) {
        if ("GLOBAL_ADMINISTRATOR".equals(roleEntity.getName()) || "ADMINISTRATOR".equals(roleEntity.getName())) {
            throw new UnableToSetPermissionsException();
        }
    }

    protected void checkUserPermissions(final Set<String> userRoleNames,
                                        final Set<RoleEntity> entityAppliedRoles,
                                        final String requiredPermission) {
        if (!isUserAdmin(userRoleNames) && !entityAppliedRoles.isEmpty()) {

            boolean isPermissionRolePresented = false;
            final Set<String> entityAppliedRolesWithRequiredPermission = retrieveSetOfRoleNamesFilteredByPermission(
                    entityAppliedRoles, requiredPermission);
            for (final String role : entityAppliedRolesWithRequiredPermission) {
                if (userRoleNames.contains(role)) {
                    isPermissionRolePresented = true;
                    break;
                }
            }

            if (!isPermissionRolePresented) {
                throw new ForbiddenOperationException();
            }
        }
    }

    protected Set<String> retrieveSetOfRoleNamesFilteredByPermission(final Set<RoleEntity> roleEntities,
                                                                     final String permission) {
        return roleEntities.stream().filter(roleEntity -> roleEntity.getPermissions().stream()
                .anyMatch(permissionEntity -> permissionEntity.getName().equals(permission)))
                .map(RoleEntity::getName).collect(
                        Collectors.toSet());
    }
}

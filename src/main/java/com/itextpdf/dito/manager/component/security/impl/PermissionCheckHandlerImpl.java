package com.itextpdf.dito.manager.component.security.impl;


import com.itextpdf.dito.manager.component.security.PermissionCheckHandler;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionType;
import com.itextpdf.dito.manager.dto.permission.PermissionDTO;
import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.dto.role.RoleDTO;
import com.itextpdf.dito.manager.entity.PermissionEntity;
import com.itextpdf.dito.manager.entity.RoleEntity;
import com.itextpdf.dito.manager.entity.TemplateTypeEnum;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.model.datacollection.DataCollectionModelWithRoles;
import com.itextpdf.dito.manager.model.resource.ResourceModelWithRoles;
import com.itextpdf.dito.manager.model.template.TemplateModelWithRoles;
import com.itextpdf.dito.manager.service.user.UserService;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PermissionCheckHandlerImpl implements PermissionCheckHandler {

    private UserService userService;

    private final Set<String> dataCollectionPermissions = Set.of("E6_US34_EDIT_DATA_COLLECTION_METADATA", "E6_US35_CREATE_A_NEW_VERSION_OF_DATA_COLLECTION_USING_JSON",
            "E6_US37_ROLL_BACK_OF_THE_DATA_COLLECTION", "E6_US38_DELETE_DATA_COLLECTION", "E7_US44_CREATE_NEW_DATA_SAMPLE_BASED_ON_JSON_FILE",
            "E7_US47_EDIT_SAMPLE_METADATA", "E7_US48_CREATE_NEW_VERSION_OF_DATA_SAMPLE", "E7_US50_DELETE_DATA_SAMPLE");

    private final Map<TemplateTypeEnum, Set<String>> allowedTemplatePermissions = Map.of(
            TemplateTypeEnum.FOOTER, Set.of("E9_US75_EDIT_TEMPLATE_METADATA_STANDARD", "E9_US76_CREATE_NEW_VERSION_OF_TEMPLATE_STANDARD", "E9_US80_ROLLBACK_OF_THE_STANDARD_TEMPLATE", "E9_US81_PREVIEW_TEMPLATE_STANDARD", "E9_US24_EXPORT_TEMPLATE_DATA"),
            TemplateTypeEnum.HEADER, Set.of("E9_US75_EDIT_TEMPLATE_METADATA_STANDARD", "E9_US76_CREATE_NEW_VERSION_OF_TEMPLATE_STANDARD", "E9_US80_ROLLBACK_OF_THE_STANDARD_TEMPLATE", "E9_US81_PREVIEW_TEMPLATE_STANDARD", "E9_US24_EXPORT_TEMPLATE_DATA"),
            TemplateTypeEnum.STANDARD, Set.of("E9_US75_EDIT_TEMPLATE_METADATA_STANDARD", "E9_US76_CREATE_NEW_VERSION_OF_TEMPLATE_STANDARD", "E9_US80_ROLLBACK_OF_THE_STANDARD_TEMPLATE", "E9_US81_PREVIEW_TEMPLATE_STANDARD", "E9_US24_EXPORT_TEMPLATE_DATA"),
            TemplateTypeEnum.COMPOSITION, Set.of("E9_US75_EDIT_TEMPLATE_METADATA_STANDARD", "E9_US77_CREATE_NEW_VERSION_OF_TEMPLATE_COMPOSED", "E9_US100_ROLL_BACK_OF_THE_COMPOSITION_TEMPLATE", "E9_US81_PREVIEW_TEMPLATE_STANDARD", "E9_US24_EXPORT_TEMPLATE_DATA")
    );


    private final Map<ResourceTypeEnum, Set<String>> allowedResourcePermissions = Map.of(
            ResourceTypeEnum.IMAGE, Set.of("E8_US66_DELETE_RESOURCE_IMAGE", "E8_US62_CREATE_NEW_VERSION_OF_RESOURCE_IMAGE", "E8_US55_EDIT_RESOURCE_METADATA_IMAGE", "E8_US65_ROLL_BACK_OF_THE_RESOURCE_IMAGE"),
            ResourceTypeEnum.FONT, Set.of("E8_US66_1_DELETE_RESOURCE_FONT", "E8_US62_1_CREATE_NEW_VERSION_OF_RESOURCE_FONT", "E8_US58_EDIT_RESOURCE_METADATA_FONT", "E8_US65_1_ROLL_BACK_OF_THE_RESOURCE_FONT"),
            ResourceTypeEnum.STYLESHEET, Set.of("E8_US66_2_DELETE_RESOURCE_STYLESHEET", "E8_US63_CREATE_NEW_VERSION_OF_RESOURCE_STYLESHEET", "E8_US61_EDIT_RESOURCE_METADATA_STYLESHEET", "E8_US65_2_ROLL_BACK_OF_THE_RESOURCE_STYLESHEET")
    );

    private final String templateDeleteCompositionPermission = "E9_US127_DELETE_TEMPLATE_COMPOSITION";
    private final String templateDeleteStandardPermission = "E9_US126_DELETE_TEMPLATE_STANDARD";

    public PermissionCheckHandlerImpl(final UserService userService) {
        this.userService = userService;
    }

    public Set<String> getPermissionsByResource(final ResourceModelWithRoles resourceModel, final String email) {
        final UserEntity userEntity = userService.findActiveUserByEmail(email);
        final Set<String> userNames = userEntity.getRoles().stream().map(roleEntity -> roleEntity.getName()).collect(Collectors.toSet());
        final Set<String> permissions = new HashSet<>();

        if (isUserAdmin(userNames)) {
            permissions.addAll(allowedResourcePermissions.get(resourceModel.getType()));
        } else {
            permissions.addAll(getPermissions(resourceModel, userEntity));
        }
        return permissions;
    }

    public Set<String> getPermissionsByResource(final ResourceEntity resourceEntity, final String email) {
        final UserEntity userEntity = userService.findActiveUserByEmail(email);
        final Set<String> userNames = userEntity.getRoles().stream().map(roleEntity -> roleEntity.getName()).collect(Collectors.toSet());
        final Set<String> permissions = new HashSet<>();

        if (isUserAdmin(userNames)) {
            permissions.addAll(allowedResourcePermissions.get(resourceEntity.getType()));
        } else {
            permissions.addAll(getPermissions(resourceEntity, userEntity));
        }
        return permissions;
    }

    public Set<String> getPermissionsByDataCollection(final DataCollectionModelWithRoles dataCollectionModel, final String email) {
        final UserEntity userEntity = userService.findActiveUserByEmail(email);
        final Set<String> userNames = userEntity.getRoles().stream().map(roleEntity -> roleEntity.getName()).collect(Collectors.toSet());

        return isUserAdmin(userNames)
                ? dataCollectionPermissions
                : getPermissions(dataCollectionModel, userEntity);

    }

    public Set<String> getPermissionsByDataCollection(final DataCollectionEntity dataCollectionEntity, final String email) {
        final UserEntity userEntity = userService.findByEmail(email);
        final Set<String> userNames = userEntity.getRoles().stream().map(roleEntity -> roleEntity.getName()).collect(Collectors.toSet());

        return isUserAdmin(userNames)
                ? dataCollectionPermissions
                : getPermissions(dataCollectionEntity, userEntity);
    }

    public Set<String> getPermissionsByTemplate(final TemplateModelWithRoles templateModel, final String email) {
        final UserEntity userEntity = userService.findActiveUserByEmail(email);
        final Set<String> userNames = userEntity.getRoles().stream().map(roleEntity -> roleEntity.getName()).collect(Collectors.toSet());
        final Set<String> permissions = new HashSet<>();
        if (isUserAdmin(userNames)) {
            permissions.addAll(allowedTemplatePermissions.get(templateModel.getType()));
            if (templateModel.getType().equals(TemplateTypeEnum.COMPOSITION)) {
                permissions.add(templateDeleteCompositionPermission);
            } else {
                permissions.add(templateDeleteStandardPermission);
            }
        } else {
            permissions.addAll(getPermissions(templateModel, userEntity));
        }
        return permissions;
    }

    public Set<String> getPermissionsByTemplate(final TemplateEntity templateEntity, final String email) {
        final UserEntity userEntity = userService.findActiveUserByEmail(email);
        final Set<String> userNames = userEntity.getRoles().stream().map(roleEntity -> roleEntity.getName()).collect(Collectors.toSet());
        final Set<String> permissions = new HashSet<>();
        if (isUserAdmin(userNames)) {
            permissions.addAll(allowedTemplatePermissions.get(templateEntity.getType()));
            if (templateEntity.getType().equals(TemplateTypeEnum.COMPOSITION)) {
                permissions.add(templateDeleteCompositionPermission);
            } else {
                permissions.add(templateDeleteStandardPermission);
            }
        } else {
            permissions.addAll(getPermissions(templateEntity, userEntity));
        }
        return permissions;
    }

    private Set<String> getPermissions(final ResourceEntity resourceEntity, final UserEntity userEntity) {
        Set<String> permissions = new HashSet<>();
        final Set<RoleEntity> userRoles = userEntity.getRoles();
        final Set<RoleEntity> resourceRoles = resourceEntity.getAppliedRoles();
        final Set<RoleEntity> sameRoles = getSameRoles(userRoles, resourceRoles);
        final Set<RoleEntity> differentRoles = getDifferentRoles(userRoles, sameRoles);
        if (!sameRoles.isEmpty()) {
            permissions.addAll(getAllPermissions(userRoles, resourceRoles, differentRoles, resourceEntity.getType().toString()));
        } else {
            final Set<String> userPermissions = getAllPermissionsByUserRoles(userRoles);
            final Set<String> entityPermissions = allowedResourcePermissions.get(resourceEntity.getType());
            permissions = entityPermissions.stream().filter(userPermissions::contains).collect(Collectors.toSet());
        }
        return permissions;
    }

    private Set<String> getPermissions(final ResourceModelWithRoles resourceModel, final UserEntity userEntity) {
        Set<String> permissions = new HashSet<>();
        final Set<RoleEntity> userRoles = userEntity.getRoles();
        final Set<RoleDTO> resourceRoles = resourceModel.getAppliedRoles();
        final Set<RoleEntity> sameRoles = getSameRolesDto(userRoles, resourceRoles);
        final Set<RoleEntity> differentRoles = getDifferentRoles(userRoles, sameRoles);
        if (!sameRoles.isEmpty()) {
            permissions.addAll(getAllPermissionsWithDto(userRoles, resourceRoles, differentRoles, resourceModel.getType().toString()));
        } else {
            final Set<String> userPermissions = getAllPermissionsByUserRoles(userRoles);
            final Set<String> entityPermissions = allowedResourcePermissions.get(resourceModel.getType());
            permissions = entityPermissions.stream().filter(userPermissions::contains).collect(Collectors.toSet());
        }
        return permissions;
    }

    private Set<String> getPermissions(final DataCollectionEntity dataCollectionEntity, final UserEntity userEntity) {
        Set<String> permissions = new HashSet<>();
        final Set<RoleEntity> userRoles = userEntity.getRoles();
        final Set<RoleEntity> dataCollectionRoles = dataCollectionEntity.getAppliedRoles();
        final Set<RoleEntity> sameRoles = getSameRoles(userRoles, dataCollectionRoles);
        final Set<RoleEntity> differentRoles = getDifferentRoles(userRoles, sameRoles);
        if (!sameRoles.isEmpty()) {
            permissions.addAll(getAllPermissions(userRoles, dataCollectionRoles, differentRoles, dataCollectionEntity.getType().toString()));
        } else {
            final Set<String> userPermissions = getAllPermissionsByUserRoles(userRoles);
            permissions = dataCollectionPermissions.stream().filter(userPermissions::contains).collect(Collectors.toSet());
        }
        return permissions;
    }

    private Set<String> getPermissions(final DataCollectionModelWithRoles dataCollectionModel, final UserEntity userEntity) {
        Set<String> permissions = new HashSet<>();
        final Set<RoleEntity> userRoles = userEntity.getRoles();
        final Set<RoleDTO> dataCollectionRoles = dataCollectionModel.getAppliedRoles();
        final Set<RoleEntity> sameRoles = getSameRolesDto(userRoles, dataCollectionRoles);
        final Set<RoleEntity> differentRoles = getDifferentRoles(userRoles, sameRoles);
        if (!sameRoles.isEmpty()) {
            permissions.addAll(getAllPermissionsWithDto(userRoles, dataCollectionRoles, differentRoles, dataCollectionModel.getType().toString()));
        } else {
            final Set<String> userPermissions = getAllPermissionsByUserRoles(userRoles);
            permissions = dataCollectionPermissions.stream().filter(userPermissions::contains).collect(Collectors.toSet());
        }
        return permissions;
    }

    private Set<String> getPermissions(final TemplateEntity templateEntity, final UserEntity userEntity) {
        Set<String> permissions = new HashSet<>();
        final Set<RoleEntity> userRoles = userEntity.getRoles();
        final Set<RoleEntity> templateRoles = templateEntity.getAppliedRoles();
        final Set<RoleEntity> sameRoles = getSameRoles(userRoles, templateRoles);
        final Set<RoleEntity> differentRoles = getDifferentRoles(userRoles, sameRoles);

        if (!sameRoles.isEmpty()) {
            permissions.addAll(getAllPermissions(userRoles, templateRoles, differentRoles, templateEntity.getType().toString()));
        } else {
            final Set<String> userPermissions = getAllPermissionsByUserRoles(userRoles);
            permissions = allowedTemplatePermissions.get(templateEntity.getType()).stream().filter(userPermissions::contains).collect(Collectors.toSet());
        }
        addTemplateDeletePermissions(userRoles, permissions, templateEntity.getType());
        return permissions;
    }

    private Set<String> getPermissions(final TemplateModelWithRoles templateModel, final UserEntity userEntity) {
        Set<String> permissions = new HashSet<>();
        final Set<RoleEntity> userRoles = userEntity.getRoles();
        final Set<RoleDTO> templateRoles = templateModel.getAppliedRoles();
        final Set<RoleEntity> sameRoles = getSameRolesDto(userRoles, templateRoles);
        final Set<RoleEntity> differentRoles = getDifferentRoles(userRoles, sameRoles);
        if (!sameRoles.isEmpty()) {
            permissions.addAll(getAllPermissionsWithDto(userRoles, templateRoles, differentRoles, templateModel.getType().toString()));
        } else {
            final Set<String> userPermissions = getAllPermissionsByUserRoles(userRoles);
            permissions = allowedTemplatePermissions.get(templateModel.getType()).stream().filter(userPermissions::contains).collect(Collectors.toSet());
        }
        addTemplateDeletePermissions(userRoles, permissions, templateModel.getType());
        return permissions;
    }

    private Set<String> addTemplateDeletePermissions(final Set<RoleEntity> userRoles, final Set<String> permissions, final TemplateTypeEnum typeEnum) {
        for (final RoleEntity roleEntity : userRoles) {
            for (final PermissionEntity permissionEntity : roleEntity.getPermissions()) {
                if (TemplateTypeEnum.COMPOSITION.equals(typeEnum) && permissionEntity.getName().equals(templateDeleteCompositionPermission)) {
                    permissions.add(templateDeleteCompositionPermission);
                }
                if (!TemplateTypeEnum.COMPOSITION.equals(typeEnum) && permissionEntity.getName().equals(templateDeleteStandardPermission)) {
                    permissions.add(templateDeleteStandardPermission);
                }
            }
        }
        return permissions;
    }

    private Set<RoleEntity> getSameRolesDto(final Set<RoleEntity> userRoles, final Set<RoleDTO> entityRoles) {
        final Set<RoleEntity> roleEntitySet = new HashSet<>();
        for (final RoleEntity roleEntity : userRoles) {
            for (final RoleDTO roleDTO : entityRoles) {
                if (roleEntity.getName().equals(roleDTO.getName())) {
                    roleEntitySet.add(roleEntity);
                }
            }
        }
        return roleEntitySet;
    }

    private Set<RoleEntity> getSameRoles(final Set<RoleEntity> userRoles, final Set<RoleEntity> entityRoles) {
        final Set<RoleEntity> roleEntitySet = new HashSet<>();
        for (final RoleEntity userRole : userRoles) {
            for (final RoleEntity entityRole : entityRoles) {
                if (userRole.getName().equals(entityRole.getName())) {
                    roleEntitySet.add(userRole);
                }
            }
        }
        return roleEntitySet;
    }

    private Set<String> getAllPermissionsByUserRoles(final Set<RoleEntity> roleEntitySet) {
        final Set<String> userPermissions = new HashSet<>();
        for (final RoleEntity roleEntity : roleEntitySet) {
            for (final PermissionEntity permissionEntity : roleEntity.getPermissions()) {
                userPermissions.add(permissionEntity.getName());
            }
        }
        return userPermissions;
    }

    private Set<String> getAllPermissions(final Set<RoleEntity> userRoles, final Set<RoleEntity> entityRoles, final Set<RoleEntity> differentRoles, String type) {
        final Set<String> permissions = new HashSet<>();
        for (final RoleEntity userRole : userRoles) {
            for (final RoleEntity entityRole : entityRoles) {
                if (userRole.getName().equals(entityRole.getName())) {
                    for (final PermissionEntity permissionEntity : entityRole.getPermissions()) {
                        permissions.add(permissionEntity.getName());
                    }
                }
            }
        }
        final Set<String> allowedPermissions = getAllowedPermissionsByType(type);
        for (final RoleEntity differentRole : differentRoles) {
            for (final PermissionEntity permissionEntity : differentRole.getPermissions()) {
                addPermissionIfEquals(allowedPermissions, permissions, permissionEntity);
            }
        }
        return permissions;
    }

    private Set<String> getAllPermissionsWithDto(final Set<RoleEntity> userRoles, final Set<RoleDTO> entityRoles, final Set<RoleEntity> differentRoles, final String type) {
        final Set<String> permissions = new HashSet<>();
        for (final RoleEntity roleEntity : userRoles) {
            for (final RoleDTO entityRole : entityRoles) {
                if (roleEntity.getName().equals(entityRole.getName())) {
                    for (final PermissionDTO permissionEntity : entityRole.getPermissions()) {
                        permissions.add(permissionEntity.getName());
                    }
                }
            }
        }
        final Set<String> allowedPermissions = getAllowedPermissionsByType(type);
        for (final RoleEntity differentRole : differentRoles) {
            for (final PermissionEntity permissionEntity : differentRole.getPermissions()) {
                addPermissionIfEquals(allowedPermissions, permissions, permissionEntity);
            }
        }
        return permissions;
    }

    private boolean isUserAdmin(final Set<String> userRoleNames) {
        return userRoleNames.contains("GLOBAL_ADMINISTRATOR") || userRoleNames.contains("ADMINISTRATOR");
    }

    private Set<String> getAllowedPermissionsByType(final String type) {
        final Set<String> permissions = new HashSet<>();
        if (ResourceTypeEnum.IMAGE.toString().equals(type) || ResourceTypeEnum.FONT.toString().equals(type) || ResourceTypeEnum.STYLESHEET.toString().equals(type)) {
            permissions.addAll(allowedResourcePermissions.get(ResourceTypeEnum.valueOf(type)));
        }
        if (TemplateTypeEnum.FOOTER.toString().equals(type) || TemplateTypeEnum.COMPOSITION.toString().equals(type)
                || TemplateTypeEnum.HEADER.toString().equals(type) || TemplateTypeEnum.STANDARD.toString().equals(type)) {
            permissions.addAll(allowedTemplatePermissions.get(TemplateTypeEnum.valueOf(type)));
        }
        if (DataCollectionType.JSON.toString().equals(type)) {
            permissions.addAll(dataCollectionPermissions);
        }
        return permissions;
    }

    private Set<RoleEntity> getDifferentRoles(final Set<RoleEntity> userRoles, final Set<RoleEntity> sameRoles) {
        final Set<RoleEntity> differentRoles = new HashSet<>(userRoles);
        differentRoles.removeIf(roleEntity -> {
            return sameRoles.stream().anyMatch(sameRole -> sameRole.getName().equals(roleEntity.getName()));
        });

        return differentRoles;
    }

    private void addPermissionIfEquals(final Set<String> allowedPermissions, final Set<String> permissions, final PermissionEntity permissionEntity) {
        for (final String permission : allowedPermissions) {
            if (permissionEntity.getName().equals(permission)) {
                permissions.add(permission);
            }
        }
    }

}

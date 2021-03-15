package com.itextpdf.dito.manager.component.security.impl;


import com.itextpdf.dito.manager.component.security.PermissionCheckHandler;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PermissionCheckHandlerImpl implements PermissionCheckHandler {

    private UserService userService;

    private final Set<String> dataCollectionPermissions = Set.of("E6_US34_EDIT_DATA_COLLECTION_METADATA", "E6_US35_CREATE_A_NEW_VERSION_OF_DATA_COLLECTION_USING_JSON",
            "E6_US37_ROLL_BACK_OF_THE_DATA_COLLECTION", "E6_US38_DELETE_DATA_COLLECTION", "E7_US44_CREATE_NEW_DATA_SAMPLE_BASED_ON_JSON_FILE",
            "E7_US47_EDIT_SAMPLE_METADATA", "E7_US48_CREATE_NEW_VERSION_OF_DATA_SAMPLE", "E7_US50_DELETE_DATA_SAMPLE");

    private final Map<TemplateTypeEnum, List<String>> allowedTemplatePermissions = Map.of(
            TemplateTypeEnum.FOOTER, Arrays.asList("E9_US75_EDIT_TEMPLATE_METADATA_STANDARD", "E9_US76_CREATE_NEW_VERSION_OF_TEMPLATE_STANDARD", "E9_US80_ROLLBACK_OF_THE_STANDARD_TEMPLATE", "E9_US81_PREVIEW_TEMPLATE_STANDARD", "E9_US24_EXPORT_TEMPLATE_DATA"),
            TemplateTypeEnum.HEADER, Arrays.asList("E9_US75_EDIT_TEMPLATE_METADATA_STANDARD", "E9_US76_CREATE_NEW_VERSION_OF_TEMPLATE_STANDARD", "E9_US80_ROLLBACK_OF_THE_STANDARD_TEMPLATE", "E9_US81_PREVIEW_TEMPLATE_STANDARD", "E9_US24_EXPORT_TEMPLATE_DATA"),
            TemplateTypeEnum.STANDARD, Arrays.asList("E9_US75_EDIT_TEMPLATE_METADATA_STANDARD", "E9_US76_CREATE_NEW_VERSION_OF_TEMPLATE_STANDARD", "E9_US80_ROLLBACK_OF_THE_STANDARD_TEMPLATE", "E9_US81_PREVIEW_TEMPLATE_STANDARD", "E9_US24_EXPORT_TEMPLATE_DATA"),
            TemplateTypeEnum.COMPOSITION, Arrays.asList("E9_US75_EDIT_TEMPLATE_METADATA_STANDARD", "E9_US76_CREATE_NEW_VERSION_OF_TEMPLATE_STANDARD", "E9_US80_ROLLBACK_OF_THE_STANDARD_TEMPLATE", "E9_US81_PREVIEW_TEMPLATE_STANDARD", "E9_US24_EXPORT_TEMPLATE_DATA")
    );


    private final Map<ResourceTypeEnum, List<String>> allowedResourcePermissions = Map.of(
            ResourceTypeEnum.IMAGE, Arrays.asList("E8_US66_DELETE_RESOURCE_IMAGE", "E8_US62_CREATE_NEW_VERSION_OF_RESOURCE_IMAGE", "E8_US55_EDIT_RESOURCE_METADATA_IMAGE", "E8_US65_ROLL_BACK_OF_THE_RESOURCE_IMAGE"),
            ResourceTypeEnum.FONT, Arrays.asList("E8_US66_1_DELETE_RESOURCE_FONT", "E8_US62_1_CREATE_NEW_VERSION_OF_RESOURCE_FONT", "E8_US58_EDIT_RESOURCE_METADATA_FONT", "E8_US65_1_ROLL_BACK_OF_THE_RESOURCE_FONT"),
            ResourceTypeEnum.STYLESHEET, Arrays.asList("E8_US66_2_DELETE_RESOURCE_STYLESHEET", "E8_US63_CREATE_NEW_VERSION_OF_RESOURCE_STYLESHEET", "E8_US61_EDIT_RESOURCE_METADATA_STYLESHEET", "E8_US65_2_ROLL_BACK_OF_THE_RESOURCE_STYLESHEET")
    );

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
            permissions.addAll(new ArrayList<>(getPermissions(resourceModel, userEntity)));
        }
        return permissions;
    }

    public Set<String> getPermissionsByResource(final ResourceEntity resourceEntity, final String email) {
        final UserEntity userEntity = userService.findActiveUserByEmail(email);
        final Set<String> userNames = userEntity.getRoles().stream().map(roleEntity -> roleEntity.getName()).collect(Collectors.toSet());
        final Set<String> permissions = new HashSet<>();

        if (isUserAdmin(userNames)) {
            allowedResourcePermissions.get(resourceEntity.getType());
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
        } else {
            permissions.addAll(new ArrayList<>(getPermissions(templateModel, userEntity)));
        }
        return permissions;
    }

    public Set<String> getPermissionsByTemplate(final TemplateEntity templateEntity, final String email) {
        final UserEntity userEntity = userService.findActiveUserByEmail(email);
        final Set<String> userNames = userEntity.getRoles().stream().map(roleEntity -> roleEntity.getName()).collect(Collectors.toSet());
        final Set<String> permissions = new HashSet<>();
        if (isUserAdmin(userNames)) {
            permissions.addAll(allowedTemplatePermissions.get(templateEntity.getType()));
        } else {
            permissions.addAll(new ArrayList<>(getPermissions(templateEntity, userEntity)));
        }
        return permissions;
    }

    private Set<String> getPermissions(final ResourceEntity resourceEntity, final UserEntity userEntity) {
        Set<String> permissions = new HashSet<>();
        final Set<RoleEntity> userRoles = userEntity.getRoles();
        final Set<RoleEntity> resourceRoles = resourceEntity.getAppliedRoles();
        final Set<RoleEntity> sameRoles = getSameRoles(userRoles, resourceRoles);
        if (!sameRoles.isEmpty()) {
            permissions.addAll(getAllPermissions(userRoles, resourceRoles));
        } else {
            final Set<String> userPermissions = getAllPermissionsByUserRoles(userRoles);
            final List<String> entityPermissions = allowedResourcePermissions.get(resourceEntity.getType());
            permissions = entityPermissions.stream().filter(userPermissions::contains).collect(Collectors.toSet());
        }
        return permissions;
    }

    private Set<String> getPermissions(final ResourceModelWithRoles resourceModel, final UserEntity userEntity) {
        Set<String> permissions = new HashSet<>();
        final Set<RoleEntity> userRoles = userEntity.getRoles();
        final Set<RoleDTO> resourceRoles = resourceModel.getAppliedRoles();
        final Set<RoleEntity> sameRoles = getSameRolesDto(userRoles, resourceRoles);
        if (!sameRoles.isEmpty()) {
            permissions.addAll(getAllPermissionsWithDto(userRoles, resourceRoles));
        } else {
            final Set<String> userPermissions = getAllPermissionsByUserRoles(userRoles);
            final List<String> entityPermissions = allowedResourcePermissions.get(resourceModel.getType());
            permissions = entityPermissions.stream().filter(userPermissions::contains).collect(Collectors.toSet());
        }
        return permissions;
    }

    private Set<String> getPermissions(final DataCollectionEntity dataCollectionEntity, final UserEntity userEntity) {
        Set<String> permissions = new HashSet<>();
        final Set<RoleEntity> userRoles = userEntity.getRoles();
        final Set<RoleEntity> dataCollectionRoles = dataCollectionEntity.getAppliedRoles();
        final Set<RoleEntity> sameRoles = getSameRoles(userRoles, dataCollectionRoles);
        if (!sameRoles.isEmpty()) {
            permissions.addAll(getAllPermissions(userRoles, dataCollectionRoles));
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
        if (!sameRoles.isEmpty()) {
            permissions.addAll(getAllPermissionsWithDto(userRoles, dataCollectionRoles));
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
        if (!sameRoles.isEmpty()) {
            permissions.addAll(getAllPermissions(userRoles, templateRoles));
        } else {
            final Set<String> userPermissions = getAllPermissionsByUserRoles(userRoles);
            permissions = allowedTemplatePermissions.get(templateEntity.getType()).stream().filter(userPermissions::contains).collect(Collectors.toSet());
        }
        return permissions;
    }

    private Set<String> getPermissions(final TemplateModelWithRoles templateModel, final UserEntity userEntity) {
        Set<String> permissions = new HashSet<>();
        final Set<RoleEntity> userRoles = userEntity.getRoles();
        final Set<RoleDTO> templateRoles = templateModel.getAppliedRoles();
        final Set<RoleEntity> sameRoles = getSameRolesDto(userRoles, templateRoles);
        if (!sameRoles.isEmpty()) {
            permissions.addAll(getAllPermissionsWithDto(userRoles, templateRoles));
        } else {
            final Set<String> userPermissions = getAllPermissionsByUserRoles(userRoles);
            permissions = allowedTemplatePermissions.get(templateModel.getType()).stream().filter(userPermissions::contains).collect(Collectors.toSet());
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

    private Set<String> getAllPermissions(final Set<RoleEntity> userRoles, final Set<RoleEntity> entityRoles) {
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
        return permissions;
    }

    private Set<String> getAllPermissionsWithDto(final Set<RoleEntity> userRoles, final Set<RoleDTO> entityRoles) {
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
        return permissions;
    }

    private boolean isUserAdmin(final Set<String> userRoleNames) {
        return userRoleNames.contains("GLOBAL_ADMINISTRATOR") || userRoleNames.contains("ADMINISTRATOR");
    }
}

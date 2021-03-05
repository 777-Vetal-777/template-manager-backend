package com.itextpdf.dito.manager.component.security.impl;

import com.google.common.base.Predicates;
import com.itextpdf.dito.manager.component.security.PermissionHandler;
import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.dto.template.create.TemplateCreateRequestDTO;
import com.itextpdf.dito.manager.entity.PermissionEntity;
import com.itextpdf.dito.manager.entity.RoleEntity;
import com.itextpdf.dito.manager.entity.TemplateTypeEnum;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.exception.Base64DecodeException;
import com.itextpdf.dito.manager.exception.resource.NoSuchResourceTypeException;
import com.itextpdf.dito.manager.service.datacollection.DataCollectionService;
import com.itextpdf.dito.manager.service.resource.ResourceService;
import com.itextpdf.dito.manager.service.template.TemplateService;
import com.itextpdf.dito.manager.service.user.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

@Component
public class PermissionHandlerImpl implements PermissionHandler {
	
	private static final String E9_US73_CREATE_NEW_TEMPLATE_WITH_DATA_STANDARD = "E9_US73_CREATE_NEW_TEMPLATE_WITH_DATA_STANDARD";
	private static final String E9_US80_ROLLBACK_OF_THE_STANDARD_TEMPLATE = "E9_US80_ROLLBACK_OF_THE_STANDARD_TEMPLATE";
	private static final String E9_US72_CREATE_NEW_TEMPLATE_WITHOUT_DATA = "E9_US72_CREATE_NEW_TEMPLATE_WITHOUT_DATA";
	private static final String E9_US76_CREATE_NEW_VERSION_OF_TEMPLATE_STANDARD = "E9_US76_CREATE_NEW_VERSION_OF_TEMPLATE_STANDARD";
	private static final String E9_US126_DELETE_TEMPLATE_STANDARD = "E9_US126_DELETE_TEMPLATE_STANDARD";

    private final Map<TemplateTypeEnum, Predicate<String>> templateCommonPermissionsByType = Map.of(
            TemplateTypeEnum.STANDARD, Predicates.or(E9_US73_CREATE_NEW_TEMPLATE_WITH_DATA_STANDARD::equals, E9_US72_CREATE_NEW_TEMPLATE_WITHOUT_DATA::equals),
            TemplateTypeEnum.HEADER, Predicates.or(E9_US73_CREATE_NEW_TEMPLATE_WITH_DATA_STANDARD::equals, E9_US72_CREATE_NEW_TEMPLATE_WITHOUT_DATA::equals),
            TemplateTypeEnum.FOOTER, Predicates.or(E9_US73_CREATE_NEW_TEMPLATE_WITH_DATA_STANDARD::equals, E9_US72_CREATE_NEW_TEMPLATE_WITHOUT_DATA::equals),
            TemplateTypeEnum.COMPOSITION, Predicates.or("E9_US99_NEW_TEMPLATE_WITH_DATA_COMPOSITION"::equals, "E9_US102_CREATE_NEW_TEMPLATE_WITHOUT_DATA_COMPOSITION"::equals));

    private static final Map<TemplateTypeEnum, String> TEMPLATE_ROLLBACK_METADATA_PERMISSIONS =
            Map.of(TemplateTypeEnum.STANDARD, E9_US80_ROLLBACK_OF_THE_STANDARD_TEMPLATE,
                    TemplateTypeEnum.FOOTER, E9_US80_ROLLBACK_OF_THE_STANDARD_TEMPLATE,
                    TemplateTypeEnum.HEADER, E9_US80_ROLLBACK_OF_THE_STANDARD_TEMPLATE,
                    TemplateTypeEnum.COMPOSITION,"E9_US100_ROLL_BACK_OF_THE_COMPOSITION_TEMPLATE");

    private static final Map<TemplateTypeEnum, String> TEMPLATE_CREATE_NEW_VERSION_PERMISSIONS =
            Map.of(TemplateTypeEnum.STANDARD, E9_US76_CREATE_NEW_VERSION_OF_TEMPLATE_STANDARD,
                    TemplateTypeEnum.FOOTER, E9_US76_CREATE_NEW_VERSION_OF_TEMPLATE_STANDARD,
                    TemplateTypeEnum.HEADER, E9_US76_CREATE_NEW_VERSION_OF_TEMPLATE_STANDARD,
                    TemplateTypeEnum.COMPOSITION,"E9_US77_CREATE_NEW_VERSION_OF_TEMPLATE_COMPOSED");


    private static final Map<TemplateTypeEnum, Predicate<String>> TEMPLATE_DELETE_PERMISSIONS = Map.of(
            TemplateTypeEnum.STANDARD, E9_US126_DELETE_TEMPLATE_STANDARD::equals,
            TemplateTypeEnum.HEADER, E9_US126_DELETE_TEMPLATE_STANDARD::equals,
            TemplateTypeEnum.FOOTER, E9_US126_DELETE_TEMPLATE_STANDARD::equals,
            TemplateTypeEnum.COMPOSITION, "E9_US127_DELETE_TEMPLATE_COMPOSITION"::equals);

    private static final Map<ResourceTypeEnum, Predicate<String>> RESOURCE_VIEW_PERMISSIONS = Map.of(
            ResourceTypeEnum.IMAGE, "E8_US54_VIEW_RESOURCE_METADATA_IMAGE"::equals,
            ResourceTypeEnum.FONT, "E8_US57_VIEW_RESOURCE_METADATA_FONT"::equals,
            ResourceTypeEnum.STYLESHEET, "E8_US60_VIEW_RESOURCE_METADATA_STYLESHEET"::equals);

    private static final Map<ResourceTypeEnum, Predicate<String>> RESOURCE_CREATE_PERMISSIONS = Map.of(
            ResourceTypeEnum.IMAGE, "E8_US53_CREATE_NEW_RESOURCE_IMAGE"::equals,
            ResourceTypeEnum.STYLESHEET, "E8_US59_CREATE_NEW_RESOURCE_STYLESHEET"::equals,
            ResourceTypeEnum.FONT, "E8_US56_CREATE_NEW_RESOURCE_FONT"::equals);

    private static final Map<ResourceTypeEnum, String> RESOURCE_CREATE_NEW_VERSION_PERMISSIONS =
            Map.of(ResourceTypeEnum.IMAGE, "E8_US62_CREATE_NEW_VERSION_OF_RESOURCE_IMAGE",
                    ResourceTypeEnum.STYLESHEET, "E8_US63_CREATE_NEW_VERSION_OF_RESOURCE_STYLESHEET",
                    ResourceTypeEnum.FONT, "E8_US62_1_CREATE_NEW_VERSION_OF_RESOURCE_FONT");

    private static final Map<ResourceTypeEnum, String> RESOURCE_EDIT_METADATA_PERMISSIONS =
            Map.of(ResourceTypeEnum.IMAGE, "E8_US55_EDIT_RESOURCE_METADATA_IMAGE",
                    ResourceTypeEnum.STYLESHEET, "E8_US61_EDIT_RESOURCE_METADATA_STYLESHEET",
                    ResourceTypeEnum.FONT, "E8_US58_EDIT_RESOURCE_METADATA_FONT");

    private static final Map<ResourceTypeEnum, String> RESOURCE_DELETE_PERMISSIONS =
            Map.of(ResourceTypeEnum.IMAGE, "E8_US66_DELETE_RESOURCE_IMAGE",
                    ResourceTypeEnum.STYLESHEET, "E8_US66_2_DELETE_RESOURCE_STYLESHEET",
                    ResourceTypeEnum.FONT, "E8_US66_1_DELETE_RESOURCE_FONT");

    private static final Map<ResourceTypeEnum, String> RESOURCE_ROLLBACK_PERMISSIONS =
            Map.of(ResourceTypeEnum.IMAGE, "E8_US65_ROLL_BACK_OF_THE_RESOURCE_IMAGE",
                    ResourceTypeEnum.STYLESHEET, "E8_US65_2_ROLL_BACK_OF_THE_RESOURCE_STYLESHEET",
                    ResourceTypeEnum.FONT, "E8_US65_1_ROLL_BACK_OF_THE_RESOURCE_FONT");

    private final UserService userService;
    private final TemplateService templateService;
    private final DataCollectionService dataCollectionService;
    private final ResourceService resourceService;

    public PermissionHandlerImpl(final UserService userService,
                                 final TemplateService templateService,
                                 final DataCollectionService dataCollectionService,
                                 final ResourceService resourceService) {
        this.userService = userService;
        this.templateService = templateService;
        this.dataCollectionService = dataCollectionService;
        this.resourceService = resourceService;
    }

    @Override
    public String decodeBase64(final String name) {
        try {
            return new String(Base64.getUrlDecoder().decode(name));
        } catch (IllegalArgumentException ex) {
            throw new Base64DecodeException(name);
        }
    }

    //  Resources
    @Override
    public boolean checkResourceCommonPermissionByType(final Authentication authentication,
                                                       final String type) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(RESOURCE_VIEW_PERMISSIONS.get(fromPluralNameOrParse(type)));
    }

    @Override
    public boolean checkResourceCreatePermissionByType(final Authentication authentication,
                                                       final String type) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(RESOURCE_CREATE_PERMISSIONS.get(fromPluralNameOrParse(type)));
    }

    //  Templates
    @Override
    public boolean checkTemplateDeletePermissions(final Authentication authentication, final String templateName) {
        final TemplateEntity templateEntity = templateService.get(templateName);
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(TEMPLATE_DELETE_PERMISSIONS.get(templateEntity.getType()));
    }

    @Override
    public boolean checkTemplateCommonPermissionByType(final Authentication authentication,
                                                       final TemplateCreateRequestDTO request) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(templateCommonPermissionsByType.get(request.getType()));
    }

    private ResourceTypeEnum fromPluralNameOrParse(final String type) {
        return Optional.ofNullable(ResourceTypeEnum.getFromPluralName(type))
                .orElseGet(() -> {
                    try {
                        return ResourceTypeEnum.valueOf(type);
                    } catch (IllegalArgumentException e) {
                        throw new NoSuchResourceTypeException(type);
                    }
                });
    }

    @Override
    public boolean checkResourceCreateVersionPermissions(final String email, final String resourceType, final String resourceName) {
        final ResourceTypeEnum type = fromPluralNameOrParse(resourceType);

        return checkResourcePermissions(email, type, resourceName, RESOURCE_CREATE_NEW_VERSION_PERMISSIONS.get(type));
    }

    @Override
    public boolean checkResourceEditMetadataPermissions(final String email, final String resourceType, final String resourceName) {
        final ResourceTypeEnum type = fromPluralNameOrParse(resourceType);

        return checkResourcePermissions(email, type, resourceName, RESOURCE_EDIT_METADATA_PERMISSIONS.get(type));
    }

    @Override
    public boolean checkResourceDeletePermissions(final String email, final String resourceType, final String resourceName) {
        final ResourceTypeEnum type = fromPluralNameOrParse(resourceType);

        return checkResourcePermissions(email, type, resourceName, RESOURCE_DELETE_PERMISSIONS.get(type));
    }


    @Override
    public boolean checkResourceRollbackPermissions(final String email, final String resourceType, final String resourceName) {
        final ResourceTypeEnum type = fromPluralNameOrParse(resourceType);

        return checkResourcePermissions(email, type, resourceName, RESOURCE_ROLLBACK_PERMISSIONS.get(type));
    }

    @Override
    public boolean checkPermissionsByUser(final String email, final String permission) {
        final UserEntity userEntity = userService.findActiveUserByEmail(email);
        final List<RoleEntity> roles = new ArrayList<>(userEntity.getRoles());
        boolean existPermission = false;
        for (final RoleEntity roleEntity : roles) {
            final Set<PermissionEntity> permissionEntities = roleEntity.getPermissions();
            for (final PermissionEntity permissionEntity : permissionEntities) {
                if (permissionEntity.getName().equals(permission)) {
                    existPermission = true;
                    break;
                }
            }
        }
        return existPermission;
    }

    @Override
    public boolean checkTemplateRollbackPermissions(final String email, final String templateName) {
        final TemplateEntity templateEntity = templateService.get(templateName);
        final TemplateTypeEnum type = templateEntity.getType();
        final UserEntity userEntity = userService.findByEmail(email);

        return checkTemplatePermissions(userEntity, templateEntity, TEMPLATE_ROLLBACK_METADATA_PERMISSIONS.get(type));
    }

    @Override
    public boolean checkTemplateCreateVersionPermission(final Authentication authentication, final String templateName) {
        final TemplateEntity templateEntity = templateService.get(templateName);
        final TemplateTypeEnum type = templateEntity.getType();
        final UserEntity userEntity = userService.findByEmail(authentication.getName());

        return checkTemplatePermissions(userEntity, templateEntity, TEMPLATE_CREATE_NEW_VERSION_PERMISSIONS.get(type));
    }

    @Override
    public boolean checkTemplateCreateVersionPermission(final UserEntity userEntity, final TemplateEntity templateEntity) {
        final TemplateTypeEnum type = templateEntity.getType();
        return checkTemplatePermissions(userEntity, templateEntity, TEMPLATE_CREATE_NEW_VERSION_PERMISSIONS.get(type));
    }

    private boolean checkResourcePermissions(final String email, final ResourceTypeEnum resourceType, final String resourceName, final String checkingPermission) {
        final ResourceEntity resourceEntity = resourceService.getResource(resourceName, resourceType);
        final UserEntity userEntity = userService.findActiveUserByEmail(email);

        return checkUserPermissions(userEntity, resourceEntity.getAppliedRoles(), checkingPermission);
    }

    @Override
    public boolean checkDataCollectionPermissions(final String email, final String dataCollectionName, final String checkingPermission) {
        final DataCollectionEntity dataCollectionEntity = dataCollectionService.get(dataCollectionName);
        final UserEntity userEntity = userService.findActiveUserByEmail(email);

        return checkUserPermissions(userEntity, dataCollectionEntity.getAppliedRoles(), checkingPermission);
    }

    @Override
    public boolean checkTemplatePermissions(final UserEntity userEntity, final TemplateEntity templateEntity, final String checkingPermission) {
        return checkUserPermissions(userEntity, templateEntity.getAppliedRoles(), checkingPermission);
    }

    @Override
    public boolean checkTemplatePermissions(final Authentication authentication, final String templateName, final String checkingPermission) {
        final TemplateEntity templateEntity = templateService.get(templateName);
        final UserEntity userEntity = userService.findActiveUserByEmail(authentication.getName());

        return checkTemplatePermissions(userEntity, templateEntity, checkingPermission);
    }

    private Set<RoleEntity> retrieveEntityAppliedRoles(final Set<RoleEntity> entityAppliedRoles, final Set<RoleEntity> userMasterRoles) {
        final Map<String, RoleEntity> appliedRoles = entityAppliedRoles.stream().collect(toMap(RoleEntity::getName, role -> role));
        userMasterRoles.forEach(role -> appliedRoles.putIfAbsent(role.getName(), role));
        return new HashSet<>(appliedRoles.values());
    }

    private Set<String> retrieveSetOfRoleNames(final Set<RoleEntity> roleEntities) {
        return roleEntities.stream().map(RoleEntity::getName).collect(Collectors.toSet());
    }

    private boolean isUserAdmin(final Set<String> userRoleNames) {
        return userRoleNames.contains("GLOBAL_ADMINISTRATOR") || userRoleNames.contains("ADMINISTRATOR");
    }

    boolean checkUserPermissions(final UserEntity userEntity,
                                 final Set<RoleEntity> entityRoles,
                                 final String requiredPermission) {
        final Set<String> userRoleNames = retrieveSetOfRoleNames(userEntity.getRoles());
        final Set<RoleEntity> entityAppliedRoles =  retrieveEntityAppliedRoles(entityRoles, userEntity.getRoles());
        boolean isPermissionRolePresented = false;

        if (!isUserAdmin(userRoleNames) && !entityAppliedRoles.isEmpty()) {
            final Set<String> entityAppliedRolesWithRequiredPermission = retrieveSetOfRoleNamesFilteredByPermission(entityAppliedRoles, requiredPermission);
            for (final String role : entityAppliedRolesWithRequiredPermission) {
                if (userRoleNames.contains(role)) {
                    isPermissionRolePresented = true;
                    break;
                }
            }
        } else {
            isPermissionRolePresented = true;
        }

        return isPermissionRolePresented;
    }

    private Set<String> retrieveSetOfRoleNamesFilteredByPermission(final Set<RoleEntity> roleEntities,
                                                                   final String permission) {
        return roleEntities.stream().filter(roleEntity -> roleEntity.getPermissions().stream()
                .anyMatch(permissionEntity -> permissionEntity.getName().equals(permission)))
                .map(RoleEntity::getName).collect(
                        Collectors.toSet());
    }
}

package com.itextpdf.dito.manager.component.security;

import com.google.common.base.Predicates;
import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.dto.template.create.TemplateCreateRequestDTO;
import com.itextpdf.dito.manager.entity.TemplateTypeEnum;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

@Component("permissionHandler")
public class PermissionAccessHandler {

    private final Map<ResourceTypeEnum, Predicate<String>> resourceCommonPermissionsByType = Map.of(
            ResourceTypeEnum.IMAGE, Predicates.or("E8_US54_VIEW_RESOURCE_METADATA_IMAGE"::equals, "E8_US101_RESOURCE_NAVIGATION_MENU"::equals),
            ResourceTypeEnum.FONT, "E8_US57_VIEW_RESOURCE_METADATA_FONT"::equals,
            ResourceTypeEnum.STYLESHEET, "E8_US60_VIEW_RESOURCE_METADATA_STYLESHEET"::equals);

    private final Map<ResourceTypeEnum, Predicate<String>> resourceCreatePermissionsByType = Map.of(
            ResourceTypeEnum.IMAGE, "E8_US54_VIEW_RESOURCE_METADATA_IMAGE"::equals,
            ResourceTypeEnum.STYLESHEET, "E8_US60_VIEW_RESOURCE_METADATA_STYLESHEET"::equals);

    private final Map<TemplateTypeEnum, Predicate<String>> templateCommonPermissionsByType = Map.of(
            TemplateTypeEnum.STANDARD, "E9_US73_CREATE_NEW_TEMPLATE_WITH_DATA_STANDARD"::equals,
            TemplateTypeEnum.HEADER, "E9_US99_NEW_TEMPLATE_WITH_DATA_COMPOSITION"::equals,
            TemplateTypeEnum.FOOTER, "E9_US72_CREATE_NEW_TEMPLATE_WITHOUT_DATA"::equals);

    private final Map<ResourceTypeEnum, Predicate<String>> resourceEditPermissionsByType = Map.of(
            ResourceTypeEnum.IMAGE, "E8_US55_EDIT_RESOURCE_METADATA_IMAGE"::equals,
            ResourceTypeEnum.FONT, "E8_US58_EDIT_RESOURCE_METADATA_FONT"::equals,
            ResourceTypeEnum.STYLESHEET, "E8_US61_EDIT_RESOURCE_METADATA_STYLESHEET"::equals
    );

    private final Map<ResourceTypeEnum, Predicate<String>> resourceCreateVersionPermissionsByType = Map.of(
            ResourceTypeEnum.IMAGE, "E8_US62_CREATE_NEW_VERSION_OF_RESOURCE_IMAGE"::equals,
            ResourceTypeEnum.STYLESHEET, "E8_US63_CREATE_NEW_VERSION_OF_RESOURCE_STYLESHEET"::equals
    );

    //  Resources
    public boolean checkResourceCommonPermissionByType(Authentication authentication, String type) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(resourceCommonPermissionsByType.get(fromPluralNameOrParse(type)));
    }

    public boolean checkResourceCreateVersionPermissionByType(Authentication authentication, String type) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(resourceCreateVersionPermissionsByType.get(fromPluralNameOrParse(type)));
    }

    public boolean checkResourceCreatePermissionByType(Authentication authentication, String type) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(resourceCreatePermissionsByType.get(fromPluralNameOrParse(type)));
    }


    public boolean checkResourceDeletePermissionByType(Authentication authentication, String type) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("E8_US66_DELETE_RESOURCE_IMAGE"::equals);
    }

    public boolean checkResourceEditPermissionByType(Authentication authentication, String type) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(resourceEditPermissionsByType.get(fromPluralNameOrParse(type)));
    }

    //  Templates
    public boolean checkTemplateCommonPermissionByType(Authentication authentication, String type) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(templateCommonPermissionsByType.get(type));
    }

    public boolean checkTemplateCommonPermissionByType(Authentication authentication, TemplateCreateRequestDTO request) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(templateCommonPermissionsByType.get(request.getType()));
    }

    private ResourceTypeEnum fromPluralNameOrParse(String type) {
        return Optional.ofNullable(ResourceTypeEnum.getFromPluralName(type))
                .orElseGet(() -> ResourceTypeEnum.valueOf(type));
    }
}

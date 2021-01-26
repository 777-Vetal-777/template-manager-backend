package com.itextpdf.dito.manager.component.security;

import com.itextpdf.dito.manager.dto.template.create.TemplateCreateRequestDTO;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import org.springframework.security.core.Authentication;

public interface PermissionHandler {
    //  Resources
    boolean checkResourceCommonPermissionByType(Authentication authentication, String type);

    boolean checkResourceCreateVersionPermissionByType(Authentication authentication, String type);

    boolean checkResourceCreatePermissionByType(Authentication authentication, String type);

    boolean checkResourceDeletePermissionByType(Authentication authentication, String type);

    boolean checkResourceEditPermissionByType(Authentication authentication, String type);

    //  Templates
    boolean checkTemplateCommonPermissionByType(Authentication authentication, String type);

    boolean checkTemplateCommonPermissionByType(Authentication authentication, TemplateCreateRequestDTO request);

    boolean checkResourceCreateVersionPermissions(String email, String resourceType, String resourceName);

    boolean checkResourceEditMetadataPermissions(String email, String resourceType, String resourceName);

    boolean checkResourceDeletePermissions(String email, String resourceType, String resourceName);

    boolean checkDataCollectionPermissions(String email, String dataCollectionName, String checkingPermission);

    boolean checkTemplatePermissions(UserEntity userEntity, TemplateEntity templateEntity, String permission);

    boolean checkTemplatePermissions(String email, String templateName, String checkingPermission);
}

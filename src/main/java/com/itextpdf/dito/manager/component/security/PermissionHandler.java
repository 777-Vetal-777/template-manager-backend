package com.itextpdf.dito.manager.component.security;

import com.itextpdf.dito.manager.dto.template.create.TemplateCreateRequestDTO;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import org.springframework.security.core.Authentication;

public interface PermissionHandler {
    //  Resources
    boolean checkResourceCommonPermissionByType(Authentication authentication, String type);

    boolean checkResourceCreatePermissionByType(Authentication authentication, String type);

    boolean checkResourceCreateVersionPermissions(String email, String resourceType, String resourceName);

    boolean checkResourceEditMetadataPermissions(String email, String resourceType, String resourceName);

    boolean checkResourceDeletePermissions(String email, String resourceType, String resourceName);

    boolean checkResourceRollbackPermissions(String email, String resourceType, String resourceName);

    //  Templates
    boolean checkTemplateCommonPermissionByType(Authentication authentication, String type);

    boolean checkTemplateCommonPermissionByType(Authentication authentication, TemplateCreateRequestDTO request);

    boolean checkTemplatePermissions(UserEntity userEntity, TemplateEntity templateEntity, String permission);

    boolean checkTemplatePermissions(String email, String templateName, String checkingPermission);

     boolean checkTemplateRollbackPermissions(String email, String templateName);

     boolean checkTemplateCreateVersionPermission(Authentication authentication, String templateName);

     boolean checkTemplateCreateVersionPermission(final UserEntity userEntity, final TemplateEntity templateEntity);


        //  Data Collections
    boolean checkDataCollectionPermissions(String email, String dataCollectionName, String checkingPermission);

}

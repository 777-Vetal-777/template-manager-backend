package com.itextpdf.dito.manager.integration.editor.controller.template;

import com.itextpdf.dito.editor.server.common.core.descriptor.TemplateAddDescriptor;
import com.itextpdf.dito.editor.server.common.core.descriptor.TemplateDescriptor;
import com.itextpdf.dito.editor.server.common.core.descriptor.TemplateUpdateDescriptor;
import com.itextpdf.dito.manager.config.OpenApiConfig;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestPart;

import java.security.Principal;
import java.util.List;

public interface TemplateManagementController {
    // Endpoints naming is used from the strict requirements for editor integration.
    String CREATE_TEMPLATE_URL = "/workspace/{workspace-id}/templates";
    String TEMPLATE_LIST_URL = "/workspace/{workspace-id}/templates";
    String TEMPLATE_URL = "/templates/{template-id}";
    String TEMPLATE_DESCRIPTOR_URL = "/templates/{template-id}/descriptor";

    @PostMapping(value = CREATE_TEMPLATE_URL, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    TemplateDescriptor add(Principal principal,
                           @PathVariable("workspace-id") String workspaceId,
                           @RequestPart TemplateAddDescriptor descriptor,
                           @RequestPart byte[] data);

    @GetMapping(value = TEMPLATE_URL, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Operation(security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    byte[] get(@PathVariable("template-id") String templateId);

    @GetMapping(value = TEMPLATE_DESCRIPTOR_URL, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    TemplateDescriptor getDescriptor(@PathVariable("template-id") String templateId);

    @GetMapping(value = TEMPLATE_LIST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    List<TemplateDescriptor> getAllDescriptors(@PathVariable("workspace-id") String workspaceId);

    @PutMapping(value = TEMPLATE_URL, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    TemplateDescriptor update(Principal principal,
                              @PathVariable("template-id") String templateId,
                              @RequestPart(required = false) TemplateUpdateDescriptor descriptor,
                              @RequestPart byte[] data);

    @PreAuthorize("@permissionHandlerImpl.checkTemplateDeletePermissions(authentication, @permissionHandlerImpl.decodeBase64(#templateId))")
    @DeleteMapping(value = TEMPLATE_URL, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    TemplateDescriptor delete(@PathVariable("template-id") String templateId);
}

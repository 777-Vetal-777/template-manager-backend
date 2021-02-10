package com.itextpdf.dito.manager.integration.editor.controller.resource;

import com.itextpdf.dito.editor.server.common.core.descriptor.resource.ResourceLeafDescriptor;
import com.itextpdf.dito.manager.config.OpenApiConfig;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestPart;

import java.security.Principal;
import java.util.List;

public interface ResourceManagementController {
    String RESOURCE_URL = "/resources/{resource-id}";
    String WORKSPACE_RESOURCES_URL = "/workspace/{workspace-id}/resources";
    String CREATE_RESOURCE_URL = "/resources";

    @GetMapping(RESOURCE_URL)
	@Operation(security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    byte[] getResourceDirectoryContentById(@PathVariable("resource-id") String resourceId);

    @GetMapping(WORKSPACE_RESOURCES_URL)
	@Operation(security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    List<ResourceLeafDescriptor> getWorkspaceResources(@PathVariable("workspace-id") String workspaceId);

    @GetMapping(CREATE_RESOURCE_URL)
	@Operation(security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    List<ResourceLeafDescriptor> getResources();

    @PutMapping(RESOURCE_URL)
	@Operation(security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResourceLeafDescriptor createOrUpdate(Principal principal, @PathVariable("resource-id") String resourceId,
                                          @RequestPart ResourceLeafDescriptor descriptor, @RequestPart byte[] data);

    @PostMapping(CREATE_RESOURCE_URL)
	@Operation(security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    List<ResourceLeafDescriptor> add(Principal principal,
                                     @RequestPart ResourceLeafDescriptor descriptor, @RequestPart byte[] data);

    @DeleteMapping(RESOURCE_URL)
	@Operation(security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    void deleteResourceById(Principal principal, @PathVariable("resource-id") String resourceId);
}

package com.itextpdf.dito.manager.integration.editor.controller.resource;

import com.itextpdf.dito.editor.server.common.core.descriptor.resource.ResourceLeafDescriptor;
import com.itextpdf.dito.manager.config.OpenApiConfig;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "editor-resources",description = "editor integration API")
public interface ResourceManagementController {
    String RESOURCE_URL = "/resources/{resource-id}";
    String WORKSPACE_RESOURCES_URL = "/workspace/{workspace-id}/resources";
    String CREATE_RESOURCE_URL = "/resources";

    @GetMapping(value = RESOURCE_URL, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@Operation(summary = "Get resource", description = "returns stream of resource if it's not directory, otherwise list of leaf resources descriptors are returned",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE))
    @ApiResponse(responseCode = "404", description = "Resource not found by id", content = @Content)
    @ApiResponse(responseCode = "400", description = "Bad request, for example, null id is passed", content = @Content)
    byte[] getResourceDirectoryContentById(@PathVariable("resource-id") String resourceId);

    @GetMapping(value = WORKSPACE_RESOURCES_URL, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Get resources by workspace id", description = "Returns descriptors of resources from specified workspace", security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = ResourceLeafDescriptor.class))))
    @ApiResponse(responseCode = "404", description = "Workspace not found by id", content = @Content)
    @ApiResponse(responseCode = "400", description = "Bad request, for example, null id is passed", content = @Content)
    List<ResourceLeafDescriptor> getWorkspaceResources(@PathVariable("workspace-id") String workspaceId);

    @GetMapping(value = CREATE_RESOURCE_URL, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Get all resources from current workspace", description = "Returns a set of resources that are in the workspace ",security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = ResourceLeafDescriptor.class))))
    List<ResourceLeafDescriptor> getResources();

    @PutMapping(value = RESOURCE_URL, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Create or update resource",description = "updates existing resource or creates new one, returns resource descriptor",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResourceLeafDescriptor.class)))
    @ApiResponse(responseCode = "404", description = "Workspace not found by id", content = @Content)
    @ApiResponse(responseCode = "400", description = "Bad request, for example, null id is passed", content = @Content)
    ResourceLeafDescriptor createOrUpdate(Principal principal, @PathVariable("resource-id") String resourceId,
                                          @RequestPart ResourceLeafDescriptor descriptor, @RequestPart byte[] data);

    @PostMapping(value = CREATE_RESOURCE_URL, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@Operation(summary = "Create a new resource", description = "creates new resources, returns JSON list of resource descriptors", security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = ResourceLeafDescriptor.class))))
    @ApiResponse(responseCode = "404", description = "Workspace not found by id", content = @Content)
    @ApiResponse(responseCode = "400", description = "Bad request, for example, null id is passed", content = @Content)
    List<ResourceLeafDescriptor> add(Principal principal, @RequestPart ResourceLeafDescriptor descriptor, @RequestPart byte[] data);

    @DeleteMapping(value = RESOURCE_URL, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @PreAuthorize("hasAuthority('E8_US68_MANAGE_RESOURCE_PERMISSIONS_IMAGE')")
	@Operation(description = "Delete resource by id", summary = "deleting a resource by its identifier ", security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponse(responseCode = "404", description = "Template not found by id", content = @Content)
    @ApiResponse(responseCode = "400", description = "Bad request, for example, null id is passed", content = @Content)
    void deleteResourceById(Principal principal, @PathVariable("resource-id") String resourceId);
}

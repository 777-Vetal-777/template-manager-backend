package com.itextpdf.dito.manager.integration.editor.controller.resource;

import com.itextpdf.dito.editor.server.common.core.descriptor.resource.ResourceLeafDescriptor;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestPart;

public interface ResourceManagementController {
    String RESOURCE_URL = "/resources/{resource-id}";
    String WORKSPACE_RESOURCES_URL = "/workspace/{workspace-id}/resources";
    String CREATE_RESOURCE_URL = "/resources";

    @GetMapping(RESOURCE_URL)
    List<ResourceLeafDescriptor> getResourceDirectoryContentById(@PathVariable("resource-id") String resourceId);

    @GetMapping(WORKSPACE_RESOURCES_URL)
    List<ResourceLeafDescriptor> getWorkspaceResources(@PathVariable("workspace-id") String workspaceId);

    @PutMapping(RESOURCE_URL)
    ResourceLeafDescriptor createOrUpdate(Principal principal, @PathVariable("resource-id") String resourceId,
            @RequestPart ResourceLeafDescriptor descriptor, @RequestPart InputStream data) throws IOException;

    @PostMapping(CREATE_RESOURCE_URL)
    List<ResourceLeafDescriptor> add(Principal principal, @RequestPart ResourceLeafDescriptor descriptor, @RequestPart InputStream data)
            throws IOException;

    @DeleteMapping(RESOURCE_URL)
    void deleteResourceById(Principal principal, @PathVariable("resource-id") String resourceId);
}

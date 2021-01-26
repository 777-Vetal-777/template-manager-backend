package com.itextpdf.dito.manager.integration.editor.controller.resource;

import com.itextpdf.dito.editor.server.common.core.descriptor.resource.ResourceLeafDescriptor;

import java.io.InputStream;
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
    ResourceLeafDescriptor createOrUpdate(@PathVariable("resource-id") String resourceId,
            @RequestPart ResourceLeafDescriptor descriptor, @RequestPart InputStream data);

    @PostMapping(CREATE_RESOURCE_URL)
    List<ResourceLeafDescriptor> add(@RequestPart ResourceLeafDescriptor descriptor, @RequestPart InputStream data);

    @DeleteMapping(RESOURCE_URL)
    void deleteResourceById(@PathVariable("resource-id") String resourceId);
}

package com.itextpdf.dito.manager.integration.editor.controller.resource.impl;

import com.itextpdf.dito.editor.server.common.core.descriptor.resource.ResourceLeafDescriptor;
import com.itextpdf.dito.manager.integration.editor.controller.resource.ResourceManagementController;
import com.itextpdf.dito.manager.service.resource.ResourceService;

import java.io.InputStream;
import java.util.List;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResourceManagementControllerImpl implements ResourceManagementController {
    private final ResourceService resourceService;

    public ResourceManagementControllerImpl(final ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @Override
    public List<ResourceLeafDescriptor> getResourceDirectoryContentById(final String resourceId) {
        return null;
    }

    @Override
    public List<ResourceLeafDescriptor> getWorkspaceResources(final String workspaceId) {
        return null;
    }

    @Override
    public ResourceLeafDescriptor createOrUpdate(final String resourceId, ResourceLeafDescriptor descriptor,
            InputStream data) {
        return null;
    }

    @Override
    public List<ResourceLeafDescriptor> add(final ResourceLeafDescriptor descriptor, InputStream data) {
        return null;
    }

    @Override
    public void deleteResourceById(final String resourceId) {

    }
}

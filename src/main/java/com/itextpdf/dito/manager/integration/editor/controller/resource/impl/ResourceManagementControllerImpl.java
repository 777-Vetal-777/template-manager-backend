package com.itextpdf.dito.manager.integration.editor.controller.resource.impl;

import com.itextpdf.dito.editor.server.common.core.descriptor.resource.ResourceLeafDescriptor;
import com.itextpdf.dito.manager.controller.AbstractController;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.integration.editor.controller.resource.ResourceManagementController;
import com.itextpdf.dito.manager.integration.editor.dto.ResourceIdDTO;
import com.itextpdf.dito.manager.integration.editor.mapper.resource.ResourceLeafDescriptorMapper;
import com.itextpdf.dito.manager.service.resource.ResourceService;

import java.io.InputStream;
import java.security.Principal;
import java.util.List;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResourceManagementControllerImpl extends AbstractController implements ResourceManagementController {
    private final ResourceService resourceService;
    private final ResourceLeafDescriptorMapper resourceLeafDescriptorMapper;

    public ResourceManagementControllerImpl(final ResourceService resourceService,
            final ResourceLeafDescriptorMapper resourceLeafDescriptorMapper) {
        this.resourceService = resourceService;
        this.resourceLeafDescriptorMapper = resourceLeafDescriptorMapper;
    }

    @Override
    public List<ResourceLeafDescriptor> getResourceDirectoryContentById(final String resourceId) {
        final List<ResourceEntity> resourceEntities = resourceService.list();
        return resourceLeafDescriptorMapper.map(resourceEntities);
    }

    @Override
    public List<ResourceLeafDescriptor> getWorkspaceResources(final String workspaceId) {
        return getResourceDirectoryContentById(null);
    }

    @Override
    public ResourceLeafDescriptor createOrUpdate(final String resourceId, final ResourceLeafDescriptor descriptor,
            final InputStream data) {

        return null;
    }

    @Override
    public List<ResourceLeafDescriptor> add(final ResourceLeafDescriptor descriptor, final InputStream data) {
        return null;
    }

    @Override
    public void deleteResourceById(final Principal principal, final String resourceId) {
        final ResourceIdDTO resourceIdDTO = resourceLeafDescriptorMapper.map(resourceId);
        resourceService.delete(resourceIdDTO.getName(), resourceIdDTO.getType(), principal.getName());
    }
}

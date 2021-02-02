package com.itextpdf.dito.manager.integration.editor.controller.resource.impl;

import com.itextpdf.dito.editor.server.common.core.descriptor.resource.ResourceLeafDescriptor;
import com.itextpdf.dito.manager.controller.AbstractController;
import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.integration.editor.controller.resource.ResourceManagementController;
import com.itextpdf.dito.manager.integration.editor.dto.ResourceIdDTO;
import com.itextpdf.dito.manager.integration.editor.mapper.resource.ResourceLeafDescriptorMapper;
import com.itextpdf.dito.manager.integration.editor.service.resource.ResourceManagementService;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResourceManagementControllerImpl extends AbstractController implements ResourceManagementController {
    private final ResourceManagementService resourceManagementService;
    private final ResourceLeafDescriptorMapper resourceLeafDescriptorMapper;

    public ResourceManagementControllerImpl(final ResourceManagementService resourceManagementService,
            final ResourceLeafDescriptorMapper resourceLeafDescriptorMapper) {
        this.resourceManagementService = resourceManagementService;
        this.resourceLeafDescriptorMapper = resourceLeafDescriptorMapper;
    }

    @Override
    public InputStream getResourceDirectoryContentById(final String resourceId) {
        final ResourceIdDTO resourceIdDTO = resourceLeafDescriptorMapper.map(resourceId);
        return resourceManagementService.get(resourceIdDTO.getName(), resourceIdDTO.getType());
    }

    @Override
    public List<ResourceLeafDescriptor> getWorkspaceResources(final String workspaceId) {
        final List<ResourceEntity> resourceEntities = resourceManagementService.list();
        return resourceLeafDescriptorMapper.map(resourceEntities);
    }

    @Override
    public ResourceLeafDescriptor createOrUpdate(final Principal principal, final String resourceId,
            final ResourceLeafDescriptor descriptor,
            final InputStream data) throws IOException {
        final ResourceIdDTO resourceIdDTO = resourceLeafDescriptorMapper.map(resourceId);
        final String name = resourceIdDTO.getName();
        final ResourceTypeEnum type = resourceIdDTO.getType();
        final byte[] bytes = data.readAllBytes();
        final String email = principal.getName();
        final ResourceEntity resourceEntity = resourceManagementService
                .createNewVersion(name, type, bytes, name, email);
        return resourceLeafDescriptorMapper.map(resourceEntity);
    }

    @Override
    public List<ResourceLeafDescriptor> add(final Principal principal, final ResourceLeafDescriptor descriptor,
            final InputStream data) throws IOException {
        final ResourceEntity resourceEntity = resourceManagementService
                .create(descriptor, data.readAllBytes(), descriptor.getDisplayName(), principal.getName());
        return Collections.singletonList(resourceLeafDescriptorMapper.map(resourceEntity));
    }

    @Override
    public void deleteResourceById(final Principal principal, final String resourceId) {
        final ResourceIdDTO resourceIdDTO = resourceLeafDescriptorMapper.map(resourceId);
        resourceManagementService.delete(resourceIdDTO.getName(), resourceIdDTO.getType(), principal.getName());
    }
}

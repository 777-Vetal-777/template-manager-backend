package com.itextpdf.dito.manager.integration.editor.controller.resource.impl;

import com.itextpdf.dito.editor.server.common.core.descriptor.resource.ResourceLeafDescriptor;
import com.itextpdf.dito.manager.controller.AbstractController;
import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.integration.editor.controller.resource.ResourceManagementController;
import com.itextpdf.dito.manager.integration.editor.dto.ResourceIdDTO;
import com.itextpdf.dito.manager.integration.editor.mapper.resource.ResourceLeafDescriptorMapper;
import com.itextpdf.dito.manager.integration.editor.service.resource.ResourceManagementService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

@RestController
public class ResourceManagementControllerImpl extends AbstractController implements ResourceManagementController {
    private static final Logger log = LogManager.getLogger(ResourceManagementControllerImpl.class);
    private final ResourceManagementService resourceManagementService;
    private final ResourceLeafDescriptorMapper resourceLeafDescriptorMapper;

    public ResourceManagementControllerImpl(final ResourceManagementService resourceManagementService,
                                            final ResourceLeafDescriptorMapper resourceLeafDescriptorMapper) {
        this.resourceManagementService = resourceManagementService;
        this.resourceLeafDescriptorMapper = resourceLeafDescriptorMapper;
    }

    @Override
    public byte[] getResourceDirectoryContentById(final String resourceId) {
        log.info("Request to get resource file by resourceId id {}.", resourceId);
        final ResourceIdDTO resourceIdDTO = resourceLeafDescriptorMapper.map(resourceId);
        log.info("Response to get resource file by resourceId id {} processed.", resourceId);
        return resourceManagementService.get(resourceIdDTO.getName(), resourceIdDTO.getType(), resourceIdDTO.getSubName());
    }

    @Override
    public List<ResourceLeafDescriptor> getWorkspaceResources(final String workspaceId) {
        log.info("Request to get workspace resources file by workspace id {} received.", workspaceId);
        final List<ResourceEntity> resourceEntities = resourceManagementService.list();
        log.info("Response to get workspace resources file by workspace id {} processed. {} resources returned", workspaceId, resourceEntities.size());
        return resourceLeafDescriptorMapper.map(resourceEntities);
    }

    @Override
    public List<ResourceLeafDescriptor> getResources() {
        log.info("Request to get resources received.");
        return getWorkspaceResources(null);
    }

    @Override
    public ResourceLeafDescriptor createOrUpdate(final Principal principal, final String resourceId,
                                                 final ResourceLeafDescriptor descriptor,
                                                 final byte[] data) {
        log.info("Request to create or update resource by resource id {} received.", resourceId);
        final ResourceIdDTO resourceIdDTO = resourceLeafDescriptorMapper.map(resourceId);
        final String name = resourceIdDTO.getName();
        final ResourceTypeEnum type = resourceIdDTO.getType();
        final byte[] bytes = data;
        final String email = principal.getName();
        final ResourceEntity resourceEntity = resourceManagementService
                .createNewVersion(name, type, bytes, name, email);
        log.info("Response to create or update resource by resource id {} processed.", resourceId);
        return resourceLeafDescriptorMapper.map(resourceEntity);
    }

    @Override
    public List<ResourceLeafDescriptor> add(final Principal principal, final ResourceLeafDescriptor descriptor,
                                            final byte[] data) {
        log.info("Request to create resource with name {} received.", descriptor.getDisplayName());
        final ResourceEntity resourceEntity = resourceManagementService
                .create(descriptor, data, descriptor.getDisplayName(), principal.getName());
        log.info("Response to create resource with name {} processed. Resource created with id {}.", resourceEntity.getName(), resourceEntity.getId());
        return Collections.singletonList(resourceLeafDescriptorMapper.map(resourceEntity));
    }

    @Override
    public void deleteResourceById(final Principal principal, final String resourceId) {
        log.info("Request to delete resource with id {} received.", resourceId);
        final ResourceIdDTO resourceIdDTO = resourceLeafDescriptorMapper.map(resourceId);
        log.info("Response to delete resource with id {} processed.", resourceId);
        resourceManagementService.delete(resourceIdDTO.getName(), resourceIdDTO.getType(), principal.getName());
    }
}

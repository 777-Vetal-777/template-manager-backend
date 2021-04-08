package com.itextpdf.dito.manager.integration.editor.service.resource.impl;

import com.itextpdf.dito.editor.server.common.core.descriptor.resource.ResourceLeafDescriptor;
import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceFileEntity;
import com.itextpdf.dito.manager.integration.editor.service.resource.ResourceManagementService;
import com.itextpdf.dito.manager.service.resource.ResourceFileService;
import com.itextpdf.dito.manager.service.resource.ResourceService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ResourceManagementServiceImpl implements ResourceManagementService {
    private static final Logger log = LogManager.getLogger(ResourceManagementServiceImpl.class);
    private final ResourceService resourceService;
    private final ResourceFileService resourceFileService;

    public ResourceManagementServiceImpl(final ResourceService resourceService,
                                         final ResourceFileService resourceFileService) {
        this.resourceService = resourceService;
        this.resourceFileService = resourceFileService;
    }

    @Override
    public ResourceFileEntity get(final String name, final ResourceTypeEnum type, final String subName) {
        log.info("Get file by resource name: {} and type: {} and subName: {} was started", name, type, subName);
        // subName - it's a name of font for that realization
		final ResourceEntity resourceEntity = resourceService.get(name, type);
		final Optional<ResourceFileEntity> resourceFileEntity;
		if (subName != null) {
			resourceFileEntity = resourceEntity.getResourceFiles().stream()
					.filter(r -> Objects.equals(r.getFontName(), subName)).findFirst();
		} else {
			resourceFileEntity = resourceEntity.getResourceFiles().stream().findFirst();
		}
        final ResourceFileEntity fileEntity = resourceFileEntity.orElseThrow();
        log.info("Get file by resource name: {} and type: {} and subName: {} was finished successfully", name, type, subName);
        return fileEntity;
    }

    @Override
    public ResourceFileEntity getByUuid(final String uuid) {
        log.info("Get file by uuid: {} was started", uuid);
        final ResourceFileEntity fileEntity = resourceFileService.getByUuid(uuid);
        log.info("Get file by uuid: {} was finished successfully", uuid);
        return fileEntity;
    }

    @Override
    public List<ResourceEntity> list() {
        return resourceService.list();
    }

    @Override
    public ResourceEntity createNewVersion(final String name, final ResourceTypeEnum type, final byte[] data,
                                           final String fileName,
                                           final String email) {
        return resourceService.createNewVersion(name, type, data, fileName, email, null);
    }

    @Override
    public ResourceEntity create(final ResourceLeafDescriptor descriptor, final byte[] data, final String fileName,
                                 final String email) {
    	//Freezed while Editor apply different resources loading. (Bounded to DTM-2006). Uploading Images Only for now
    	/*
        final ResourceTypeEnum resourceTypeEnum;
        if (descriptor instanceof ImageDescriptor) {
            resourceTypeEnum = ResourceTypeEnum.IMAGE;
        } else if (descriptor instanceof StylesheetDescriptor) {
            resourceTypeEnum = ResourceTypeEnum.STYLESHEET;
        } else {
            resourceTypeEnum = ResourceTypeEnum.FONT;
        }
        */
    	final ResourceTypeEnum resourceTypeEnum = ResourceTypeEnum.IMAGE;
        final String name = descriptor.getDisplayName();
        return resourceService.create(name, resourceTypeEnum, data, name, email);
    }

    @Override
    public ResourceEntity delete(final String name, final ResourceTypeEnum type, final String mail) {
        return resourceService.delete(name, type, mail);
    }

    @Override
    public ResourceEntity deleteByUuid(final String uuid, final String mail) {
        final ResourceEntity resourceEntity = getByUuid(uuid).getResource();
        return resourceService.delete(resourceEntity.getName(), resourceEntity.getType(), mail);
    }
}

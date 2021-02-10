package com.itextpdf.dito.manager.integration.editor.service.resource.impl;

import com.itextpdf.dito.editor.server.common.core.descriptor.resource.AbstractResourceFileDescriptor.ImageDescriptor;
import com.itextpdf.dito.editor.server.common.core.descriptor.resource.AbstractResourceFileDescriptor.StylesheetDescriptor;
import com.itextpdf.dito.editor.server.common.core.descriptor.resource.ResourceLeafDescriptor;
import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceFileEntity;
import com.itextpdf.dito.manager.integration.editor.service.resource.ResourceManagementService;
import com.itextpdf.dito.manager.service.resource.ResourceService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ResourceManagementServiceImpl implements ResourceManagementService {
    private final ResourceService resourceService;

    public ResourceManagementServiceImpl(final ResourceService resourceService) {
        this.resourceService = resourceService;
    }


    @Override
    public byte[] get(final String name, final ResourceTypeEnum type, final String subName) {
    	//subName - it's a name of font for that realization
    	ResourceEntity resourceEntity = null;
		if (subName != null) {
			resourceEntity = resourceService.get(name, type, subName);
		} else {
			resourceEntity = resourceService.get(name, type);
		}
        final Optional<ResourceFileEntity> resourceFileEntity = resourceEntity.getResourceFiles().stream().findFirst();

        return resourceFileEntity.get().getFile();
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
        final ResourceTypeEnum resourceTypeEnum;
        if (descriptor instanceof ImageDescriptor) {
            resourceTypeEnum = ResourceTypeEnum.IMAGE;
        } else if (descriptor instanceof StylesheetDescriptor) {
            resourceTypeEnum = ResourceTypeEnum.STYLESHEET;
        } else {
            resourceTypeEnum = ResourceTypeEnum.FONT;
        }
        final String name = descriptor.getDisplayName();
        return resourceService.create(name, resourceTypeEnum, data, name, email);
    }

    @Override
    public ResourceEntity delete(final String name, final ResourceTypeEnum type, final String mail) {
        return resourceService.delete(name, type, mail);
    }
}

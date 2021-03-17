package com.itextpdf.dito.manager.integration.editor.service.resource;

import com.itextpdf.dito.editor.server.common.core.descriptor.resource.ResourceLeafDescriptor;
import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceFileEntity;

import java.util.List;

public interface ResourceManagementService {
    ResourceFileEntity get(String name, ResourceTypeEnum type, String subName);

    List<ResourceEntity> list();

    ResourceEntity createNewVersion(String name, ResourceTypeEnum type, byte[] data, String fileName, String email);

    ResourceEntity create(ResourceLeafDescriptor resourceLeafDescriptor, byte[] data, String fileName, String email);

    ResourceEntity delete(String name, ResourceTypeEnum type, String mail);
}

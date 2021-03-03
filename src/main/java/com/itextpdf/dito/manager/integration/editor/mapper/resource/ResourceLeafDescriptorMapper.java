package com.itextpdf.dito.manager.integration.editor.mapper.resource;

import com.itextpdf.dito.editor.server.common.core.descriptor.resource.ResourceLeafDescriptor;
import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.integration.editor.dto.ResourceIdDTO;

import java.util.Collection;
import java.util.List;

public interface ResourceLeafDescriptorMapper {
    ResourceLeafDescriptor map(ResourceEntity entity);

    List<ResourceLeafDescriptor> map(Collection<ResourceEntity> entities);

    ResourceIdDTO map(String id);

    String encodeId(String name, ResourceTypeEnum resourceTypeEnum, String subName);
}

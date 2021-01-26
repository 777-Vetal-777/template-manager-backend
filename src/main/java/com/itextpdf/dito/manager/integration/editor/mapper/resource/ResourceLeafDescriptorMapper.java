package com.itextpdf.dito.manager.integration.editor.mapper.resource;

import com.itextpdf.dito.editor.server.common.core.descriptor.resource.ResourceLeafDescriptor;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;

import java.util.Collection;
import java.util.List;

public interface ResourceLeafDescriptorMapper {
    ResourceLeafDescriptor map(ResourceEntity resourceEntity);

    List<ResourceLeafDescriptor> map(Collection<ResourceLeafDescriptor> resourceLeafDescriptors);
}

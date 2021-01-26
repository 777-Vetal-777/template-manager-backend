package com.itextpdf.dito.manager.integration.editor.mapper.resource.impl;

import com.itextpdf.dito.editor.server.common.core.descriptor.resource.ResourceLeafDescriptor;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.integration.editor.mapper.resource.ResourceLeafDescriptorMapper;

import java.util.Collection;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ResourceLeafDescriptorMapperImpl implements ResourceLeafDescriptorMapper {
    @Override
    public ResourceLeafDescriptor map(final ResourceEntity resourceEntity) {
        return null;
    }

    @Override
    public List<ResourceLeafDescriptor> map(final Collection<ResourceLeafDescriptor> resourceLeafDescriptors) {
        return null;
    }
}

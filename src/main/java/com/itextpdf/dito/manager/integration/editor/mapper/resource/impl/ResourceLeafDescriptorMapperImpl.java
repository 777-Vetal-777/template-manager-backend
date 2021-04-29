package com.itextpdf.dito.manager.integration.editor.mapper.resource.impl;

import com.itextpdf.dito.editor.server.common.core.descriptor.resource.AbstractResourceFileDescriptor.ImageDescriptor;
import com.itextpdf.dito.editor.server.common.core.descriptor.resource.AbstractResourceFileDescriptor.StylesheetDescriptor;
import com.itextpdf.dito.editor.server.common.core.descriptor.resource.AbstractResourceFileDescriptor.UnknownResource;
import com.itextpdf.dito.editor.server.common.core.descriptor.resource.ResourceLeafDescriptor;
import com.itextpdf.dito.editor.server.common.core.descriptor.resource.font.FontDescriptor;
import com.itextpdf.dito.editor.server.common.core.descriptor.resource.font.FontFileDescriptor;
import com.itextpdf.dito.editor.server.common.core.descriptor.resource.font.FontStyle;
import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceFileEntity;
import com.itextpdf.dito.manager.integration.editor.mapper.resource.ResourceIdMapper;
import com.itextpdf.dito.manager.integration.editor.mapper.resource.ResourceLeafDescriptorMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ResourceLeafDescriptorMapperImpl implements ResourceLeafDescriptorMapper {

    private final ResourceIdMapper resourceIdMapper;

    public ResourceLeafDescriptorMapperImpl(final ResourceIdMapper resourceIdMapper) {
        this.resourceIdMapper = resourceIdMapper;
    }

    @Override
    public ResourceLeafDescriptor map(final ResourceEntity resourceEntity) {
        ResourceLeafDescriptor resourceLeafDescriptor;

        final String name = resourceEntity.getName();
        final ResourceTypeEnum type = resourceEntity.getType();
        final String id = resourceIdMapper.mapToId(resourceEntity);
        switch (type) {
            case FONT:
                final FontDescriptor fontDescriptor = new FontDescriptor(id);
                fontDescriptor.setFontFiles(getFontFiles(resourceEntity));
                resourceLeafDescriptor = fontDescriptor;
                break;
            case IMAGE:
                resourceLeafDescriptor = new ImageDescriptor(id);
                break;
            case STYLESHEET:
                resourceLeafDescriptor = new StylesheetDescriptor(id);
                break;
            default:
                resourceLeafDescriptor = new UnknownResource(id);
                break;
        }
        resourceLeafDescriptor.setDisplayName(name);

        return resourceLeafDescriptor;
    }

    @Override
    public List<ResourceLeafDescriptor> map(final Collection<ResourceEntity> resourceLeafDescriptors) {
        return resourceLeafDescriptors.stream().map(this::map).collect(Collectors.toList());
    }

    private List<FontFileDescriptor> getFontFiles(final ResourceEntity resourceEntity) {
        final List<FontFileDescriptor> fontFilesList = new ArrayList<>();
        for (final ResourceFileEntity fileEntity : resourceEntity.getResourceFiles()) {
            final String id = resourceIdMapper.mapToId(fileEntity);
            final FontFileDescriptor fileDescriptor = new FontFileDescriptor(id);
            fileDescriptor.setStyle(FontStyle.valueOf(fileEntity.getFontName()));
            fontFilesList.add(fileDescriptor);
        }
        return fontFilesList;
    }

}

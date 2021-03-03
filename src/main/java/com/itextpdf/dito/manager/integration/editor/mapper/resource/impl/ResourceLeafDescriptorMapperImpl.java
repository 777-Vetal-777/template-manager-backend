package com.itextpdf.dito.manager.integration.editor.mapper.resource.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.itextpdf.dito.manager.integration.editor.dto.ResourceIdDTO;
import com.itextpdf.dito.manager.integration.editor.mapper.resource.ResourceLeafDescriptorMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ResourceLeafDescriptorMapperImpl implements ResourceLeafDescriptorMapper {
    private static final Logger log = LogManager.getLogger(ResourceLeafDescriptorMapperImpl.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ResourceLeafDescriptor map(final ResourceEntity resourceEntity) {
        ResourceLeafDescriptor resourceLeafDescriptor;

        final String name = resourceEntity.getName();
        final ResourceTypeEnum type = resourceEntity.getType();
        final String id = encodeId(name, type, null);
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
        resourceLeafDescriptor.setDisplayName(resourceEntity.getName());

        return resourceLeafDescriptor;
    }

    @Override
    public List<ResourceLeafDescriptor> map(final Collection<ResourceEntity> resourceLeafDescriptors) {
        return resourceLeafDescriptors.stream().map(this::map).collect(Collectors.toList());
    }

    @Override
    public ResourceIdDTO map(final String id) {
        return deserialize(decode(id));
    }

    @Override
    public String encodeId(final String name, final ResourceTypeEnum resourceTypeEnum, final String subName) {
        String result;

        final ResourceIdDTO resourceIdDTO = new ResourceIdDTO();
        resourceIdDTO.setName(name);
        resourceIdDTO.setType(resourceTypeEnum);
        resourceIdDTO.setSubName(subName);
        final String json = serialize(resourceIdDTO);
        result = encode(json);

        return result;
    }

    private List<FontFileDescriptor> getFontFiles(final ResourceEntity resourceEntity) {
        final List<FontFileDescriptor> fontFilesList = new ArrayList<>();
        for (final ResourceFileEntity fileEntity : resourceEntity.getResourceFiles()) {
            final String id = encodeId(resourceEntity.getName(), ResourceTypeEnum.FONT, fileEntity.getFontName());
            final FontFileDescriptor fileDescriptor = new FontFileDescriptor(id);
            fileDescriptor.setStyle(FontStyle.valueOf(fileEntity.getFontName()));
            fontFilesList.add(fileDescriptor);
        }
        return fontFilesList;
    }

    protected ResourceIdDTO deserialize(final String data) {
        ResourceIdDTO result = null;

        try {
            result = objectMapper.readValue(data, ResourceIdDTO.class);
        } catch (JsonProcessingException e) {
            log.error(e);
        }

        return result;
    }

    private String serialize(final Object data) {
        String result = null;

        try {
            result = objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.error(e);
        }

        return result;
    }

    private String encode(final String name) {
        return Base64.getUrlEncoder().encodeToString(name.getBytes());
    }

    private String decode(final String name) {
        return new String(Base64.getUrlDecoder().decode(name));
    }
}

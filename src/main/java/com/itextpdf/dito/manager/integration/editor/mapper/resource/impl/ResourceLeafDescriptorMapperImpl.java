package com.itextpdf.dito.manager.integration.editor.mapper.resource.impl;

import com.itextpdf.dito.editor.server.common.core.descriptor.resource.AbstractResourceFileDescriptor.ImageDescriptor;
import com.itextpdf.dito.editor.server.common.core.descriptor.resource.AbstractResourceFileDescriptor.StylesheetDescriptor;
import com.itextpdf.dito.editor.server.common.core.descriptor.resource.AbstractResourceFileDescriptor.UnknownResource;
import com.itextpdf.dito.editor.server.common.core.descriptor.resource.ResourceLeafDescriptor;
import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.integration.editor.dto.ResourceIdDTO;
import com.itextpdf.dito.manager.integration.editor.mapper.resource.ResourceLeafDescriptorMapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class ResourceLeafDescriptorMapperImpl implements ResourceLeafDescriptorMapper {
    private static final Logger log = LogManager.getLogger(ResourceLeafDescriptorMapperImpl.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ResourceLeafDescriptor map(final ResourceEntity resourceEntity) {
        ResourceLeafDescriptor resourceLeafDescriptor;

        final String name = resourceEntity.getName();
        final ResourceTypeEnum type = resourceEntity.getType();
        String fontName = null;
        if(Objects.equals(ResourceTypeEnum.FONT, resourceEntity.getType())) {
        	fontName = resourceEntity.getLatestFile().get(0).getFontName();
        }
        final String id = encodeId(name, type, fontName);
        switch (type) {
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

    private String encodeId(final String name, final ResourceTypeEnum resourceTypeEnum, final String subName) {
        String result;

        final ResourceIdDTO resourceIdDTO = new ResourceIdDTO();
        resourceIdDTO.setName(name);
        resourceIdDTO.setType(resourceTypeEnum);
        resourceIdDTO.setSubName(subName);
        final String json = serialize(resourceIdDTO);
        result = encode(json);

        return result;
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

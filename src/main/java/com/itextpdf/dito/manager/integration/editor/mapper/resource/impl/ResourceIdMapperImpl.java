package com.itextpdf.dito.manager.integration.editor.mapper.resource.impl;

import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceFileEntity;
import com.itextpdf.dito.manager.integration.editor.mapper.resource.ResourceIdMapper;
import org.springframework.stereotype.Component;

@Component
public class ResourceIdMapperImpl implements ResourceIdMapper {

    @Override
    public String mapToId(final ResourceEntity entity) {
        return entity.getUuid();
    }

    @Override
    public String mapToId(final ResourceFileEntity entity) {
        return entity.getUuid();
    }
}

package com.itextpdf.dito.manager.integration.editor.mapper.resource;

import com.itextpdf.dito.manager.entity.resource.ResourceEntity;

public interface ResourceIdMapper {
    String mapToId(ResourceEntity entity);
}

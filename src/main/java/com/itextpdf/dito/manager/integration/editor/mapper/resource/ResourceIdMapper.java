package com.itextpdf.dito.manager.integration.editor.mapper.resource;

import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceFileEntity;

public interface ResourceIdMapper {
    String mapToId(ResourceEntity entity);
    String mapToId(ResourceFileEntity entity);
}

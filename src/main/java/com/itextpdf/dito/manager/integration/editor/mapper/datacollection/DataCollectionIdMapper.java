package com.itextpdf.dito.manager.integration.editor.mapper.datacollection;

import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;

public interface DataCollectionIdMapper {
    String mapToId(DataCollectionEntity entity);
}

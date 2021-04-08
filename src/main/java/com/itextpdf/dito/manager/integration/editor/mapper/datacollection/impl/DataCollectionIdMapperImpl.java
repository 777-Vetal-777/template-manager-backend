package com.itextpdf.dito.manager.integration.editor.mapper.datacollection.impl;

import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.integration.editor.mapper.datacollection.DataCollectionIdMapper;
import org.springframework.stereotype.Component;

@Component
public class DataCollectionIdMapperImpl implements DataCollectionIdMapper {

    @Override
    public String mapToId(final DataCollectionEntity entity) {
        return entity.getUuid();
    }

}

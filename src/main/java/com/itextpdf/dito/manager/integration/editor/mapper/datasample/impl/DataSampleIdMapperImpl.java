package com.itextpdf.dito.manager.integration.editor.mapper.datasample.impl;

import com.itextpdf.dito.manager.entity.datasample.DataSampleEntity;
import com.itextpdf.dito.manager.integration.editor.mapper.datasample.DataSampleIdMapper;
import org.springframework.stereotype.Component;

@Component
public class DataSampleIdMapperImpl implements DataSampleIdMapper {

    @Override
    public String mapToID(final DataSampleEntity entity) {
        return entity.getUuid();
    }
}

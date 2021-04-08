package com.itextpdf.dito.manager.integration.editor.mapper.datacollection.impl;

import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.datasample.DataSampleEntity;
import com.itextpdf.dito.manager.integration.editor.dto.DataCollectionDescriptor;
import com.itextpdf.dito.manager.integration.editor.mapper.datacollection.DataCollectionDescriptorMapper;
import com.itextpdf.dito.manager.integration.editor.mapper.datacollection.DataCollectionIdMapper;
import com.itextpdf.dito.manager.integration.editor.mapper.datasample.DataSampleIdMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DataCollectionDescriptorMapperImpl implements DataCollectionDescriptorMapper {

    private final DataCollectionIdMapper dataCollectionIdMapper;
    private final DataSampleIdMapper dataSampleIdMapper;

    public DataCollectionDescriptorMapperImpl(final DataCollectionIdMapper dataCollectionIdMapper,
                                              final DataSampleIdMapper dataSampleIdMapper) {
        this.dataCollectionIdMapper = dataCollectionIdMapper;
        this.dataSampleIdMapper = dataSampleIdMapper;
    }

    @Override
    public DataCollectionDescriptor map(final DataCollectionEntity entity, DataSampleEntity defaultDataSampleEntity) {
        final DataCollectionDescriptor descriptor = new DataCollectionDescriptor();
        descriptor.setDisplayName(entity.getName());
        descriptor.setType(entity.getType().toString());
        descriptor.setId(dataCollectionIdMapper.mapToId(entity));
        return descriptor;
    }
}

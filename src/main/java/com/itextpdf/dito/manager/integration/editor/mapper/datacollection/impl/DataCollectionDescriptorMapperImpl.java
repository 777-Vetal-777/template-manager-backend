package com.itextpdf.dito.manager.integration.editor.mapper.datacollection.impl;

import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.datasample.DataSampleEntity;
import com.itextpdf.dito.manager.integration.editor.dto.DataCollectionDescriptor;
import com.itextpdf.dito.manager.integration.editor.mapper.datacollection.DataCollectionDescriptorMapper;
import com.itextpdf.dito.manager.integration.editor.mapper.datasample.DataSampleDescriptorMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DataCollectionDescriptorMapperImpl implements DataCollectionDescriptorMapper {

    private final DataSampleDescriptorMapper dataSampleDescriptorMapper;

    public DataCollectionDescriptorMapperImpl(final DataSampleDescriptorMapper dataSampleDescriptorMapper) {
        this.dataSampleDescriptorMapper = dataSampleDescriptorMapper;
    }

    @Override
    public DataCollectionDescriptor map(final DataCollectionEntity entity, DataSampleEntity defaultDataSampleEntity) {
        final DataCollectionDescriptor descriptor = new DataCollectionDescriptor();
        descriptor.setDisplayName(entity.getName());
        descriptor.setType(entity.getType().toString());
        descriptor.setId(mapToId(entity));
        return descriptor;
    }

    @Override
    public String mapToId(final DataCollectionEntity entity) {
        return entity.getUuid();
    }
}

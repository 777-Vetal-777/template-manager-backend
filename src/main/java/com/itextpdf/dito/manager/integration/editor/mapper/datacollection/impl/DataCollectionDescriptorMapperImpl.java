package com.itextpdf.dito.manager.integration.editor.mapper.datacollection.impl;

import com.itextpdf.dito.manager.component.encoder.Encoder;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.datasample.DataSampleEntity;
import com.itextpdf.dito.manager.integration.editor.dto.DataCollectionDescriptor;
import com.itextpdf.dito.manager.integration.editor.mapper.datacollection.DataCollectionDescriptorMapper;
import com.itextpdf.dito.manager.integration.editor.mapper.datasample.DataSampleDescriptorMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DataCollectionDescriptorMapperImpl implements DataCollectionDescriptorMapper {

    private final Encoder encoder;
    private final DataSampleDescriptorMapper dataSampleDescriptorMapper;

    public DataCollectionDescriptorMapperImpl(final Encoder encoder,
                                              final DataSampleDescriptorMapper dataSampleDescriptorMapper) {
        this.encoder = encoder;
        this.dataSampleDescriptorMapper = dataSampleDescriptorMapper;
    }

    @Override
    public DataCollectionDescriptor map(final DataCollectionEntity entity, DataSampleEntity dataSampleEntity) {
        final DataCollectionDescriptor descriptor = new DataCollectionDescriptor();
        descriptor.setDisplayName(entity.getName());
        descriptor.setType(entity.getType().toString());
        descriptor.setId(encoder.encode(entity.getName()));
        Optional.ofNullable(dataSampleEntity).map(dataSampleDescriptorMapper::mapToID).ifPresent(descriptor::setDefaultSampleId);
        return descriptor;
    }
}

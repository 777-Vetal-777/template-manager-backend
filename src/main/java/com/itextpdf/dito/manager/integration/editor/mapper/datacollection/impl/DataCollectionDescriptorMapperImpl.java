package com.itextpdf.dito.manager.integration.editor.mapper.datacollection.impl;

import com.itextpdf.dito.manager.component.encoder.Encoder;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.integration.editor.dto.DataCollectionDescriptor;
import com.itextpdf.dito.manager.integration.editor.mapper.datacollection.DataCollectionDescriptorMapper;
import org.springframework.stereotype.Component;

@Component
public class DataCollectionDescriptorMapperImpl implements DataCollectionDescriptorMapper {

    private final Encoder encoder;

    public DataCollectionDescriptorMapperImpl(Encoder encoder) {
        this.encoder = encoder;
    }

    @Override
    public DataCollectionDescriptor map(final DataCollectionEntity entity) {
        final DataCollectionDescriptor descriptor = new DataCollectionDescriptor();
        descriptor.setDisplayName(entity.getName());
        descriptor.setType(descriptor.getType());
        descriptor.setId(encoder.encode(entity.getName()));
        return descriptor;
    }
}

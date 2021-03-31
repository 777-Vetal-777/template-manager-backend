package com.itextpdf.dito.manager.integration.editor.mapper.datasample.impl;

import com.itextpdf.dito.editor.server.common.core.descriptor.DataSampleDescriptor;
import com.itextpdf.dito.editor.server.common.core.descriptor.DataSampleDescriptor.DataType;
import com.itextpdf.dito.manager.component.encoder.Encoder;
import com.itextpdf.dito.manager.entity.datasample.DataSampleEntity;
import com.itextpdf.dito.manager.integration.editor.mapper.datasample.DataSampleDescriptorMapper;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class DataSampleDescriptorMapperImpl implements DataSampleDescriptorMapper {

    private final Encoder encoder;

    public DataSampleDescriptorMapperImpl(Encoder encoder) {
        this.encoder = encoder;
    }

    @Override
    public DataSampleDescriptor map(final DataSampleEntity entity) {
        final DataSampleDescriptor result;
        final String name = entity.getName();
        final String encodedName = mapToID(entity);
        result = new DataSampleDescriptor(encodedName);
        result.setDisplayName(name);
        result.setDataType(DataType.JSON);
        result.setCollectionIdList(Collections.singletonList(encoder.encode(entity.getDataCollection().getName())));
        return result;
    }

    @Override
    public String mapToID(final DataSampleEntity entity) {
        final String name = entity.getName();
        return encoder.encode(name);
    }

    @Override
    public List<DataSampleDescriptor> map(final Collection<DataSampleEntity> entities) {
        return entities.stream().map(this::map).collect(Collectors.toList());
    }

}

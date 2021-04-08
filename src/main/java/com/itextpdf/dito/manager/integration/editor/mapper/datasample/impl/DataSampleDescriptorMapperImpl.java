package com.itextpdf.dito.manager.integration.editor.mapper.datasample.impl;

import com.itextpdf.dito.editor.server.common.core.descriptor.DataSampleDescriptor;
import com.itextpdf.dito.editor.server.common.core.descriptor.DataSampleDescriptor.DataType;
import com.itextpdf.dito.manager.entity.datasample.DataSampleEntity;
import com.itextpdf.dito.manager.integration.editor.mapper.datacollection.DataCollectionIdMapper;
import com.itextpdf.dito.manager.integration.editor.mapper.datasample.DataSampleDescriptorMapper;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.itextpdf.dito.manager.integration.editor.mapper.datasample.DataSampleIdMapper;
import org.springframework.stereotype.Component;

@Component
public class DataSampleDescriptorMapperImpl implements DataSampleDescriptorMapper {

    private final DataSampleIdMapper dataSampleIdMapper;
    private final DataCollectionIdMapper dataCollectionIdMapper;

    public DataSampleDescriptorMapperImpl(final DataSampleIdMapper dataSampleIdMapper,
                                          final DataCollectionIdMapper dataCollectionIdMapper) {
        this.dataSampleIdMapper = dataSampleIdMapper;
        this.dataCollectionIdMapper = dataCollectionIdMapper;
    }

    @Override
    public DataSampleDescriptor map(final DataSampleEntity entity) {
        final DataSampleDescriptor result;
        final String name = entity.getName();
        final String encodedName = dataSampleIdMapper.mapToID(entity);
        result = new DataSampleDescriptor(encodedName);
        result.setDisplayName(name);
        result.setDataType(DataType.JSON);
        result.setCollectionIdList(Collections.singletonList(dataCollectionIdMapper.mapToId(entity.getDataCollection())));
        return result;
    }

    @Override
    public List<DataSampleDescriptor> map(final Collection<DataSampleEntity> entities) {
        return entities.stream().map(this::map).collect(Collectors.toList());
    }

}

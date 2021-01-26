package com.itextpdf.dito.manager.integration.editor.mapper.datasample;

import com.itextpdf.dito.editor.server.common.core.descriptor.DataSampleDescriptor;
import com.itextpdf.dito.manager.entity.datasample.DataSampleEntity;

import java.util.Collection;
import java.util.List;

public interface DataSampleDescriptorMapper {
    DataSampleDescriptor map(DataSampleEntity entity);

    List<DataSampleDescriptor> map(Collection<DataSampleEntity> entities);
}

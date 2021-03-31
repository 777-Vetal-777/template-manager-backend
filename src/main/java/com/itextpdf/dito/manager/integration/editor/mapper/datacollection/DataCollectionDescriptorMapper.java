package com.itextpdf.dito.manager.integration.editor.mapper.datacollection;

import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.datasample.DataSampleEntity;
import com.itextpdf.dito.manager.integration.editor.dto.DataCollectionDescriptor;

public interface DataCollectionDescriptorMapper {

    DataCollectionDescriptor map(DataCollectionEntity entity, DataSampleEntity dataSampleEntity);

}

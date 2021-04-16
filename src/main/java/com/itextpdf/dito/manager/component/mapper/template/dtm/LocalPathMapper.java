package com.itextpdf.dito.manager.component.mapper.template.dtm;

import com.itextpdf.dito.manager.entity.datacollection.DataCollectionFileEntity;
import com.itextpdf.dito.manager.entity.datasample.DataSampleFileEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceFileEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;

public interface LocalPathMapper {
    String getTemplateBasePath();
    String getResourceBasePath();
    String getDataCollectionBasePath();
    String getDataSampleBasePath();

    String getLocalPath(TemplateFileEntity entity);
    String getLocalPath(ResourceFileEntity entity);
    String getLocalPath(DataCollectionFileEntity entity);
    String getLocalPath(DataSampleFileEntity entity);
}

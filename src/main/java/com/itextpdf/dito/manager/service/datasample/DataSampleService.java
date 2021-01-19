package com.itextpdf.dito.manager.service.datasample;

import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.datasample.DataSampleEntity;
import com.itextpdf.dito.manager.filter.datasample.DataSampleFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DataSampleService {
    DataSampleEntity create(DataCollectionEntity dataCollectionEntity, String name, String fileName, String sample, String comment, String email);

    Page<DataSampleEntity> list(Pageable pageable, DataSampleFilter filter, String searchParam);

    DataSampleEntity get(String dataSampleName);
    
    DataSampleEntity setAsDefault(String dataSampleName);
    
}

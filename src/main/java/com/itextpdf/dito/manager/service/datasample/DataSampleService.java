package com.itextpdf.dito.manager.service.datasample;

import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.datasample.DataSampleEntity;

public interface DataSampleService {
    DataSampleEntity create(DataCollectionEntity dataCollectionEntity, String name, String fileName, String sample, String comment, String email);

}

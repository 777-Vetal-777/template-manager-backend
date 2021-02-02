package com.itextpdf.dito.manager.integration.editor.service.data;

import com.itextpdf.dito.manager.entity.datasample.DataSampleEntity;

import java.util.Collection;

public interface DataManagementService {
    DataSampleEntity get(String id);

    DataSampleEntity createNewVersion(String name, String data, String fileName, String email);

    DataSampleEntity create(String dataCollectionId, String name, String fileName, String sample, String email);

    DataSampleEntity delete(String id);

    Collection<DataSampleEntity> getDataSamplesByCollectionId(String collectionId);
}

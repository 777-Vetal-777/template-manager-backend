package com.itextpdf.dito.manager.integration.editor.service.data;

import com.itextpdf.dito.manager.entity.datasample.DataSampleEntity;

import java.util.Collection;

public interface DataManagementService {
    DataSampleEntity get(String id);

    DataSampleEntity createNewVersion(String uuid, String data, String fileName, String email);

    DataSampleEntity create(String dataCollectionUuid, String name, String fileName, String sample, String email);

    DataSampleEntity delete(String id);

    Collection<DataSampleEntity> getDataSamplesByCollectionUuid(String collectionId);

    DataSampleEntity getDefaultDataSampleByCollectionId(Long collectionId);
}

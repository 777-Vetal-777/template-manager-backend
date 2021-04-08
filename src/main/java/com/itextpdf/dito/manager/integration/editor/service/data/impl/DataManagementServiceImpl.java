package com.itextpdf.dito.manager.integration.editor.service.data.impl;

import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.datasample.DataSampleEntity;
import com.itextpdf.dito.manager.integration.editor.service.data.DataManagementService;
import com.itextpdf.dito.manager.service.datacollection.DataCollectionService;
import com.itextpdf.dito.manager.service.datasample.DataSampleService;

import java.util.Collection;

import org.springframework.stereotype.Service;

@Service
public class DataManagementServiceImpl implements DataManagementService {
    private final DataSampleService dataSampleService;
    private final DataCollectionService dataCollectionService;

    public DataManagementServiceImpl(final DataSampleService dataSampleService,
            final DataCollectionService dataCollectionService) {
        this.dataSampleService = dataSampleService;
        this.dataCollectionService = dataCollectionService;
    }

    @Override
    public DataSampleEntity get(final String id) {
        return dataSampleService.getByUuid(id);
    }

    @Override
    public DataSampleEntity createNewVersion(final String uuid, final String data, final String fileName,
            final String email) {
        final DataSampleEntity entity = dataSampleService.getByUuid(uuid);
        return dataSampleService.createNewVersion(entity.getDataCollection().getName(), entity.getName(), data, fileName, email, null);
    }

    @Override
    public DataSampleEntity create(final String dataCollectionUuid, final String name, final String fileName,
            final String sample, final String email) {
        final DataCollectionEntity dataCollectionEntity = dataCollectionService.getByUuid(dataCollectionUuid);
        return dataSampleService.create(dataCollectionEntity, name, fileName, sample, null, email);
    }

    @Override
    public DataSampleEntity delete(final String id) {
        return dataSampleService.deleteByUuid(id);
    }

    @Override
    public Collection<DataSampleEntity> getDataSamplesByCollectionUuid(final String collectionId) {
        final DataCollectionEntity dataCollectionEntity = dataCollectionService.getByUuid(collectionId);
        return dataCollectionEntity.getDataSamples();
    }

    @Override
    public DataSampleEntity getDefaultDataSampleByCollectionId(Long collectionId) {
        return dataSampleService.findDataSampleByCollectionId(collectionId).orElse(null);
    }
}

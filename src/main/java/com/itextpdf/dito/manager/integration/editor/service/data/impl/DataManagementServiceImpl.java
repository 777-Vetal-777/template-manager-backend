package com.itextpdf.dito.manager.integration.editor.service.data.impl;

import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.datasample.DataSampleEntity;
import com.itextpdf.dito.manager.integration.editor.service.data.DataManagementService;
import com.itextpdf.dito.manager.service.datacollection.DataCollectionService;
import com.itextpdf.dito.manager.service.datasample.DataSampleService;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
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
        return dataSampleService.get(id);
    }

    @Override
    public DataSampleEntity createNewVersion(final String name, final String data, final String fileName,
            final String email) {
        return dataSampleService.createNewVersion(name, data, fileName, email, null);
    }

    @Override
    public DataSampleEntity create(final String dataCollectionId, final String name, final String fileName,
            final String sample, final String email) {
        final DataCollectionEntity dataCollectionEntity = dataCollectionService.get(dataCollectionId);
        return dataSampleService.create(dataCollectionEntity, name, fileName, sample, null, email);
    }

    @Override
    public DataSampleEntity delete(final String id) {
        final List<DataSampleEntity> deletedDataSample = dataSampleService
                .delete(Collections.singletonList(id));
        return deletedDataSample.get(0);
    }

    @Override
    public Collection<DataSampleEntity> getDataSamplesByCollectionId(final String collectionId) {
        final DataCollectionEntity dataCollectionEntity = dataCollectionService.get(collectionId);
        return dataCollectionEntity.getDataSamples();
    }
}

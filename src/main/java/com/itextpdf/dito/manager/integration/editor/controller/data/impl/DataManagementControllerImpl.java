package com.itextpdf.dito.manager.integration.editor.controller.data.impl;

import com.itextpdf.dito.editor.server.common.core.descriptor.DataSampleDescriptor;
import com.itextpdf.dito.manager.controller.AbstractController;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.datasample.DataSampleEntity;
import com.itextpdf.dito.manager.integration.editor.controller.data.DataManagementController;
import com.itextpdf.dito.manager.integration.editor.mapper.datasample.DataSampleDescriptorMapper;
import com.itextpdf.dito.manager.service.datacollection.DataCollectionService;
import com.itextpdf.dito.manager.service.datasample.DataSampleService;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DataManagementControllerImpl extends AbstractController implements DataManagementController {
    private final DataSampleService dataSampleService;
    private final DataCollectionService dataCollectionService;
    private final DataSampleDescriptorMapper dataSampleDescriptorMapper;

    public DataManagementControllerImpl(final DataSampleService dataSampleService,
            final DataCollectionService dataCollectionService,
            final DataSampleDescriptorMapper dataSampleDescriptorMapper) {
        this.dataSampleService = dataSampleService;
        this.dataCollectionService = dataCollectionService;
        this.dataSampleDescriptorMapper = dataSampleDescriptorMapper;
    }

    @Override
    public DataSampleDescriptor getDataSampleById(final String dataSampleId) {
        final String decodedDataSampleId = decodeBase64(dataSampleId);
        final DataSampleEntity dataSampleEntity = dataSampleService.get(decodedDataSampleId);
        return dataSampleDescriptorMapper.map(dataSampleEntity);
    }

    @Override
    public InputStream fetchDataSampleById(final String dataSampleId) {
        final String decodedDataSampleId = decodeBase64(dataSampleId);
        final DataSampleEntity dataSampleEntity = dataSampleService.get(decodedDataSampleId);
        return new ByteArrayInputStream(dataSampleEntity.getLatestVersion().getData());
    }

    @Override
    public DataSampleDescriptor createOrUpdate(final Principal principal, final String dataSampleId,
            final DataSampleDescriptor descriptor,
            final String data) {
        final String decodedDataSampleId = decodeBase64(dataSampleId);
        final DataSampleEntity dataSampleEntity = dataSampleService
                .createNewVersion(decodedDataSampleId, data, decodedDataSampleId,
                        principal.getName(), null);
        return dataSampleDescriptorMapper.map(dataSampleEntity);
    }

    @Override
    public DataSampleDescriptor add(final Principal principal, final DataSampleDescriptor descriptor,
            final InputStream data) {
        final String decodedDataCollectionId = decodeBase64(descriptor.getCollectionIdList().get(0));
        final DataCollectionEntity dataCollectionEntity = dataCollectionService.get(decodedDataCollectionId);
        final DataSampleEntity dataSampleEntity = dataSampleService
                .create(dataCollectionEntity, descriptor.getDisplayName(), descriptor.getDisplayName(),
                        inputStreamToString(data), null, principal.getName());
        return dataSampleDescriptorMapper.map(dataSampleEntity);
    }

    @Override
    public DataSampleDescriptor deleteDataSampleById(final String dataSampleId) {
        final String decodedDataSampleId = decodeBase64(dataSampleId);
        final List<DataSampleEntity> deletedDataSample = dataSampleService
                .delete(Collections.singletonList(decodedDataSampleId));
        return dataSampleDescriptorMapper.map(deletedDataSample.get(0));
    }

    @Override
    public List<DataSampleDescriptor> getDataSamplesByCollectionId(final String collectionId) {
        final String decodedDataCollectionId = decodeBase64(collectionId);
        final DataCollectionEntity dataCollectionEntity = dataCollectionService.get(decodedDataCollectionId);
        final Collection<DataSampleEntity> dataSampleEntities = dataCollectionEntity.getDataSamples();
        return dataSampleDescriptorMapper.map(dataSampleEntities);
    }
}

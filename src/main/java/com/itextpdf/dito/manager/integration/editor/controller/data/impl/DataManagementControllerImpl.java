package com.itextpdf.dito.manager.integration.editor.controller.data.impl;

import com.itextpdf.dito.editor.server.common.core.descriptor.DataSampleDescriptor;
import com.itextpdf.dito.manager.controller.AbstractController;
import com.itextpdf.dito.manager.entity.datasample.DataSampleEntity;
import com.itextpdf.dito.manager.integration.editor.controller.data.DataManagementController;
import com.itextpdf.dito.manager.integration.editor.mapper.datasample.DataSampleDescriptorMapper;
import com.itextpdf.dito.manager.integration.editor.service.data.DataManagementService;

import java.security.Principal;
import java.util.Collection;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DataManagementControllerImpl extends AbstractController implements DataManagementController {
    private static final Logger log = LogManager.getLogger(DataManagementControllerImpl.class);
    private final DataManagementService dataManagementService;
    private final DataSampleDescriptorMapper dataSampleDescriptorMapper;

    public DataManagementControllerImpl(final DataManagementService dataManagementService,
            final DataSampleDescriptorMapper dataSampleDescriptorMapper) {
        this.dataManagementService = dataManagementService;
        this.dataSampleDescriptorMapper = dataSampleDescriptorMapper;
    }

    @Override
    public DataSampleDescriptor getDataSampleById(final String dataSampleId) {
        log.info("Request to get sample by data sample id {} received.",dataSampleId);
        final String decodedDataSampleId = decodeBase64(dataSampleId);
        final DataSampleEntity dataSampleEntity = dataManagementService.get(decodedDataSampleId);
        log.info("Response on get sample by data sample id {} processed.",dataSampleId);
        return dataSampleDescriptorMapper.map(dataSampleEntity);
    }

    @Override
    public byte[] fetchDataSampleById(final String dataSampleId) {
        final String decodedDataSampleId = decodeBase64(dataSampleId);
        log.info("Request to get sample by data sample id {} received.",dataSampleId);
        final DataSampleEntity dataSampleEntity = dataManagementService.get(decodedDataSampleId);
        log.info("Response on get sample by data sample id {} processed.",dataSampleId);
        return dataSampleEntity.getLatestVersion().getData();
    }

    @Override
    public DataSampleDescriptor createOrUpdate(final Principal principal, final String dataSampleId,
            final DataSampleDescriptor descriptor,
            final String data) {
        final String decodedDataSampleId = decodeBase64(dataSampleId);
        log.info("Request to create or update data sample with id {} received.",dataSampleId);
        final DataSampleEntity dataSampleEntity = dataManagementService
                .createNewVersion(decodedDataSampleId, data, decodedDataSampleId, principal.getName());
        log.info("Response to create or update data sample  by data sample id {} processed.",dataSampleId);
        return dataSampleDescriptorMapper.map(dataSampleEntity);
    }

    @Override
    public DataSampleDescriptor add(final Principal principal, final DataSampleDescriptor descriptor,
            final String data) {
        log.info("Request to create data sample with name {} received.",descriptor.getDisplayName());
        final String decodedDataCollectionId = decodeBase64(descriptor.getCollectionIdList().get(0));
        final String displayName = descriptor.getDisplayName();
        final DataSampleEntity dataSampleEntity = dataManagementService.create(decodedDataCollectionId,
                displayName, displayName, data, principal.getName());
        log.info("Response to create resource with name {} processed. Resource created with id {}.",dataSampleEntity.getName(), dataSampleEntity.getId());
        return dataSampleDescriptorMapper.map(dataSampleEntity);
    }

    @Override
    public DataSampleDescriptor deleteDataSampleById(final String dataSampleId) {
        log.info("Request to delete resource with id {} received.",dataSampleId);
        final String decodedDataSampleId = decodeBase64(dataSampleId);
        final DataSampleEntity deletedDataSample = dataManagementService.delete(decodedDataSampleId);
        log.info("Response to delete resource with id {} processed.",dataSampleId);
        return dataSampleDescriptorMapper.map(deletedDataSample);
    }

    @Override
    public List<DataSampleDescriptor> getDataSamplesByCollectionId(final String collectionId) {
        final String decodedDataCollectionId = decodeBase64(collectionId);
        log.info("Request to get data samples by collection with id {} received.",decodedDataCollectionId);
        final Collection<DataSampleEntity> dataSampleEntities = dataManagementService.getDataSamplesByCollectionId(decodedDataCollectionId);
        log.info("Response to get data samples by collection with id {} processed.",decodedDataCollectionId);
        return dataSampleDescriptorMapper.map(dataSampleEntities);
    }
}

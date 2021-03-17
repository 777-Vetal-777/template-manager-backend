package com.itextpdf.dito.manager.integration.editor.controller.data.impl;

import com.itextpdf.dito.editor.server.common.core.descriptor.DataSampleDescriptor;
import com.itextpdf.dito.manager.component.encoder.Encoder;
import com.itextpdf.dito.manager.controller.AbstractController;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.datasample.DataSampleEntity;
import com.itextpdf.dito.manager.integration.editor.controller.data.DataManagementController;
import com.itextpdf.dito.manager.integration.editor.dto.DataCollectionDescriptor;
import com.itextpdf.dito.manager.integration.editor.mapper.datacollection.DataCollectionDescriptorMapper;
import com.itextpdf.dito.manager.integration.editor.mapper.datasample.DataSampleDescriptorMapper;
import com.itextpdf.dito.manager.integration.editor.service.data.DataCollectionManagementService;
import com.itextpdf.dito.manager.integration.editor.service.data.DataManagementService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Collection;
import java.util.List;

@RestController
public class DataManagementControllerImpl extends AbstractController implements DataManagementController {
    private static final Logger log = LogManager.getLogger(DataManagementControllerImpl.class);
    private static final String JSON_SUFFIX = ".json";
    private final DataManagementService dataManagementService;
    private final DataCollectionManagementService dataCollectionManagementService;
    private final DataSampleDescriptorMapper dataSampleDescriptorMapper;
    private final DataCollectionDescriptorMapper dataCollectionDescriptorMapper;
    private final Encoder encoder;

    public DataManagementControllerImpl(final DataManagementService dataManagementService,
                                        final DataSampleDescriptorMapper dataSampleDescriptorMapper,
                                        final DataCollectionManagementService dataCollectionManagementService,
                                        final DataCollectionDescriptorMapper dataCollectionDescriptorMapper,
                                        final Encoder encoder) {
        this.dataManagementService = dataManagementService;
        this.dataSampleDescriptorMapper = dataSampleDescriptorMapper;
        this.dataCollectionManagementService = dataCollectionManagementService;
        this.dataCollectionDescriptorMapper = dataCollectionDescriptorMapper;
        this.encoder = encoder;
    }

    @Override
    public DataSampleDescriptor getDataSampleById(final String dataSampleId) {
        final String decodedDataSampleId = encoder.decode(dataSampleId);
        log.info("Request to get sample by data sample id {} received.", decodedDataSampleId);
        final DataSampleEntity dataSampleEntity = dataManagementService.get(decodedDataSampleId);
        log.info("Response on get sample by data sample id {} processed.", decodedDataSampleId);
        return dataSampleDescriptorMapper.map(dataSampleEntity);
    }

    @Override
    public byte[] fetchDataSampleById(final String dataSampleId) {
        final String decodedDataSampleId = encoder.decode(dataSampleId);
        log.info("Request to get sample by data sample id {} received.", decodedDataSampleId);
        final DataSampleEntity dataSampleEntity = dataManagementService.get(decodedDataSampleId);
        log.info("Response on get sample by data sample id {} processed.", decodedDataSampleId);
        return dataSampleEntity.getLatestVersion().getData();
    }

    @Override
    public DataSampleDescriptor createNewDataSampleVersion(final Principal principal, final String dataSampleId,
                                                           final DataSampleDescriptor descriptor,
                                                           final String data) {
        final String decodedDataSampleId = encoder.decode(dataSampleId);
        log.info("Request to create or update data sample with id {} received.", decodedDataSampleId);
        final String newName = descriptor != null ? descriptor.getDisplayName() : null;

        final DataSampleEntity dataSampleEntity = dataManagementService
                .createNewVersion(decodedDataSampleId, data, newName, principal.getName());
        log.info("Response to create or update data sample  by data sample id {} processed.", decodedDataSampleId);
        return dataSampleDescriptorMapper.map(dataSampleEntity);
    }

    @Override
    public DataSampleDescriptor add(final Principal principal, final DataSampleDescriptor descriptor,
                                    final String data) {
        log.info("Request to create data sample with name {} received.", descriptor.getDisplayName());
        final String decodedDataCollectionId = encoder.decode(descriptor.getCollectionIdList().get(0));
        final String displayName = descriptor.getDisplayName();
        final DataSampleEntity dataSampleEntity = dataManagementService.create(decodedDataCollectionId,
                displayName, displayName, data, principal.getName());
        log.info("Response to create resource with name {} processed. Resource created with id {}.", dataSampleEntity.getName(), dataSampleEntity.getId());
        return dataSampleDescriptorMapper.map(dataSampleEntity);
    }

    @Override
    public DataSampleDescriptor deleteDataSampleById(final String dataSampleId) {
        final String decodedDataSampleId = encoder.decode(dataSampleId);
        log.info("Request to delete resource with id {} received.", decodedDataSampleId);
        final DataSampleEntity deletedDataSample = dataManagementService.delete(decodedDataSampleId);
        log.info("Response to delete resource with id {} processed.", decodedDataSampleId);
        return dataSampleDescriptorMapper.map(deletedDataSample);
    }

    @Override
    public List<DataSampleDescriptor> getDataSamplesByCollectionId(final String collectionId) {
        final String decodedDataCollectionId = encoder.decode(collectionId);
        log.info("Request to get data samples by collection with id {} received.", decodedDataCollectionId);
        final Collection<DataSampleEntity> dataSampleEntities = dataManagementService.getDataSamplesByCollectionId(decodedDataCollectionId);
        log.info("Response to get data samples by collection with id {} processed.", decodedDataCollectionId);
        final List<DataSampleDescriptor> descriptorList = dataSampleDescriptorMapper.map(dataSampleEntities);
        descriptorList.forEach(dsd -> dsd.setDisplayName(dsd.getDisplayName().replaceAll(JSON_SUFFIX, "") + JSON_SUFFIX));
        return descriptorList;
    }

    @Override
    public DataCollectionDescriptor getDataCollectionById(String collectionId) {
        final String decodedDataCollectionId = encoder.decode(collectionId);
        log.info("Request to get collection by data collection id {} received.", decodedDataCollectionId);
        final DataCollectionEntity dataCollectionEntity = dataCollectionManagementService.get(decodedDataCollectionId);
        log.info("Response on get collection by data collection id {} processed.", decodedDataCollectionId);
        return dataCollectionDescriptorMapper.map(dataCollectionEntity);
    }
}

package com.itextpdf.dito.manager.component.uuid;

import com.itextpdf.dito.manager.dto.datacollection.DataCollectionType;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.datasample.DataSampleEntity;
import com.itextpdf.dito.manager.integration.AbstractIntegrationTest;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionRepository;
import com.itextpdf.dito.manager.repository.datasample.DataSampleRepository;
import com.itextpdf.dito.manager.service.datacollection.DataCollectionService;
import com.itextpdf.dito.manager.service.datasample.DataSampleService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class DataSampleUuidModifierTest extends AbstractIntegrationTest {
    private static final String DATACOLLECTION_NAME = "data-collection-test";
    private static final String TYPE = "JSON";
    private static final String DATASAMPLE_NAME = "name";

    @Autowired
    private DataCollectionRepository dataCollectionRepository;
    @Autowired
    private DataCollectionService dataCollectionService;
    @Autowired
    private DataSampleRepository dataSampleRepository;
    @Autowired
    private DataSampleService dataSampleService;
    @Autowired
    @Qualifier("dataSampleUuidModifier")
    private UuidModifier uuidModifier;

    @BeforeEach
    void onInit() {
        dataCollectionService.create(DATACOLLECTION_NAME, DataCollectionType.valueOf(TYPE), "{\"file\":\"data\"}".getBytes(), "datacollection.json", "admin@email.com");
        final DataCollectionEntity dataCollectionEntity = dataCollectionRepository.findByName(DATACOLLECTION_NAME).orElseThrow();

        dataSampleService.create(dataCollectionEntity, DATASAMPLE_NAME, "test.json",  "{\"file\":\"data\"}", "comment", "admin@email.com");
        dataSampleRepository.findByNameAndDataCollection(DATASAMPLE_NAME, dataCollectionEntity).ifPresent(dataSampleEntity -> {
            dataSampleEntity.setUuid(null);
            dataSampleRepository.save(dataSampleEntity);
        });
    }

    @AfterEach
    void clearDb() {
        dataCollectionRepository.findByName(DATACOLLECTION_NAME).ifPresent(dataCollectionRepository::delete);
        dataSampleRepository.deleteAll();
    }

    @Test
    void shouldUpdateDataCollection() {
        final DataCollectionEntity dataCollectionEntity = dataCollectionRepository.findByName(DATACOLLECTION_NAME).orElseThrow();
        DataSampleEntity entity = dataSampleRepository.findByNameAndDataCollection(DATASAMPLE_NAME, dataCollectionEntity).orElseThrow();
        assertNull(entity.getUuid());

        uuidModifier.updateEmptyUuid();

        entity = dataSampleRepository.findByNameAndDataCollection(DATASAMPLE_NAME, dataCollectionEntity).orElseThrow();
        assertNotNull(entity.getUuid());
    }

}

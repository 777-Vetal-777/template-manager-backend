package com.itextpdf.dito.manager.component.uuid;

import com.itextpdf.dito.manager.dto.datacollection.DataCollectionType;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.integration.AbstractIntegrationTest;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionRepository;
import com.itextpdf.dito.manager.service.datacollection.DataCollectionService;
import com.itextpdf.kernel.xmp.impl.Base64;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class DataCollectionUuidModifierTest extends AbstractIntegrationTest {
    private static final String DATACOLLECTION_NAME = "data-collection-test";
    private static final String TYPE = "JSON";

    @Autowired
    private DataCollectionRepository dataCollectionRepository;
    @Autowired
    private DataCollectionService dataCollectionService;
    @Autowired
    @Qualifier("dataCollectionUuidModifier")
    private UuidModifier uuidModifier;

    @BeforeEach
    void onInit() {
        dataCollectionService.create(DATACOLLECTION_NAME, DataCollectionType.valueOf(TYPE), "{\"file\":\"data\"}".getBytes(), "datacollection.json", "admin@email.com");
        dataCollectionRepository.findByName(DATACOLLECTION_NAME).ifPresent(dataCollectionEntity -> {
            dataCollectionEntity.setUuid(null);
            dataCollectionRepository.save(dataCollectionEntity);
        });
    }

    @AfterEach
    void clearDb() {
        dataCollectionRepository.findByName(DATACOLLECTION_NAME).ifPresent(dataCollectionRepository::delete);
    }

    @Test
    void shouldUpdateDataCollection() {
        DataCollectionEntity entity = dataCollectionRepository.findByName(DATACOLLECTION_NAME).orElseThrow();
        assertNull(entity.getUuid());

        uuidModifier.updateEmptyUuid();

        entity = dataCollectionRepository.findByName(DATACOLLECTION_NAME).orElseThrow();
        assertNotNull(entity.getUuid());
    }

}

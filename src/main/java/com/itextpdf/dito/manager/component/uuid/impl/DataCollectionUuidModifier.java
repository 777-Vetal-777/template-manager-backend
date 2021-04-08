package com.itextpdf.dito.manager.component.uuid.impl;

import com.itextpdf.dito.manager.component.uuid.UuidModifier;
import com.itextpdf.dito.manager.dto.dependency.DependencyType;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionRepository;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.UUID;

@Component
public class DataCollectionUuidModifier implements UuidModifier {

    private final DataCollectionRepository dataCollectionRepository;

    public DataCollectionUuidModifier(final DataCollectionRepository dataCollectionRepository) {
        this.dataCollectionRepository = dataCollectionRepository;
    }

    @Override
    public String getTarget() {
        return DependencyType.DATA_COLLECTION.toString();
    }

    @Override
    public void updateEmptyUuid() {
        final List<DataCollectionEntity> nullUuidDataCollections = dataCollectionRepository.findByUuidNull();

        for (final DataCollectionEntity dataCollectionEntity : nullUuidDataCollections) {
            dataCollectionEntity.setUuid(UUID.randomUUID().toString());
        }

        dataCollectionRepository.saveAll(nullUuidDataCollections);
    }

    @PostConstruct
    public void onInit() {
        updateEmptyUuid();
    }

}

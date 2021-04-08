package com.itextpdf.dito.manager.component.uuid.impl;

import com.itextpdf.dito.manager.component.uuid.UuidModifier;
import com.itextpdf.dito.manager.entity.datasample.DataSampleEntity;
import com.itextpdf.dito.manager.repository.datasample.DataSampleRepository;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Component
public class DataSampleUuidModifier implements UuidModifier {

    private final DataSampleRepository dataSampleRepository;

    public DataSampleUuidModifier(final DataSampleRepository dataSampleRepository) {
        this.dataSampleRepository = dataSampleRepository;
    }

    @Override
    public String getTarget() {
        return "DATA_SAMPLE";
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void updateEmptyUuid() {
        final List<DataSampleEntity> nullUuidDataSamples = dataSampleRepository.findByUuidNull();

        for (final DataSampleEntity dataSampleEntity : nullUuidDataSamples) {
            dataSampleEntity.setUuid(UUID.randomUUID().toString());
        }

        dataSampleRepository.saveAll(nullUuidDataSamples);
    }

    @PostConstruct
    @Transactional(rollbackOn = Exception.class)
    public void onInit() {
        updateEmptyUuid();
    }

}

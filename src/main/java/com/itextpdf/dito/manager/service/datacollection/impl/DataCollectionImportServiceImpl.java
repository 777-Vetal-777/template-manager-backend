package com.itextpdf.dito.manager.service.datacollection.impl;

import com.itextpdf.dito.editor.server.common.core.stream.Streamable;
import com.itextpdf.dito.editor.server.common.elements.entity.DataSampleElement;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionType;
import com.itextpdf.dito.manager.dto.template.setting.SettingType;
import com.itextpdf.dito.manager.dto.template.setting.TemplateImportNameModel;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.datasample.DataSampleEntity;
import com.itextpdf.dito.manager.exception.datacollection.DataCollectionAlreadyExistsException;
import com.itextpdf.dito.manager.exception.datacollection.DataCollectionNotFoundException;
import com.itextpdf.dito.manager.exception.datasample.DataSampleNotFoundException;
import com.itextpdf.dito.manager.model.template.duplicates.DuplicatesList;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionRepository;
import com.itextpdf.dito.manager.repository.datasample.DataSampleRepository;
import com.itextpdf.dito.manager.service.datacollection.DataCollectionImportService;
import com.itextpdf.dito.manager.service.datacollection.DataCollectionService;
import com.itextpdf.dito.manager.service.datasample.DataSampleService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.itextpdf.dito.manager.util.TemplateUtils.readStreamable;

@Service
public class DataCollectionImportServiceImpl implements DataCollectionImportService {

    private final DataCollectionRepository dataCollectionRepository;
    private final DataSampleRepository dataSampleRepository;
    private final DataCollectionService dataCollectionService;
    private final DataSampleService dataSampleService;

    public DataCollectionImportServiceImpl(final DataCollectionService dataCollectionService,
                                           final DataSampleService dataSampleService,
                                           final DataCollectionRepository dataCollectionRepository,
                                           final DataSampleRepository dataSampleRepository) {
        this.dataCollectionService = dataCollectionService;
        this.dataSampleService = dataSampleService;
        this.dataCollectionRepository = dataCollectionRepository;
        this.dataSampleRepository = dataSampleRepository;
    }

    private DataSampleEntity importDataSample(final String fileName, final Streamable stream, final String dataSampleName, final DataCollectionEntity entity, final String email) throws IOException {
        final byte[] json = readStreamable(stream);

        DataSampleEntity dataSampleEntity;
        try {
            dataSampleService.get(entity.getName(), dataSampleName);

            int currentNumber = dataSampleRepository.findMaxIntegerByNamePattern(fileName).orElse(0) + 1;
            dataSampleEntity = dataSampleService.create(entity, new StringBuilder(fileName).append("(").append(currentNumber).append(")").toString(), dataSampleName, new String(json), "Import template", email);
        } catch (DataSampleNotFoundException e) {
            dataSampleEntity = dataSampleService.create(entity, dataSampleName, dataSampleName, new String(json), "Import template", email);
        }
        return dataSampleEntity;
    }

    private DataCollectionEntity importDataCollection(final String fileName, final DataSampleElement dataSampleElement, final TemplateImportNameModel nameModel, final String email) throws IOException {
        final byte[] json = readStreamable(dataSampleElement.getDataSampleStream());

        DataCollectionEntity entity;
        try {
            dataCollectionService.get(dataSampleElement.getDataSampleName());

            if (nameModel == null) {
                throw new DataCollectionAlreadyExistsException(dataSampleElement.getDataSampleName());
            }

            if (Boolean.TRUE.equals(nameModel.getAllowedNewVersion())) {
                entity = dataCollectionService.createNewVersion(dataSampleElement.getDataSampleName(),
                        DataCollectionType.valueOf(dataSampleElement.getDataType().toString()),
                        json, dataSampleElement.getDataSampleName(), email, "Updated on template import");
            } else {
                int currentNumber = dataCollectionRepository.findMaxIntegerByNamePattern(fileName).orElse(0) + 1;
                entity = dataCollectionService.create(new StringBuilder(fileName).append("(").append(currentNumber).append(")").toString(),
                        DataCollectionType.valueOf(dataSampleElement.getDataType().toString()),
                        json, dataSampleElement.getDataSampleName(), email);
            }
        } catch (DataCollectionNotFoundException e) {
            entity = dataCollectionService.create(dataSampleElement.getDataSampleName(), DataCollectionType.valueOf(dataSampleElement.getDataType().toString()), json, dataSampleElement.getDataSampleName(), email);
        }
        return entity;
    }

    @Override
    public DataCollectionEntity importDataCollectionAndSamples(final String fileName,
                                                               final List<DataSampleElement> dataSamples,
                                                               final Map<String, TemplateImportNameModel> dataCollectionSettings,
                                                               final DuplicatesList duplicatesList,
                                                               final String email) throws IOException {
        DataCollectionEntity dataCollectionEntity = null;

        if (dataSamples != null && !dataSamples.isEmpty()) {
            final DataSampleElement dataSample = dataSamples.get(0);
            try {
                dataCollectionEntity =
                        importDataCollection(fileName,
                                dataSample,
                                dataCollectionSettings.get(dataSample.getDataSampleName()), email);

                for (final var dataSampleElement : dataSamples) {
                    importDataSample(fileName, dataSampleElement.getDataSampleStream(),
                            dataSampleElement.getDataSampleName(),
                            dataCollectionEntity,
                            email);
                }

            } catch (DataCollectionAlreadyExistsException e) {
                duplicatesList.putToDuplicates(SettingType.DATA_COLLECTION, dataSample.getDataSampleName());
                dataCollectionEntity = null;
            }
        }

        return dataCollectionEntity;
    }

}

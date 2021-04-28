package com.itextpdf.dito.manager.component.template.dtm.read.impl;

import com.itextpdf.dito.manager.component.template.dtm.read.DtmFileItemReader;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionType;
import com.itextpdf.dito.manager.dto.template.setting.SettingType;
import com.itextpdf.dito.manager.dto.template.setting.TemplateImportNameModel;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.exception.datacollection.DataCollectionAlreadyExistsException;
import com.itextpdf.dito.manager.exception.datacollection.DataCollectionNotFoundException;
import com.itextpdf.dito.manager.exception.template.TemplateImportProjectException;
import com.itextpdf.dito.manager.model.template.dtm.DtmFileDescriptorModel;
import com.itextpdf.dito.manager.model.template.dtm.ItemType;
import com.itextpdf.dito.manager.model.template.dtm.context.DtmFileImportContext;
import com.itextpdf.dito.manager.model.template.dtm.datacollection.DtmDataCollectionDescriptorModel;
import com.itextpdf.dito.manager.model.template.dtm.datacollection.DtmDataCollectionVersionDescriptorModel;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionRepository;
import com.itextpdf.dito.manager.service.datacollection.DataCollectionService;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class DtmDataCollectionReaderImpl implements DtmFileItemReader {
    private final DataCollectionService dataCollectionService;
    private final DataCollectionRepository dataCollectionRepository;

    public DtmDataCollectionReaderImpl(final DataCollectionService dataCollectionService,
                                       final DataCollectionRepository dataCollectionRepository) {
        this.dataCollectionService = dataCollectionService;
        this.dataCollectionRepository = dataCollectionRepository;
    }

    @Override
    public void read(final DtmFileImportContext context,
                     final DtmFileDescriptorModel model,
                     final Path basePath) {
        model.getDataCollections().forEach(dataCollectionModel -> {
                    DataCollectionEntity dataCollectionEntity;
                    try {
                        dataCollectionEntity = dataCollectionService.get(dataCollectionModel.getName());

                        final TemplateImportNameModel nameModel = Optional.ofNullable(context.getSettings(SettingType.DATA_COLLECTION))
                                .map(setting -> setting.get(dataCollectionModel.getName()))
                                .orElseThrow(() -> new DataCollectionAlreadyExistsException(dataCollectionModel.getName()));

                        if (Boolean.TRUE.equals(nameModel.getAllowedNewVersion())) {
                            dataCollectionEntity = makeImportVersions(context, basePath, dataCollectionEntity.getName(), dataCollectionModel, dataCollectionEntity);
                        } else {
                            int currentNumber = dataCollectionRepository.findMaxIntegerByNamePattern(context.getFileName()).orElse(0) + 1;
                            dataCollectionEntity = makeImportVersions(context, basePath, new StringBuilder(context.getFileName()).append("(").append(currentNumber).append(")").toString(), dataCollectionModel, null);
                        }

                    } catch (DataCollectionNotFoundException e) {
                        dataCollectionEntity = makeImportVersions(context, basePath, dataCollectionModel.getName(), dataCollectionModel, null);
                    } catch (DataCollectionAlreadyExistsException e) {
                        context.putToDuplicates(SettingType.DATA_COLLECTION, dataCollectionModel.getName());
                    }
                }
        );
    }

    private DataCollectionEntity makeImportVersions(final DtmFileImportContext context,
                                                    final Path basePath,
                                                    final String dataCollectionName,
                                                    final DtmDataCollectionDescriptorModel dataCollectionModel,
                                                    final DataCollectionEntity initialEntity) {
        DataCollectionEntity dataCollectionEntity = initialEntity;
        final List<DtmDataCollectionVersionDescriptorModel> descriptorModels = Optional.of(dataCollectionModel.getVersions())
                .stream().flatMap(List::stream)
                .sorted(Comparator.comparingLong(DtmDataCollectionVersionDescriptorModel::getVersion))
                .collect(Collectors.toList());
        for (final DtmDataCollectionVersionDescriptorModel version : descriptorModels) {
            try {
                if (dataCollectionEntity == null) {
                    dataCollectionEntity = dataCollectionService.create(dataCollectionName, DataCollectionType.valueOf(dataCollectionModel.getType().toString()), Files.readAllBytes(basePath.resolve(version.getLocalPath())), version.getFileName(), context.getEmail());
                    context.map(dataCollectionModel.getId(), dataCollectionEntity);
                } else {
                    dataCollectionEntity = dataCollectionService.createNewVersion(dataCollectionEntity.getName(), dataCollectionEntity.getType(), Files.readAllBytes(basePath.resolve(version.getLocalPath())), version.getFileName(), context.getEmail(), version.getComment());
                }
                context.map(dataCollectionModel.getId(), version.getVersion(), dataCollectionEntity.getLatestVersion());
            } catch (IOException ioException) {
                throw new TemplateImportProjectException("Importing archive is broken or corrupted, could not load file " + version.getLocalPath() + " for data collection", ioException);
            }
        }
        return dataCollectionEntity;
    }

    @Override
    public ItemType getType() {
        return ItemType.DATA_COLLECTION;
    }

    @Override
    public int getPriority() {
        return 10;
    }
}

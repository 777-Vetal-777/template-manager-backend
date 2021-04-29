package com.itextpdf.dito.manager.component.template.dtm.read.impl;

import com.itextpdf.dito.manager.component.template.dtm.read.DtmFileItemReader;
import com.itextpdf.dito.manager.dto.template.setting.SettingType;
import com.itextpdf.dito.manager.dto.template.setting.TemplateImportNameModel;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.datasample.DataSampleEntity;
import com.itextpdf.dito.manager.exception.datacollection.DataCollectionNotFoundException;
import com.itextpdf.dito.manager.exception.datasample.DataSampleAlreadyExistsException;
import com.itextpdf.dito.manager.exception.template.TemplateImportProjectException;
import com.itextpdf.dito.manager.model.template.dtm.DtmFileDescriptorModel;
import com.itextpdf.dito.manager.model.template.dtm.ItemType;
import com.itextpdf.dito.manager.model.template.dtm.context.DtmFileImportContext;
import com.itextpdf.dito.manager.model.template.dtm.datacollection.DtmDataCollectionDescriptorModel;
import com.itextpdf.dito.manager.model.template.dtm.datasample.DtmDataSampleDescriptorModel;
import com.itextpdf.dito.manager.model.template.dtm.datasample.DtmDataSampleVersionDescriptorModel;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionRepository;
import com.itextpdf.dito.manager.repository.datasample.DataSampleRepository;
import com.itextpdf.dito.manager.service.datasample.DataSampleService;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class DtmDataSampleReaderImpl implements DtmFileItemReader {

    private final DataCollectionRepository dataCollectionRepository;
    private final DataSampleRepository dataSampleRepository;
    private final DataSampleService dataSampleService;

    public DtmDataSampleReaderImpl(final DataCollectionRepository dataCollectionRepository,
                                   final DataSampleRepository dataSampleRepository,
                                   final DataSampleService dataSampleService) {
        this.dataCollectionRepository = dataCollectionRepository;
        this.dataSampleRepository = dataSampleRepository;
        this.dataSampleService = dataSampleService;
    }

    @Override
    public void read(final DtmFileImportContext context,
                     final DtmFileDescriptorModel model,
                     final Path basePath) {
        final Map<Long, List<DtmDataSampleDescriptorModel>> dataSamplesMap = getDataSamplesMap(context, model);

        dataSamplesMap.forEach((dataCollectionId, dtmDataSampleDescriptorModels) -> {
                    final DataCollectionEntity dataCollectionEntity = dataCollectionRepository.getOne(dataCollectionId);
                    dtmDataSampleDescriptorModels.forEach(dataSampleModel -> {
                                DataSampleEntity dataSampleEntity;
                                try {
                                    dataSampleEntity = dataSampleService.get(dataSampleModel.getName(), dataCollectionEntity.getName());

                                    final TemplateImportNameModel nameModel = Optional.ofNullable(context.getSettings(SettingType.DATA_SAMPLE))
                                            .map(setting -> setting.get(dataSampleModel.getName()))
                                            .orElseThrow(() -> new DataSampleAlreadyExistsException(dataSampleModel.getName()));

                                    if (Boolean.TRUE.equals(nameModel.getAllowedNewVersion())) {
                                        dataSampleEntity = makeImportVersions(context, dataCollectionEntity, basePath, dataSampleEntity.getName(), dataSampleModel, dataSampleEntity);
                                    } else {
                                        int currentNumber = dataSampleRepository.findMaxIntegerByNamePattern(context.getFileName()).orElse(0) + 1;
                                        dataSampleEntity = makeImportVersions(context, dataCollectionEntity, basePath, new StringBuilder(context.getFileName()).append("(").append(currentNumber).append(")").toString(), dataSampleModel, null);
                                    }

                                } catch (DataCollectionNotFoundException e) {
                                    dataSampleEntity = makeImportVersions(context, dataCollectionEntity, basePath, dataSampleModel.getName(), dataSampleModel, null);
                                } catch (DataSampleAlreadyExistsException e) {
                                    context.putToDuplicates(SettingType.DATA_SAMPLE, dataSampleModel.getName());
                                }
                            }
                    );
                }
        );

    }

    private Map<Long, List<DtmDataSampleDescriptorModel>> getDataSamplesMap(final DtmFileImportContext context,
                                                                            final DtmFileDescriptorModel model) {
        final Map<Long, List<DtmDataSampleDescriptorModel>> result = new HashMap<>();
        for (final DtmDataCollectionDescriptorModel dataCollection : model.getDataCollections()) {
            final Long key = context.getCollectionMapping(dataCollection.getId());
            if (key != null) {
                result.put(key,
                        model.getDataSamples().stream()
                                .filter(dtmDataSampleDescriptorModel -> dataCollection.getSamples().contains(dtmDataSampleDescriptorModel.getId()))
                                .collect(Collectors.toList()));
            }
        }
        return result;
    }

    private DataSampleEntity makeImportVersions(final DtmFileImportContext context,
                                                final DataCollectionEntity dataCollectionEntity,
                                                final Path basePath,
                                                final String dataSampleName,
                                                final DtmDataSampleDescriptorModel dataSampleModel,
                                                final DataSampleEntity initialEntity) {
        DataSampleEntity dataSampleEntity = initialEntity;
        final List<DtmDataSampleVersionDescriptorModel> descriptorModels = Optional.of(dataSampleModel.getVersions())
                .stream().flatMap(List::stream)
                .sorted(Comparator.comparingLong(DtmDataSampleVersionDescriptorModel::getVersion))
                .collect(Collectors.toList());
        for (final DtmDataSampleVersionDescriptorModel version : descriptorModels) {
            try {
                if (dataSampleEntity == null) {
                    dataSampleEntity = dataSampleService.create(dataCollectionEntity, dataSampleName, version.getFileName(), Files.readString(basePath.resolve(version.getLocalPath())), version.getComment(), context.getEmail());
                    context.map(dataSampleModel.getId(), dataSampleEntity);
                } else {
                    dataSampleEntity = dataSampleService.createNewVersion(dataCollectionEntity.getName(), dataSampleName, Files.readString(basePath.resolve(version.getLocalPath())), version.getFileName(), context.getEmail(), version.getComment());
                }
                context.map(dataSampleModel.getId(), version.getVersion(), dataSampleEntity.getLatestVersion());
            } catch (IOException ioException) {
                throw new TemplateImportProjectException("Importing archive is broken or corrupted, could not load file " + version.getLocalPath() + " for data sample", ioException);
            }
        }
        return dataSampleEntity;
    }

    @Override
    public ItemType getType() {
        return ItemType.DATA_SAMPLE;
    }
}

package com.itextpdf.dito.manager.component.template.dtm.model.impl;

import com.itextpdf.dito.manager.component.mapper.template.dtm.LocalPathMapper;
import com.itextpdf.dito.manager.component.template.dtm.model.DtmItemModelExtractor;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionFileEntity;
import com.itextpdf.dito.manager.entity.datasample.DataSampleEntity;
import com.itextpdf.dito.manager.entity.datasample.DataSampleFileEntity;
import com.itextpdf.dito.manager.model.template.dtm.DtmFileDescriptorModel;
import com.itextpdf.dito.manager.model.template.dtm.ItemType;
import com.itextpdf.dito.manager.model.template.dtm.context.DtmFileExportContext;
import com.itextpdf.dito.manager.model.template.dtm.datasample.DtmDataSampleDescriptorModel;
import com.itextpdf.dito.manager.model.template.dtm.datasample.DtmDataSampleVersionDescriptorModel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Component
public class DtmDataSampleModelExtractorImpl implements DtmItemModelExtractor {

    private final LocalPathMapper localPathMapper;

    public DtmDataSampleModelExtractorImpl(final LocalPathMapper localPathMapper) {
        this.localPathMapper = localPathMapper;
    }

    @Override
    public ItemType getType() {
        return ItemType.DATA_SAMPLE;
    }

    @Override
    public DtmFileDescriptorModel extract(final DtmFileExportContext context, final DtmFileDescriptorModel model) {
        if (context.isExportDependencies()) {
            final AtomicLong currentId = new AtomicLong(1);

            final Map<DataSampleEntity, List<DataSampleFileEntity>> collectedSamples = context
                    .getTemplateFileEntities()
                    .stream()
                    .flatMap(templateFileEntity -> Optional.ofNullable(templateFileEntity.getDataCollectionFile()).map(DataCollectionFileEntity::getDataCollection).map(DataCollectionEntity::getDataSamples).stream())
                    .flatMap(Collection::stream)
                    .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparingLong(DataSampleEntity::getId))), ArrayList::new))
                    .stream()
                    .flatMap(dataSampleEntity -> dataSampleEntity.getVersions().stream())
                    .collect(Collectors.groupingBy(DataSampleFileEntity::getDataSample));
            final List<DtmDataSampleDescriptorModel> samplesModels = new ArrayList<>();

            collectedSamples.forEach((sampleEntity, sampleFiles) -> {
                        context.map(sampleEntity, currentId.getAndIncrement());
                        final DtmDataSampleDescriptorModel descriptorModel = new DtmDataSampleDescriptorModel();
                        descriptorModel.setId(context.getMapping(sampleEntity));
                        descriptorModel.setName(sampleEntity.getName());
                        descriptorModel.setDescription(sampleEntity.getDescription());
                        descriptorModel.setVersions(mapVersionDescriptors(sampleFiles, context));
                        samplesModels.add(descriptorModel);
                    }
            );

            model.setDataSamples(samplesModels);
        }
        return model;
    }

    private List<DtmDataSampleVersionDescriptorModel> mapVersionDescriptors(final List<DataSampleFileEntity> sampleVersions,
                                                                            final DtmFileExportContext context) {
        final List<DtmDataSampleVersionDescriptorModel> versionDescriptors = new ArrayList<>();
        final AtomicLong currentVersion = new AtomicLong(1L);
        sampleVersions.stream().sorted(Comparator.comparing(DataSampleFileEntity::getVersion)).forEachOrdered(
                (sampleFileEntities) -> {
                    final DtmDataSampleVersionDescriptorModel sampleVersionModel = new DtmDataSampleVersionDescriptorModel();
                    context.map(sampleFileEntities, currentVersion.getAndIncrement());
                    sampleVersionModel.setVersion(context.getMapping(sampleFileEntities));
                    sampleVersionModel.setComment(sampleFileEntities.getComment());
                    sampleVersionModel.setFileName(sampleFileEntities.getFileName());
                    sampleVersionModel.setLocalPath(localPathMapper.getLocalPath(sampleFileEntities));
                    versionDescriptors.add(sampleVersionModel);
                }
        );
        return versionDescriptors;
    }

}
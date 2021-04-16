package com.itextpdf.dito.manager.component.template.dtm.model.impl;

import com.itextpdf.dito.manager.component.mapper.template.dtm.LocalPathMapper;
import com.itextpdf.dito.manager.component.template.dtm.model.DtmItemModelExtractor;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionFileEntity;
import com.itextpdf.dito.manager.model.template.dtm.DtmFileDescriptorModel;
import com.itextpdf.dito.manager.model.template.dtm.ItemType;
import com.itextpdf.dito.manager.model.template.dtm.context.DtmFileExportContext;
import com.itextpdf.dito.manager.model.template.dtm.datacollection.DtmDataCollectionDescriptorModel;
import com.itextpdf.dito.manager.model.template.dtm.datacollection.DtmDataCollectionUsedInDescriptorModel;
import com.itextpdf.dito.manager.model.template.dtm.datacollection.DtmDataCollectionVersionDescriptorModel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class DtmDataCollectionModelExtractorImpl implements DtmItemModelExtractor {

    private final LocalPathMapper localPathMapper;

    public DtmDataCollectionModelExtractorImpl(final LocalPathMapper localPathMapper) {
        this.localPathMapper = localPathMapper;
    }

    @Override
    public ItemType getType() {
        return ItemType.DATA_COLLECTION;
    }

    @Override
    public DtmFileDescriptorModel extract(final DtmFileExportContext context, final DtmFileDescriptorModel model) {
        if (context.isExportDependencies()) {
            final AtomicLong currentId = new AtomicLong(1);
            final Map<DataCollectionEntity, Set<DataCollectionFileEntity>> dataCollections = context.getTemplateFileEntities()
                    .stream()
                    .flatMap(templateFileEntity -> Optional.ofNullable(templateFileEntity.getDataCollectionFile()).stream())
                    .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparingLong(DataCollectionFileEntity::getId))), ArrayList::new))
                    .stream()
                    .collect(Collectors.groupingBy(DataCollectionFileEntity::getDataCollection, Collectors.toSet()));

            final List<DtmDataCollectionDescriptorModel> descriptors = new ArrayList<>();
            dataCollections.forEach((entity, set) -> {
                        final DtmDataCollectionDescriptorModel descriptorModel = new DtmDataCollectionDescriptorModel();
                        descriptorModel.setId(Long.valueOf(currentId.getAndIncrement()).toString());
                        context.map(entity, currentId.get());
                        descriptorModel.setName(entity.getName());
                        descriptorModel.setType(entity.getType());
                        descriptorModel.setDescription(entity.getDescription());
                        descriptorModel.setVersions(extractVersions(set, context));
                        descriptorModel.setSamples(entity.getDataSamples().stream().map(context::getMapping).map(Objects::toString).collect(Collectors.toList()));
                        descriptors.add(descriptorModel);
                    }
            );
            model.setDataCollections(descriptors);
        }
        return model;
    }

    private List<DtmDataCollectionVersionDescriptorModel> extractVersions(Set<DataCollectionFileEntity> list, DtmFileExportContext context) {
        final List<DtmDataCollectionVersionDescriptorModel> dataCollectionVersions = new ArrayList<>();
        final AtomicLong currentVersion = new AtomicLong(1);
        list.stream()
                .sorted(Comparator.comparing(DataCollectionFileEntity::getVersion))
                .forEachOrdered(version -> {
                    final DtmDataCollectionVersionDescriptorModel versionModel = new DtmDataCollectionVersionDescriptorModel();
                    versionModel.setVersion(currentVersion.getAndIncrement());
                    context.map(version, currentVersion.get());
                    versionModel.setComment(versionModel.getComment());
                    versionModel.setFileName(versionModel.getFileName());
                    versionModel.setLocalPath(generateDataCollectionLocalPath(version));
                    final List<DtmDataCollectionUsedInDescriptorModel> versionUsedIn = version.getTemplateFiles().stream().flatMap(templateFileEntity -> {
                        final Stream<DtmDataCollectionUsedInDescriptorModel> result;
                        if (context.getMapping(templateFileEntity) != null) {
                            final DtmDataCollectionUsedInDescriptorModel usedIn = new DtmDataCollectionUsedInDescriptorModel();
                            usedIn.setId(context.getMapping(templateFileEntity.getTemplate()));
                            usedIn.setType(ItemType.TEMPLATE.getPluralName());
                            usedIn.setVersion(context.getMapping(templateFileEntity));
                            result = Stream.of(usedIn);
                        } else {
                            result = Stream.empty();
                        }
                        return result;
                    }).collect(Collectors.toList());
                    versionModel.setUsedIn(versionUsedIn);
                    dataCollectionVersions.add(versionModel);
                });
        return dataCollectionVersions;
    }

    //put in constant
    private String generateDataCollectionLocalPath(final DataCollectionFileEntity entity) {
        return localPathMapper.getLocalPath(entity);
    }

    @Override
    public int getPriority() {
        return 75;
    }
}

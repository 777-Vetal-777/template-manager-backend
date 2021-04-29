package com.itextpdf.dito.manager.component.template.dtm.file.impl;

import com.itextpdf.dito.manager.component.mapper.template.dtm.LocalPathMapper;
import com.itextpdf.dito.manager.component.template.dtm.file.DtmItemFileExtractor;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionFileEntity;
import com.itextpdf.dito.manager.entity.datasample.DataSampleEntity;
import com.itextpdf.dito.manager.entity.datasample.DataSampleFileEntity;
import com.itextpdf.dito.manager.model.template.dtm.ItemType;
import com.itextpdf.dito.manager.model.template.dtm.context.DtmFileExportContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static java.nio.file.StandardOpenOption.CREATE;

@Component
public class DtmDataSampleFileExtractorImpl implements DtmItemFileExtractor {
    private final LocalPathMapper localPathMapper;

    public DtmDataSampleFileExtractorImpl(final LocalPathMapper localPathMapper) {
        this.localPathMapper = localPathMapper;
    }

    @Override
    public ItemType getType() {
        return ItemType.DATA_COLLECTION;
    }

    @Override
    public Path extract(final Path directory, final DtmFileExportContext context) throws IOException {
        final Path templateFolder = directory.resolve(Paths.get(localPathMapper.getDataSampleBasePath()));
        Files.createDirectory(templateFolder);

        if (context.isExportDependencies()) {
            final List<DataSampleFileEntity> dataSampleFileEntities = context
                    .getTemplateFileEntities()
                    .stream()
                    .flatMap(templateFileEntity -> Optional.ofNullable(templateFileEntity.getDataCollectionFile()).map(DataCollectionFileEntity::getDataCollection).map(DataCollectionEntity::getDataSamples).stream())
                    .flatMap(Collection::stream)
                    .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparingLong(DataSampleEntity::getId))), ArrayList::new))
                    .stream()
                    .flatMap(dataSampleEntity -> dataSampleEntity.getVersions().stream()).collect(Collectors.toList());

            for (final DataSampleFileEntity dataSampleFileEntity : dataSampleFileEntities) {
                Files.write(directory.resolve(Path.of(localPathMapper.getLocalPath(dataSampleFileEntity))), dataSampleFileEntity.getData(), CREATE);
            }
        }
        return templateFolder;
    }
}

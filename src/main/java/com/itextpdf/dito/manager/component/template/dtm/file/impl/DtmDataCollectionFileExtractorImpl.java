package com.itextpdf.dito.manager.component.template.dtm.file.impl;

import com.itextpdf.dito.manager.component.mapper.template.dtm.LocalPathMapper;
import com.itextpdf.dito.manager.component.template.dtm.file.DtmItemFileExtractor;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionFileEntity;
import com.itextpdf.dito.manager.model.template.dtm.ItemType;
import com.itextpdf.dito.manager.model.template.dtm.context.DtmFileExportContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static java.nio.file.StandardOpenOption.CREATE;

@Component
public class DtmDataCollectionFileExtractorImpl implements DtmItemFileExtractor {

    private final LocalPathMapper localPathMapper;

    public DtmDataCollectionFileExtractorImpl(final LocalPathMapper localPathMapper) {
        this.localPathMapper = localPathMapper;
    }

    @Override
    public ItemType getType() {
        return ItemType.DATA_COLLECTION;
    }

    @Override
    public Path extract(Path directory, DtmFileExportContext context) throws IOException {
        final Path templateFolder = directory.resolve(Paths.get(localPathMapper.getDataCollectionBasePath()));
        Files.createDirectory(templateFolder);

        if (context.isExportDependencies()) {
            final List<DataCollectionFileEntity> dataCollectionFileEntities = context
                    .getTemplateFileEntities()
                    .stream()
                    .flatMap(templateFileEntity -> Optional.ofNullable(templateFileEntity.getDataCollectionFile()).stream())
                    .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparingLong(DataCollectionFileEntity::getId))), ArrayList::new));

            for (final DataCollectionFileEntity dataCollectionFileEntity : dataCollectionFileEntities) {
                Files.write(directory.resolve(Path.of(localPathMapper.getLocalPath(dataCollectionFileEntity))), dataCollectionFileEntity.getData(), CREATE);
            }
        }

        return templateFolder;
    }
}

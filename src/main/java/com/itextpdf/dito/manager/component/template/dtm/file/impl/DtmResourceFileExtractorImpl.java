package com.itextpdf.dito.manager.component.template.dtm.file.impl;

import com.itextpdf.dito.manager.component.mapper.template.dtm.LocalPathMapper;
import com.itextpdf.dito.manager.component.template.dtm.file.DtmItemFileExtractor;
import com.itextpdf.dito.manager.entity.resource.ResourceFileEntity;
import com.itextpdf.dito.manager.model.template.dtm.ItemType;
import com.itextpdf.dito.manager.model.template.dtm.context.DtmFileExportContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static java.nio.file.StandardOpenOption.CREATE;

@Component
public class DtmResourceFileExtractorImpl implements DtmItemFileExtractor {

    private final LocalPathMapper localPathMapper;

    public DtmResourceFileExtractorImpl(final LocalPathMapper localPathMapper) {
        this.localPathMapper = localPathMapper;
    }

    @Override
    public ItemType getType() {
        return ItemType.RESOURCE;
    }

    @Override
    public Path extract(final Path directory, final DtmFileExportContext context) throws IOException {
        final Path templateFolder = directory.resolve(Paths.get(localPathMapper.getResourceBasePath()));
        Files.createDirectory(templateFolder);

        if (context.isExportDependencies()) {
            final ArrayList<ResourceFileEntity> resourceFileEntities = context
                    .getTemplateFileEntities()
                    .stream()
                    .flatMap(templateFileEntity -> templateFileEntity.getResourceFiles().stream())
                    .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparingLong(ResourceFileEntity::getId))), ArrayList::new));

            for (final ResourceFileEntity resourceFileEntity : resourceFileEntities) {
                Files.write(directory.resolve(Path.of(localPathMapper.getLocalPath(resourceFileEntity))), resourceFileEntity.getFile(), CREATE);
            }
        }
        return templateFolder;
    }
}

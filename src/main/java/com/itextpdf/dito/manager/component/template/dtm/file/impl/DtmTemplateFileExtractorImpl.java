package com.itextpdf.dito.manager.component.template.dtm.file.impl;

import com.itextpdf.dito.manager.component.mapper.template.dtm.LocalPathMapper;
import com.itextpdf.dito.manager.component.template.dtm.file.DtmItemFileExtractor;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;
import com.itextpdf.dito.manager.model.template.dtm.ItemType;
import com.itextpdf.dito.manager.model.template.dtm.context.DtmFileExportContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.StandardOpenOption.CREATE;

@Component
public class DtmTemplateFileExtractorImpl implements DtmItemFileExtractor {

    private final LocalPathMapper localPathMapper;

    public DtmTemplateFileExtractorImpl(final LocalPathMapper localPathMapper) {
        this.localPathMapper = localPathMapper;
    }

    @Override
    public ItemType getType() {
        return ItemType.TEMPLATE;
    }

    @Override
    public Path extract(final Path directory, final DtmFileExportContext context) throws IOException {
        final Path templateFolder = directory.resolve(Paths.get(localPathMapper.getTemplateBasePath()));
        Files.createDirectory(templateFolder);

        for (final TemplateFileEntity templateFileEntity : context.getTemplateFileEntities()) {
            Files.write(directory.resolve(Path.of(localPathMapper.getLocalPath(templateFileEntity))), templateFileEntity.getData(), CREATE);
        }

        return templateFolder;
    }
}

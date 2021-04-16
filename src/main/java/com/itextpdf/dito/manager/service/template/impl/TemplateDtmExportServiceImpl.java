package com.itextpdf.dito.manager.service.template.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.dito.manager.component.template.dtm.file.DtmFileExtractor;
import com.itextpdf.dito.manager.component.template.dtm.model.DtmModelExtractor;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.exception.template.TemplateProjectGenerationException;
import com.itextpdf.dito.manager.model.template.TemplateExportVersion;
import com.itextpdf.dito.manager.model.template.dtm.DtmFileDescriptorModel;
import com.itextpdf.dito.manager.model.template.dtm.context.DtmFileExportContext;
import com.itextpdf.dito.manager.service.template.TemplateDtmExportService;
import com.itextpdf.dito.manager.service.template.TemplateService;
import com.itextpdf.dito.manager.util.FilesUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.CREATE;

@Service
public class TemplateDtmExportServiceImpl implements TemplateDtmExportService {
    private final TemplateService templateService;
    private final DtmFileExtractor fileExtractor;
    private final DtmModelExtractor modelExtractor;
    private final BuildProperties buildProperties;
    private final ObjectMapper objectMapper;

    public TemplateDtmExportServiceImpl(final TemplateService templateService,
                                        final DtmFileExtractor fileExtractor,
                                        final DtmModelExtractor modelExtractor,
                                        final BuildProperties buildProperties,
                                        final ObjectMapper objectMapper) {
        this.templateService = templateService;
        this.fileExtractor = fileExtractor;
        this.modelExtractor = modelExtractor;
        this.buildProperties = buildProperties;
        this.objectMapper = objectMapper;
    }

    @Override
    public byte[] export(String templateName, boolean exportDependencies, TemplateExportVersion versionsToExport) {
        final TemplateEntity templateEntity = templateService.get(templateName);

        final Path tempDirectory;
        final Path baseTempDirectory = FilesUtils.TEMP_DIRECTORY.toPath();
        try {
            tempDirectory = Files.createTempDirectory(baseTempDirectory, templateName);
        } catch (IOException e) {
            throw new TemplateProjectGenerationException("Could not create temporary directory: ".concat(e.getMessage()));
        }

        final DtmFileDescriptorModel model = new DtmFileDescriptorModel("DTM2", buildProperties.getVersion());

        final DtmFileExportContext context;
        if (TemplateExportVersion.ALL.equals(versionsToExport)) {
            context = new DtmFileExportContext(templateEntity, exportDependencies);
        } else {
            context = new DtmFileExportContext(templateEntity.getLatestFile(), exportDependencies);
        }

        final Path zipFile;
        try {
            zipFile = Files.createTempFile(baseTempDirectory, templateName, ".dito");
        } catch (IOException e) {
            FileUtils.deleteQuietly(tempDirectory.toFile());
            throw new TemplateProjectGenerationException("Could not create temporary file: ".concat(e.getMessage()));
        }

        try {
            modelExtractor.extract(context, model);
            fileExtractor.extractFiles(tempDirectory, context);
            Files.write(Path.of(tempDirectory.toAbsolutePath().toString(), "meta.json"), objectMapper.writeValueAsBytes(model), CREATE);
            FilesUtils.zipFolder(tempDirectory, zipFile);
            return Files.readAllBytes(zipFile);
        } catch (IOException e) {
            throw new TemplateProjectGenerationException("Error during export files: ".concat(e.getMessage()));
        } finally {
            FileUtils.deleteQuietly(tempDirectory.toFile());
            FileUtils.deleteQuietly(zipFile.toFile());
        }
    }

}

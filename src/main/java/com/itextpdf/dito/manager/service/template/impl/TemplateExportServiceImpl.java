package com.itextpdf.dito.manager.service.template.impl;

import com.itextpdf.dito.manager.entity.datasample.DataSampleEntity;
import com.itextpdf.dito.manager.entity.datasample.DataSampleFileEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.exception.template.TemplateProjectGenerationException;
import com.itextpdf.dito.manager.service.datasample.DataSampleService;
import com.itextpdf.dito.manager.service.template.TemplateExportService;
import com.itextpdf.dito.manager.service.template.TemplateProjectGenerator;
import com.itextpdf.dito.manager.service.template.TemplateService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static com.itextpdf.dito.manager.util.FilesUtils.zipFile;
import static com.itextpdf.dito.manager.util.FilesUtils.zipFolder;
import static org.apache.commons.io.FileUtils.deleteQuietly;

@Service
public class TemplateExportServiceImpl implements TemplateExportService {

    private static final Logger log = LogManager.getLogger(TemplateExportServiceImpl.class);

    private final TemplateService templateService;
    private final DataSampleService dataSampleService;
    private final TemplateProjectGenerator templateProjectGenerator;

    public TemplateExportServiceImpl(final TemplateService templateService,
                                     final DataSampleService dataSampleService,
                                     final TemplateProjectGenerator templateProjectGenerator) {
        this.templateService = templateService;
        this.dataSampleService = dataSampleService;
        this.templateProjectGenerator = templateProjectGenerator;
    }

    @Override
    public byte[] export(final String templateName) {
        final TemplateEntity templateEntity = templateService.get(templateName);
        final File zippedProject = exportToDito(templateEntity);
        final File result;

        try {
            result = zipFile(Path.of(templateEntity.getName().concat(".zip")), zippedProject.toPath());
        } catch (IOException e) {
            log.error(e);
            throw new TemplateProjectGenerationException("Error while packing zipped project");
        } finally {
            deleteQuietly(zippedProject);
        }

        try {
            return Files.readAllBytes(result.toPath());
        } catch (IOException e) {
            log.error(e);
            throw new TemplateProjectGenerationException("Error while reading zipped project");
        } finally {
            deleteQuietly(result);
        }

    }

    private File exportToDito(final TemplateEntity templateEntity) {
        final List<DataSampleFileEntity> dataSampleFileEntities = dataSampleService.getListByTemplateName(templateEntity.getName()).stream().map(DataSampleEntity::getLatestVersion).collect(Collectors.toList());

        final File zippedProject = templateProjectGenerator.generateZippedProjectByTemplate(templateEntity, dataSampleFileEntities);
        return zippedProject;
    }

/*
    private byte[] exportToDito(final String templateName) {
        final TemplateEntity templateEntity = templateService.get(templateName);
        final List<DataSampleFileEntity> dataSampleFileEntities = dataSampleService.getListByTemplateName(templateName).stream().map(DataSampleEntity::getLatestVersion).collect(Collectors.toList());

        final File zippedProject = templateProjectGenerator.generateZippedProjectByTemplate(templateEntity, dataSampleFileEntities);
        try {
            return Files.readAllBytes(zippedProject.toPath());
        } catch (IOException e) {
            log.error(e);
            throw new TemplateProjectGenerationException("Error while reading zipped project");
        } finally {
            deleteQuietly(zippedProject);
        }
    }
*/


}

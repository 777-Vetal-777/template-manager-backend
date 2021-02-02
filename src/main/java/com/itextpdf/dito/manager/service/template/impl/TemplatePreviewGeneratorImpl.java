package com.itextpdf.dito.manager.service.template.impl;

import com.itextpdf.dito.manager.entity.datasample.DataSampleEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.exception.template.TemplateNotFoundException;
import com.itextpdf.dito.manager.exception.template.TemplatePreviewGenerationException;
import com.itextpdf.dito.manager.repository.datasample.DataSampleRepository;
import com.itextpdf.dito.manager.repository.template.TemplateRepository;
import com.itextpdf.dito.manager.service.template.TemplatePreviewGenerator;
import com.itextpdf.dito.manager.service.template.TemplateProjectGenerator;
import com.itextpdf.dito.manager.util.FilesUtils;
import com.itextpdf.dito.sdk.core.data.IExplicitTemplateData;
import com.itextpdf.dito.sdk.core.data.JsonData;
import com.itextpdf.dito.sdk.core.preprocess.ExtendedProjectPreprocessor;
import com.itextpdf.dito.sdk.core.preprocess.asset.TemplateAssetRetriever;
import com.itextpdf.dito.sdk.output.PdfProducer;
import com.itextpdf.dito.sdk.output.PdfProducerProperties;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Service
public class TemplatePreviewGeneratorImpl implements TemplatePreviewGenerator {

    private static final Logger log = LogManager.getLogger(TemplatePreviewGeneratorImpl.class);

    private final TemplateRepository templateRepository;
    private final TemplateProjectGenerator templateProjectGenerator;
    private final TemplateAssetRetriever resourceAssetRetriever;
    private final TemplateAssetRetriever templateAssetRetriever;
    private final DataSampleRepository dataSampleRepository;

    public TemplatePreviewGeneratorImpl(final TemplateRepository templateRepository,
                                        final TemplateProjectGenerator templateProjectGenerator,
                                        final TemplateAssetRetriever resourceAssetRetriever,
                                        final TemplateAssetRetriever templateAssetRetriever,
                                        final DataSampleRepository dataSampleRepository) {
        this.templateRepository = templateRepository;
        this.templateProjectGenerator = templateProjectGenerator;
        this.resourceAssetRetriever = resourceAssetRetriever;
        this.templateAssetRetriever = templateAssetRetriever;
        this.dataSampleRepository = dataSampleRepository;
    }

    @Override
    public OutputStream generatePreview(final String templateName) {
        final TemplateEntity templateEntity = getTemplateByName(templateName);
        if (CollectionUtils.isEmpty(templateEntity.getFiles())) {
            throw new IllegalArgumentException(
                    new StringBuilder().append("No file found for template ").append(templateEntity.getName())
                            .toString());
        }

        final File temporaryPreviewFolder = new File(
                new StringBuilder(FilesUtils.TEMP_DIRECTORY.toString()).append("/preview_").append(templateName)
                        .toString());
        if (temporaryPreviewFolder.exists()) {
            try {
                FileUtils.deleteDirectory(temporaryPreviewFolder);
            } catch (IOException e) {
                log.error("Error at the stage of deleting the temporary folder. Exception message :".concat(e.getMessage()));
            }
        }
        try (final OutputStream pdfOutputStream = new ByteArrayOutputStream()) {
            final File zip = templateProjectGenerator.generateZipByTemplateName(templateEntity);
            final ExtendedProjectPreprocessor extendedProjectPreprocessor = new ExtendedProjectPreprocessor(
                    resourceAssetRetriever, templateAssetRetriever);
            extendedProjectPreprocessor.toCanonicalTemplateProject(zip, temporaryPreviewFolder);
            final DataSampleEntity dataSampleByTemplateId = dataSampleRepository
                    .findDataSampleByTemplateId(templateEntity.getId())
                    .orElseThrow(() -> new TemplatePreviewGenerationException("Template missing sample date"));
            final String dataSample = new String(dataSampleByTemplateId.getLatestVersion().getData());
            final File generatedTemplate = new File(new StringBuilder(temporaryPreviewFolder.getAbsolutePath()).append("/templates/").append(templateName).toString());
            final Method convertExplodedTemplate = PdfProducer.class.getDeclaredMethod(
                    "convertExplodedTemplateImpl",
                    InputStream.class,
                    OutputStream.class,
                    String.class,
                    IExplicitTemplateData.class,
                    PdfProducerProperties.class
            );
            convertExplodedTemplate.setAccessible(true);
            convertExplodedTemplate
                    .invoke(null, new FileInputStream(generatedTemplate), pdfOutputStream,
                            temporaryPreviewFolder.toPath().toAbsolutePath().toString(), new JsonData(dataSample),
                            null);
            return pdfOutputStream;
        } catch (IOException | NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
            log.error(ex);
            throw new TemplatePreviewGenerationException("Error while generating PDF preview for template");
        } finally {
            try {
                FileUtils.deleteDirectory(temporaryPreviewFolder);
            } catch (final IOException exception) {
                throw new TemplatePreviewGenerationException(exception.getMessage());
            }
        }
    }

    private TemplateEntity getTemplateByName(final String templateName) {
        return templateRepository.findByName(templateName)
                .orElseThrow(() -> new TemplateNotFoundException(templateName));
    }
}

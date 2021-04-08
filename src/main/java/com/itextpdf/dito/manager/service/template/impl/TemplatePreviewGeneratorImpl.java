package com.itextpdf.dito.manager.service.template.impl;

import com.itextpdf.dito.manager.entity.datasample.DataSampleEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.exception.template.TemplateNotFoundException;
import com.itextpdf.dito.manager.exception.template.TemplatePreviewGenerationException;
import com.itextpdf.dito.manager.repository.template.TemplateRepository;
import com.itextpdf.dito.manager.service.datasample.DataSampleService;
import com.itextpdf.dito.manager.service.template.TemplatePreviewGenerator;
import com.itextpdf.dito.manager.service.template.TemplateProjectGenerator;
import com.itextpdf.dito.sdk.core.data.IExplicitTemplateData;
import com.itextpdf.dito.sdk.core.data.JsonData;
import com.itextpdf.dito.sdk.output.PdfProducer;
import com.itextpdf.dito.sdk.output.PdfProducerProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

import static org.apache.commons.io.FileUtils.deleteDirectory;

@Service
public class TemplatePreviewGeneratorImpl implements TemplatePreviewGenerator {

    private static final Logger log = LogManager.getLogger(TemplatePreviewGeneratorImpl.class);

    private final TemplateRepository templateRepository;
    private final TemplateProjectGenerator templateProjectGenerator;
    private final DataSampleService dataSampleService;

    public TemplatePreviewGeneratorImpl(final TemplateRepository templateRepository,
                                        final TemplateProjectGenerator templateProjectGenerator,
                                        final DataSampleService dataSampleService) {
        this.templateRepository = templateRepository;
        this.templateProjectGenerator = templateProjectGenerator;
        this.dataSampleService = dataSampleService;
    }

    @Override
    public ByteArrayOutputStream generatePreview(final String templateName, final String dataSampleName) {
        log.info("Generate template preview by template name: {} and data sample name: {} was started", templateName, dataSampleName);
        final TemplateEntity templateEntity = getTemplateByName(templateName);

        //get data sample file by template id to transfer it to SDK
        final DataSampleEntity sampleForPreview;
        if (StringUtils.isBlank(dataSampleName)) {
            //specially made for case, when template without data collection
            final Optional<DataSampleEntity> dataSampleByTemplateId = dataSampleService.findDataSampleByTemplateId(templateEntity.getId());
            sampleForPreview = dataSampleByTemplateId.orElse(null);
        } else {
            sampleForPreview = dataSampleService.getByNameAndTemplateName(dataSampleName, templateName);
        }
        final String dataSample = Objects.isNull(sampleForPreview) ? "{}" : new String(sampleForPreview.getLatestVersion().getData(), StandardCharsets.UTF_8);

        final File temporaryPreviewFolder = templateProjectGenerator.generateProjectFolderByTemplate(templateEntity, Objects.isNull(sampleForPreview) ? null : sampleForPreview.getLatestVersion());

        try (final ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream()) {
            try {
                //Temporary folder where SDK will record the result
                final File generatedTemplate = new File(new StringBuilder(temporaryPreviewFolder.getAbsolutePath()).append("/templates/").append(templateName).toString());
                generatePdfPreview(temporaryPreviewFolder, pdfOutputStream, dataSample, generatedTemplate);
                return pdfOutputStream;
            } finally {
                deleteDirectory(temporaryPreviewFolder);
                log.info("Generate template preview by template name: {} and data sample name: {} was finished successfully", templateName, dataSampleName);
            }
        } catch (IOException | NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
            log.error(ex);
            throw new TemplatePreviewGenerationException("Error while generating PDF preview for template");
        }
    }

    private void generatePdfPreview(final File temporaryPreviewFolder, final OutputStream pdfOutputStream,
                                    final String dataSample, final File generatedTemplate)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, FileNotFoundException {
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
                        temporaryPreviewFolder.toPath().toAbsolutePath().toString(), new JsonData(dataSample), null);
    }

    private TemplateEntity getTemplateByName(final String templateName) {
        return templateRepository.findByName(templateName)
                .orElseThrow(() -> new TemplateNotFoundException(templateName));
    }
}
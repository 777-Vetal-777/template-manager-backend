package com.itextpdf.dito.manager.service.template.impl;

import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;
import com.itextpdf.dito.manager.exception.template.TemplatePreviewGenerationException;
import com.itextpdf.dito.manager.service.template.TemplatePreviewGenerator;
import com.itextpdf.dito.manager.service.template.TemplateService;
import com.itextpdf.dito.sdk.core.data.IExplicitTemplateData;
import com.itextpdf.dito.sdk.core.data.JsonData;
import com.itextpdf.dito.sdk.core.preprocess.asset.TemplateAssetRetriever;
import com.itextpdf.dito.sdk.output.PdfProducer;
import com.itextpdf.dito.sdk.output.PdfProducerProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Component
public class TemplatePreviewGeneratorImpl implements TemplatePreviewGenerator {
    private static final Logger log = LogManager.getLogger(TemplatePreviewGenerator.class);

    private final TemplateAssetRetriever templateAssetRetriever;
    private final TemplateService templateService;

    public TemplatePreviewGeneratorImpl(final TemplateAssetRetriever templateAssetRetriever,
                                        final TemplateService templateService) {
        this.templateAssetRetriever = templateAssetRetriever;
        this.templateService = templateService;
    }

    @Override
    public OutputStream generate(final String templateName) {
        final TemplateEntity templateEntity = templateService.get(templateName);
        if (CollectionUtils.isEmpty(templateEntity.getFiles())) {
            throw new IllegalArgumentException(new StringBuilder().append("No file found for template ").append(templateEntity.getName()).toString());
        }

        final TemplateFileEntity fileEntity = templateEntity.getFiles().get(0);

        try (final InputStream templateInputStream = new ByteArrayInputStream(fileEntity.getData());
             final OutputStream pdfOutputStream = new ByteArrayOutputStream()) {
            //TODO implement data samples support. For now it is just hardcoded value
            final String dataSample = new String(this.getClass().getClassLoader().getResourceAsStream("templates/data-sample.json").readAllBytes());
            final Method convertExplodedTemplate = PdfProducer.class.getDeclaredMethod(
                    "convertExplodedTemplateImpl",
                    InputStream.class,
                    OutputStream.class,
                    String.class,
                    IExplicitTemplateData.class,
                    PdfProducerProperties.class
            );
            convertExplodedTemplate.setAccessible(true);
            convertExplodedTemplate.invoke(null, templateInputStream, pdfOutputStream, null, new JsonData(dataSample), null);
            return pdfOutputStream;
        } catch (IOException | NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
            log.error(ex);
            throw new TemplatePreviewGenerationException("Error while generating PDF preview for template");
        }
    }
}

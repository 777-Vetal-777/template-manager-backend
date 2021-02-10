package com.itextpdf.dito.manager.service.template.impl;

import com.itextpdf.dito.manager.entity.datasample.DataSampleEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.exception.template.TemplateNotFoundException;
import com.itextpdf.dito.manager.exception.template.TemplatePreviewGenerationException;
import com.itextpdf.dito.manager.repository.template.TemplateRepository;
import com.itextpdf.dito.manager.service.datasample.DataSampleService;
import com.itextpdf.dito.manager.service.template.TemplatePreviewGenerator;
import com.itextpdf.dito.manager.service.template.TemplateProjectGenerator;
import com.itextpdf.dito.manager.util.FilesUtils;
import com.itextpdf.dito.sdk.core.data.IExplicitTemplateData;
import com.itextpdf.dito.sdk.core.data.JsonData;
import com.itextpdf.dito.sdk.core.preprocess.ExtendedProjectPreprocessor;
import com.itextpdf.dito.sdk.core.preprocess.asset.TemplateAssetRetriever;
import com.itextpdf.dito.sdk.output.PdfProducer;
import com.itextpdf.dito.sdk.output.PdfProducerProperties;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.Objects;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import static org.apache.commons.io.FileUtils.deleteDirectory;
import static org.apache.commons.io.FileUtils.deleteQuietly;

@Service
public class TemplatePreviewGeneratorImpl implements TemplatePreviewGenerator {

    private static final Logger log = LogManager.getLogger(TemplatePreviewGeneratorImpl.class);

    private final TemplateRepository templateRepository;
    private final TemplateProjectGenerator templateProjectGenerator;
    private final TemplateAssetRetriever resourceAssetRetriever;
    private final TemplateAssetRetriever templateAssetRetriever;
    private final DataSampleService dataSampleService;

    public TemplatePreviewGeneratorImpl(final TemplateRepository templateRepository,
            final TemplateProjectGenerator templateProjectGenerator,
            final TemplateAssetRetriever resourceAssetRetriever,
            final TemplateAssetRetriever templateAssetRetriever,
            final DataSampleService dataSampleService) {
        this.templateRepository = templateRepository;
        this.templateProjectGenerator = templateProjectGenerator;
        this.resourceAssetRetriever = resourceAssetRetriever;
        this.templateAssetRetriever = templateAssetRetriever;
        this.dataSampleService = dataSampleService;
    }

    @Override
    public OutputStream generatePreview(final String templateName, final String dataSampleName) {
        final TemplateEntity templateEntity = getTemplateByName(templateName);
        File zippedProject = null;

        final File temporaryPreviewFolder;
        try {
            temporaryPreviewFolder = Files.createTempDirectory(FilesUtils.TEMP_DIRECTORY.toPath(),"preview_".concat(templateName)).toFile();
        } catch (IOException e) {
            throw new TemplatePreviewGenerationException(e.getMessage());
        }

        try (final OutputStream pdfOutputStream = new ByteArrayOutputStream()) {
            try {
                //get data sample file by template id to transfer it to SDK
                final DataSampleEntity sampleForPreview;
                if(Objects.isNull(dataSampleName)){
                    //specially made for case, when template without data collection
                    final Optional<DataSampleEntity> dataSampleByTemplateId = dataSampleService.findDataSampleByTemplateId(templateEntity.getId());
                    sampleForPreview = dataSampleByTemplateId.orElse(null);
                }else {
                    sampleForPreview = dataSampleService.get(dataSampleName);
                }
                final String dataSample = Objects.isNull(sampleForPreview) ? "{}" : new String(sampleForPreview.getLatestVersion().getData());
                zippedProject = templateProjectGenerator.generateZipByTemplateName(templateEntity, Objects.isNull(sampleForPreview) ? null : sampleForPreview.getLatestVersion());

                final ExtendedProjectPreprocessor extendedProjectPreprocessor = new ExtendedProjectPreprocessor(resourceAssetRetriever, templateAssetRetriever);
                extendedProjectPreprocessor.toCanonicalTemplateProject(zippedProject, temporaryPreviewFolder);
                //Temporary folder where SDK will record the result
                final File generatedTemplate = new File(new StringBuilder(temporaryPreviewFolder.getAbsolutePath()).append("/templates/").append(templateName).toString());
                generatePdfPreview(temporaryPreviewFolder, pdfOutputStream, dataSample, generatedTemplate);
                return pdfOutputStream;
            } finally {
                deleteQuietly(zippedProject);
                deleteDirectory(temporaryPreviewFolder);
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
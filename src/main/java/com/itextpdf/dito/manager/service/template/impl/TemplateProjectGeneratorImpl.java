package com.itextpdf.dito.manager.service.template.impl;

import com.itextpdf.dito.manager.entity.datasample.DataSampleEntity;
import com.itextpdf.dito.manager.entity.datasample.DataSampleFileEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.exception.template.TemplateProjectGenerationException;
import com.itextpdf.dito.manager.repository.datasample.DataSampleRepository;
import com.itextpdf.dito.manager.service.template.TemplateProjectGenerator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import static com.itextpdf.dito.manager.util.FilesUtils.DATA_FOLDER;
import static com.itextpdf.dito.manager.util.FilesUtils.TEMPLATES_FOLDER;
import static com.itextpdf.dito.manager.util.FilesUtils.createTemplateDirectoryForPreview;
import static com.itextpdf.dito.manager.util.FilesUtils.zipFolder;
import static java.nio.file.StandardOpenOption.CREATE;

@Component
public class TemplateProjectGeneratorImpl implements TemplateProjectGenerator {

    private static final Logger log = LogManager.getLogger(TemplateProjectGeneratorImpl.class);

    private final DataSampleRepository dataSampleRepository;

    public TemplateProjectGeneratorImpl(final DataSampleRepository dataSampleRepository) {
        this.dataSampleRepository = dataSampleRepository;
    }

    /**
     * Generate template project with template file and data sample.
     *
     * @param templateEntity - to be generated in project.
     * @return .zip template project file
     */
    @Override
    public File generateZipByTemplateName(final TemplateEntity templateEntity,
            final DataSampleFileEntity sampleFileEntity) {
        try {
            final String templateName = templateEntity.getName();
            final Map<String, Path> directories = createTemplateDirectoryForPreview(templateName);
            createFile(TEMPLATES_FOLDER, templateName, templateEntity.getLatestFile().getData(), directories);
            //If data sample exist -> write to root of tmp folder
            final DataSampleFileEntity fileEntity = Optional.ofNullable(sampleFileEntity).orElseGet(() ->
                    dataSampleRepository.findDataSampleByTemplateId(templateEntity.getId()).map(
                            DataSampleEntity::getLatestVersion).orElse(null));
            if (Objects.nonNull(fileEntity)) {
                createFile(DATA_FOLDER, fileEntity.getFileName(), fileEntity.getData(), directories);
            }
            //write folder to zip
            final Path folders = directories.get(templateName);
            final File createdZip = zipFolder(folders,
                    Path.of(new StringBuilder(folders.getParent().toString()).append("/").append(templateName)
                            .append(".zip").toString()));
            FileUtils.deleteDirectory(directories.get(templateName).toFile());
            return createdZip;
        } catch (IOException exception) {
            log.error(exception);
            throw new TemplateProjectGenerationException(exception.getMessage());
        }
    }

    @Override
    public void createFile(final String templateName, final String fileName, final byte[] file,
            final Map<String, Path> folders) {
        try {
            final Path newFile = Path
                    .of(new StringBuilder(folders.get(templateName).toAbsolutePath().toString()).append("/")
                            .append(fileName).toString());
            Files.write(newFile, file, CREATE);
        } catch (IOException exception) {
            log.error(exception);
            throw new TemplateProjectGenerationException(exception.getMessage());
        }
    }
}

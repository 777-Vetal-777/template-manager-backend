package com.itextpdf.dito.manager.service.template.impl;

import com.itextpdf.dito.manager.entity.datasample.DataSampleEntity;
import com.itextpdf.dito.manager.entity.datasample.DataSampleFileEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.exception.template.TemplateProjectGenerationException;
import com.itextpdf.dito.manager.repository.datasample.DataSampleRepository;
import com.itextpdf.dito.manager.service.template.TemplateProjectGenerator;
import com.itextpdf.dito.manager.util.FilesUtils;
import com.itextpdf.dito.sdk.core.preprocess.ExtendedProjectPreprocessor;
import com.itextpdf.dito.sdk.core.preprocess.asset.TemplateAssetRetriever;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.itextpdf.dito.manager.util.FilesUtils.DATA_FOLDER;
import static com.itextpdf.dito.manager.util.FilesUtils.TEMPLATES_FOLDER;
import static com.itextpdf.dito.manager.util.FilesUtils.createTemplateDirectoryForPreview;
import static com.itextpdf.dito.manager.util.FilesUtils.zipFolder;
import static java.nio.file.StandardOpenOption.CREATE;
import static org.apache.commons.io.FileUtils.deleteQuietly;

@Component
public class TemplateProjectGeneratorImpl implements TemplateProjectGenerator {

    private static final Logger log = LogManager.getLogger(TemplateProjectGeneratorImpl.class);

    private final DataSampleRepository dataSampleRepository;
    private final ExtendedProjectPreprocessor extendedProjectPreprocessor;

    public TemplateProjectGeneratorImpl(final DataSampleRepository dataSampleRepository,
                                        final TemplateAssetRetriever resourceAssetRetriever,
                                        final TemplateAssetRetriever templateAssetRetriever) {
        this.dataSampleRepository = dataSampleRepository;
        this.extendedProjectPreprocessor = new ExtendedProjectPreprocessor(resourceAssetRetriever, templateAssetRetriever);
    }

    /**
     * Generate template project with template file and data sample.
     *
     * @param templateEntity - to be generated in project.
     * @param sampleFileEntity - using Data Sample file
     * @return .zip template project file
     */
    private File generateZipByTemplateName(final TemplateEntity templateEntity,
                                          final DataSampleFileEntity sampleFileEntity) {
        return generateZipByTemplate(templateEntity, toDataSampleList(templateEntity, sampleFileEntity));
    }

    /**
     * Generate template project with template file and data sample.
     *
     * @param templateEntity         - to be generated in project.
     * @param dataSampleFileEntities - list of used Data Sample files
     * @return .zip template project file
     */
    private File generateZipByTemplate(TemplateEntity templateEntity, List<DataSampleFileEntity> dataSampleFileEntities) {
        try {
            final String templateName = templateEntity.getName();
            final Map<String, Path> directories = createTemplateDirectoryForPreview(templateName);
            createFile(TEMPLATES_FOLDER, templateName, templateEntity.getLatestFile().getData(), directories);
            //write all data samples to root of tmp folder
            if (Objects.nonNull(dataSampleFileEntities)) {
                dataSampleFileEntities.forEach(fileEntity -> createFile(DATA_FOLDER, fileEntity.getFileName(), fileEntity.getData(), directories));
            }
            //write folder to zip
            final Path folders = directories.get(templateName);
            final File createdZip = zipFolder(folders, Path.of(folders.getParent().toString(), templateName.concat(".zip")));
            FileUtils.deleteDirectory(directories.get(templateName).toFile());
            return createdZip;
        } catch (IOException exception) {
            log.error(exception);
            throw new TemplateProjectGenerationException(exception.getMessage());
        }
    }

    private void createFile(final String templateName, final String fileName, final byte[] file,
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

    @Override
    public File generateProjectFolderByTemplate(TemplateEntity templateEntity, DataSampleFileEntity dataSampleFileEntity) {
        return generateProjectFolderByTemplate(templateEntity, toDataSampleList(templateEntity, dataSampleFileEntity));
    }

    @Override
    public File generateZippedProjectByTemplate(TemplateEntity templateEntity, DataSampleFileEntity dataSampleFileEntity) {
        return generateZippedProjectByTemplate(templateEntity, toDataSampleList(templateEntity, dataSampleFileEntity));
    }

    @Override
    public File generateZippedProjectByTemplate(TemplateEntity templateEntity, List<DataSampleFileEntity> dataSampleFileEntities) {
        final Path projectFolder = generateProjectFolderByTemplate(templateEntity, dataSampleFileEntities).toPath();
        final File zippedProject;

        try {
            zippedProject = zipFolder(projectFolder, Path.of(projectFolder.getParent().toString(), templateEntity.getName().concat(".zip")));
        } catch (IOException e) {
            log.error(e);
            throw new TemplateProjectGenerationException("Error while generating zipped project");
        }

        return zippedProject;
    }

    private File generateProjectFolderByTemplate(TemplateEntity templateEntity, List<DataSampleFileEntity> dataSampleFileEntities) {
        final File projectFolder;
        try {
            projectFolder = Files.createTempDirectory(FilesUtils.TEMP_DIRECTORY.toPath(),"preview_".concat(templateEntity.getName())).toFile();
        } catch (IOException e) {
            throw new TemplateProjectGenerationException(e.getMessage());
        }

        try {
            final File zippedProject = generateZipByTemplate(templateEntity, dataSampleFileEntities);

            try {
                extendedProjectPreprocessor.toCanonicalTemplateProject(zippedProject, projectFolder);
            } finally {
                deleteQuietly(zippedProject);
            }
        } catch (IOException ex) {
            log.error(ex);
            throw new TemplateProjectGenerationException("Error while generating PDF preview for template");
        }
        return projectFolder;
    }

    private List<DataSampleFileEntity> toDataSampleList(final TemplateEntity templateEntity,
                                                        final DataSampleFileEntity sampleFileEntity) {
        return  Collections.singletonList(Optional.ofNullable(sampleFileEntity).orElseGet(() ->
                dataSampleRepository.findDataSampleByTemplateId(templateEntity.getId()).map(
                        DataSampleEntity::getLatestVersion).orElse(null)));
    }

}

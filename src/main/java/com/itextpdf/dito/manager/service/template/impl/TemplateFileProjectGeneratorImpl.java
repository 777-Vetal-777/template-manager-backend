package com.itextpdf.dito.manager.service.template.impl;

import com.itextpdf.dito.manager.component.retriever.RetrieverBuilder;
import com.itextpdf.dito.manager.entity.datasample.DataSampleEntity;
import com.itextpdf.dito.manager.entity.datasample.DataSampleFileEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;
import com.itextpdf.dito.manager.exception.template.TemplateProjectGenerationException;
import com.itextpdf.dito.manager.repository.datasample.DataSampleRepository;
import com.itextpdf.dito.manager.service.template.TemplateFileProjectGenerator;
import com.itextpdf.dito.manager.util.FilesUtils;
import com.itextpdf.dito.sdk.core.preprocess.ExtendedProjectPreprocessor;
import com.itextpdf.dito.sdk.core.preprocess.asset.TemplateAssetRetriever;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

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
import static com.itextpdf.dito.manager.util.FilesUtils.RESOURCES_FOLDER;
import static com.itextpdf.dito.manager.util.FilesUtils.TEMPLATES_FOLDER;
import static com.itextpdf.dito.manager.util.FilesUtils.createTemplateDirectoryForPreview;
import static com.itextpdf.dito.manager.util.FilesUtils.zipFolder;
import static com.itextpdf.dito.sdk.core.pkg.PackageConstant.SPECIAL_TEMPLATE_PROJECT_FOLDERS;
import static com.itextpdf.dito.sdk.core.pkg.PackageConstant.TEMPLATE_PACKAGE_EXTENSION;
import static java.nio.file.StandardOpenOption.CREATE;
import static org.apache.commons.io.FileUtils.deleteQuietly;

@Service
public class TemplateFileProjectGeneratorImpl implements TemplateFileProjectGenerator {

    private static final Logger log = LogManager.getLogger(TemplateFileProjectGeneratorImpl.class);

    private final DataSampleRepository dataSampleRepository;
    private final RetrieverBuilder retrieverBuilder;

    public TemplateFileProjectGeneratorImpl(final DataSampleRepository dataSampleRepository,
                                            final RetrieverBuilder retrieverBuilder) {
        this.dataSampleRepository = dataSampleRepository;
        this.retrieverBuilder = retrieverBuilder;
    }

    @Override
    public File generateProjectFolderByTemplate(final TemplateFileEntity templateFileEntity, final DataSampleFileEntity dataSampleFileEntity) {
        return generateProjectFolderByTemplate(templateFileEntity, toDataSampleList(templateFileEntity.getTemplate(), dataSampleFileEntity), true);
    }

    @Override
    public File generateZippedProjectByTemplate(final TemplateFileEntity templateFileEntity, final DataSampleFileEntity dataSampleFileEntity, final boolean exportDependencies) {
        return generateZippedProjectByTemplate(templateFileEntity, toDataSampleList(templateFileEntity.getTemplate(), dataSampleFileEntity), exportDependencies);
    }

    @Override
    public File generateZippedProjectByTemplate(final TemplateFileEntity templateFileEntity, final List<DataSampleFileEntity> dataSampleFileEntities, final boolean exportDependencies) {
        final Path projectFolder = generateProjectFolderByTemplate(templateFileEntity, dataSampleFileEntities, exportDependencies).toPath();
        final File zippedProject;

        try {
            zippedProject = zipFolder(projectFolder, Path.of(projectFolder.getParent().toString(), templateFileEntity.getTemplate().getName().concat(TEMPLATE_PACKAGE_EXTENSION)));
        } catch (IOException e) {
            log.error(e);
            throw new TemplateProjectGenerationException("Error while generating zipped project");
        } finally {
            deleteQuietly(projectFolder.toFile());
        }

        return zippedProject;
    }

    private List<DataSampleFileEntity> toDataSampleList(final TemplateEntity templateEntity,
                                                        final DataSampleFileEntity sampleFileEntity) {
        return Collections.singletonList(Optional.ofNullable(sampleFileEntity).orElseGet(() ->
                dataSampleRepository.findDataSampleByTemplateId(templateEntity.getId()).map(
                        DataSampleEntity::getLatestVersion).orElse(null)));
    }

    private File generateProjectFolderByTemplate(final TemplateFileEntity templateFileEntity, final List<DataSampleFileEntity> dataSampleFileEntities, final boolean exportDependencies) {
        final File projectFolder;
        try {
            projectFolder = Files.createTempDirectory(FilesUtils.TEMP_DIRECTORY.toPath(), "preview_".concat(templateFileEntity.getTemplate().getUuid())).toFile();
        } catch (IOException e) {
            throw new TemplateProjectGenerationException(e.getMessage());
        }

        try {
            final File zippedProject = generateZipByTemplate(templateFileEntity, dataSampleFileEntities);

            try {
                final TemplateAssetRetriever resourceAssetRetriever = retrieverBuilder.buildResourceAssetRetriever(templateFileEntity);
                final TemplateAssetRetriever templateAssetRetriever = retrieverBuilder.buildTemplateAssetRetriever(templateFileEntity);
                final ExtendedProjectPreprocessor extendedProjectPreprocessor = new ExtendedProjectPreprocessor(resourceAssetRetriever, templateAssetRetriever);
                extendedProjectPreprocessor.toCanonicalTemplateProject(zippedProject, projectFolder);
            } finally {
                deleteQuietly(zippedProject);
            }

            if (!exportDependencies) {
                deleteQuietly(Path.of(projectFolder.getAbsolutePath(), DATA_FOLDER).toFile());
                deleteQuietly(Path.of(projectFolder.getAbsolutePath(), RESOURCES_FOLDER).toFile());
            }

            SPECIAL_TEMPLATE_PROJECT_FOLDERS.forEach(folder -> new File(projectFolder, folder).mkdir());

        } catch (IOException ex) {
            log.error(ex);
            throw new TemplateProjectGenerationException("Error while generating PDF preview for template");
        }
        return projectFolder;
    }

    /**
     * Generate template project with template file and data sample.
     *
     * @param templateFileEntity         - to be generated in project.
     * @param dataSampleFileEntities - list of used Data Sample files
     * @return .zip template project file
     */
    private File generateZipByTemplate(final TemplateFileEntity templateFileEntity, final List<DataSampleFileEntity> dataSampleFileEntities) {
        try {
            final String templateName = templateFileEntity.getTemplate().getName().replace(' ', '_');
            final Map<String, Path> directories = createTemplateDirectoryForPreview(templateName);
            createFile(TEMPLATES_FOLDER, templateName, templateFileEntity.getData(), directories);
            //write all data samples to root of tmp folder
            if (Objects.nonNull(dataSampleFileEntities)) {
                dataSampleFileEntities.forEach(fileEntity -> {
                    if (fileEntity != null) {
                        createFile(DATA_FOLDER, fileEntity.getFileName(), fileEntity.getData(), directories);
                    }
                });
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

}

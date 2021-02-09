package com.itextpdf.dito.manager.service.template.impl;

import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.entity.datasample.DataSampleFileEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceFileEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.exception.template.TemplateProjectGenerationException;
import com.itextpdf.dito.manager.repository.datasample.DataSampleRepository;
import com.itextpdf.dito.manager.repository.resource.ResourceRepository;
import com.itextpdf.dito.manager.service.template.TemplateProjectGenerator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import static com.itextpdf.dito.manager.util.FilesUtils.DATA_FOLDER;
import static com.itextpdf.dito.manager.util.FilesUtils.RESOURCES_FONTS_FOLDER;
import static com.itextpdf.dito.manager.util.FilesUtils.RESOURCES_IMAGES_FOLDER;
import static com.itextpdf.dito.manager.util.FilesUtils.RESOURCES_STYLESHEETS_FOLDER;
import static com.itextpdf.dito.manager.util.FilesUtils.TEMPLATES_FOLDER;
import static com.itextpdf.dito.manager.util.FilesUtils.TEMP_DIRECTORY;
import static com.itextpdf.dito.manager.util.FilesUtils.createTemplateDirectoryForPreview;
import static com.itextpdf.dito.manager.util.FilesUtils.zipFolder;
import static java.nio.file.StandardOpenOption.CREATE;

@Component
public class TemplateProjectGeneratorImpl implements TemplateProjectGenerator {

    private static final Logger log = LogManager.getLogger(TemplateProjectGeneratorImpl.class);

    private final DataSampleRepository dataSampleRepository;
    private final ResourceRepository resourceRepository;

    public TemplateProjectGeneratorImpl(final DataSampleRepository dataSampleRepository,
            final ResourceRepository resourceRepository) {
        this.dataSampleRepository = dataSampleRepository;
        this.resourceRepository = resourceRepository;
    }

    /**
     * Generate template project with template file and data sample.
     *
     * @param templateEntity - to be generated in project.
     * @return .zip template project file
     */
    @Override
    public File generateZipByTemplateName(final TemplateEntity templateEntity, final DataSampleFileEntity sampleFileEntity) {
        try {
            final String templateName = templateEntity.getName();
            final Map<String, Path> directories = createTemplateDirectoryForPreview(templateName);
            //If data sample exist -> write to root of tmp folder
            if(Objects.nonNull(sampleFileEntity)){
                createFile(DATA_FOLDER, sampleFileEntity.getFileName(), sampleFileEntity.getData(), directories);
            }else {
                dataSampleRepository.findDataSampleByTemplateId(templateEntity.getId()).ifPresent(sample -> {
                    final DataSampleFileEntity file = sample.getLatestVersion();
                    createFile(DATA_FOLDER, file.getFileName(), file.getData(), directories);
                });
            }
            createFile(TEMPLATES_FOLDER, templateName, templateEntity.getLatestFile().getData(), directories);
            //can drop file already exist exception, need to check/rewrite query
            final List<ResourceEntity> resource = resourceRepository
                    .findAllResourceByTemplateId(templateEntity.getId());
            resource.forEach(res -> {
                if (res.getType() == ResourceTypeEnum.IMAGE) {
                    final ResourceFileEntity file = res.getLatestFile().get(0);
                    createFile(RESOURCES_IMAGES_FOLDER, file.getFileName(), file.getFile(), directories);
                }
                if (res.getType() == ResourceTypeEnum.FONT) {
                    final List<ResourceFileEntity> files = res.getLatestFile();
                    files.forEach(file -> createFile(RESOURCES_FONTS_FOLDER, file.getFileName(), file.getFile(),
                            directories));
                }
                if (res.getType() == ResourceTypeEnum.STYLESHEET) {
                    final ResourceFileEntity file = res.getLatestFile().get(0);
                    createFile(RESOURCES_STYLESHEETS_FOLDER, file.getFileName(), file.getFile(), directories);
                }
            });
            //write folder to zip
            final Path folders = directories.get(templateName);
            final File createdZip = zipFolder(folders, Path.of(new StringBuilder(folders.getParent().toString()).append("/").append(templateName).append(".zip").toString()));
            FileUtils.deleteDirectory(directories.get(templateName).toFile());
            return createdZip;
        } catch (IOException exception) {
            log.error(exception);
            throw new TemplateProjectGenerationException(exception.getMessage());
        } finally {
            try {
                FileUtils.deleteDirectory(new File(TEMP_DIRECTORY + "/" + templateEntity.getName()));
            } catch (IOException exception) {
                throw new TemplateProjectGenerationException(exception.getMessage());
            }
        }
    }

    @Override
    public void createFile(final String templateName, final String fileName, final byte[] file,
            final Map<String, Path> folders) {
        try {
            final Path newFile = Path
                    .of(new StringBuilder(folders.get(templateName).toAbsolutePath().toString()).append("/").append(fileName).toString());
            Files.write(newFile, file, CREATE);
        } catch (IOException exception) {
            log.error(exception);
            throw new TemplateProjectGenerationException(exception.getMessage());
        }
    }
}

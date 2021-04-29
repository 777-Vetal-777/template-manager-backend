package com.itextpdf.dito.manager.component.template.dtm.read.impl;

import com.itextpdf.dito.manager.component.template.dtm.read.DtmFileItemReader;
import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.dto.template.setting.SettingType;
import com.itextpdf.dito.manager.dto.template.setting.TemplateImportNameModel;
import com.itextpdf.dito.manager.entity.resource.FontTypeEnum;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.exception.resource.ResourceAlreadyExistsException;
import com.itextpdf.dito.manager.exception.resource.ResourceNotFoundException;
import com.itextpdf.dito.manager.exception.template.TemplateImportProjectException;
import com.itextpdf.dito.manager.model.template.dtm.DtmFileDescriptorModel;
import com.itextpdf.dito.manager.model.template.dtm.ItemType;
import com.itextpdf.dito.manager.model.template.dtm.context.DtmFileImportContext;
import com.itextpdf.dito.manager.model.template.dtm.resource.DtmFontFaceDescriptorModel;
import com.itextpdf.dito.manager.model.template.dtm.resource.DtmResourceDescriptorModel;
import com.itextpdf.dito.manager.model.template.dtm.resource.DtmResourceVersionDescriptorModel;
import com.itextpdf.dito.manager.repository.resource.ResourceRepository;
import com.itextpdf.dito.manager.service.resource.ResourceService;
import org.apache.http.entity.ContentType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class DtmResourceReaderImpl implements DtmFileItemReader {
    private final ResourceRepository resourceRepository;
    private final ResourceService resourceService;

    public DtmResourceReaderImpl(final ResourceRepository resourceRepository,
                                 final ResourceService resourceService) {
        this.resourceRepository = resourceRepository;
        this.resourceService = resourceService;
    }

    @Override
    public void read(final DtmFileImportContext context,
                     final DtmFileDescriptorModel model,
                     final Path basePath) {
        model.getResources().forEach(resourceModel -> {
                    ResourceEntity resourceEntity;
                    final SettingType settingType = SettingType.valueOf(resourceModel.getType().toString());
                    try {
                        resourceEntity = resourceService.get(resourceModel.getName(), resourceModel.getType());

                        final TemplateImportNameModel nameModel = Optional.ofNullable(context.getSettings(settingType))
                                .map(setting -> setting.get(resourceModel.getName()))
                                .orElseThrow(() -> new ResourceAlreadyExistsException(resourceModel.getName()));

                        if (Boolean.TRUE.equals(nameModel.getAllowedNewVersion())) {
                            resourceEntity = makeImportVersions(context, basePath, resourceEntity.getName(), resourceModel, resourceEntity);
                        } else {
                            int currentNumber = resourceRepository.findMaxIntegerByNamePattern(context.getFileName()).orElse(0) + 1;
                            resourceEntity = makeImportVersions(context, basePath, new StringBuilder(context.getFileName()).append("(").append(currentNumber).append(")").toString(), resourceModel, null);
                        }

                    } catch (ResourceNotFoundException e) {
                        resourceEntity = makeImportVersions(context, basePath, resourceModel.getName(), resourceModel, null);
                    } catch (ResourceAlreadyExistsException e) {
                        context.putToDuplicates(settingType, resourceModel.getName());
                    }
                }
        );
    }

    private ResourceEntity makeImportVersions(final DtmFileImportContext context,
                                              final Path basePath,
                                              final String resourceName,
                                              final DtmResourceDescriptorModel resourceModel,
                                              final ResourceEntity initialEntity) {
        ResourceEntity resourceEntity = initialEntity;
        final List<DtmResourceVersionDescriptorModel> descriptorModels = Optional.of(resourceModel.getVersions())
                .stream().flatMap(List::stream)
                .sorted(Comparator.comparingLong(DtmResourceVersionDescriptorModel::getVersion))
                .collect(Collectors.toList());
        for (final DtmResourceVersionDescriptorModel version : descriptorModels) {
            try {
                if (resourceEntity == null) {
                    if (ResourceTypeEnum.FONT.equals(resourceModel.getType())) {
                        final Map<FontTypeEnum, MultipartFile> multipart = new EnumMap<>(FontTypeEnum.class);
                        for (String fontFace : version.getFontFaces().keySet()) {
                            final DtmFontFaceDescriptorModel dtmFontFaceDescriptorModel = version.getFontFaces().get(fontFace);
                            final FontTypeEnum key = FontTypeEnum.valueOf(fontFace);
                            final MultipartFile value = new ByteArrayMultipartFile(Files.readAllBytes(basePath.resolve(dtmFontFaceDescriptorModel.getLocalPath())), dtmFontFaceDescriptorModel.getFileName());
                            multipart.put(key, value);
                        }
                        resourceEntity = resourceService.createNewFont(context.getEmail(), resourceName, resourceModel.getType(), multipart);
                    } else {
                        resourceEntity = resourceService.create(resourceName, resourceModel.getType(), Files.readAllBytes(basePath.resolve(version.getLocalPath())), version.getFileName(), context.getEmail());
                    }
                } else {
                    resourceEntity = ResourceTypeEnum.FONT.equals(resourceModel.getType()) ? resourceEntity : resourceService.createNewVersion(resourceEntity.getName(), resourceEntity.getType(), Files.readAllBytes(basePath.resolve(version.getLocalPath())), version.getFileName(), context.getEmail(), version.getComment());
                }
                context.map(resourceModel.getId(), resourceEntity);
                context.map(resourceModel.getId(), version.getVersion(), resourceEntity.getLatestFile().get(0));
            } catch (IOException ioException) {
                throw new TemplateImportProjectException("Importing archive is broken or corrupted, could not load file " + version.getLocalPath() + " for resource", ioException);
            }
        }
        return resourceEntity;
    }

    @Override
    public ItemType getType() {
        return ItemType.RESOURCE;
    }

    private static class ByteArrayMultipartFile implements MultipartFile {
        private final byte[] content;
        private final String fileName;

        public ByteArrayMultipartFile(final byte[] content, final String fileName) {
            this.content = content;
            this.fileName = fileName;
        }

        @Override
        public String getName() {
            return fileName;
        }

        @Override
        public String getOriginalFilename() {
            return getName();
        }

        @Override
        public String getContentType() {
            return ContentType.APPLICATION_OCTET_STREAM.toString();
        }

        @Override
        public boolean isEmpty() {
            return content == null || content.length == 0;
        }

        @Override
        public long getSize() {
            return content.length;
        }

        @Override
        public byte[] getBytes() throws IOException {
            return content;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(content);
        }

        @Override
        public void transferTo(File dest) throws IOException {
            try (FileOutputStream outputStream = new FileOutputStream(dest)) {
                outputStream.write(content);
            }
        }
    }
}

package com.itextpdf.dito.manager.component.template.dtm.read.impl;

import com.itextpdf.dito.manager.component.template.dtm.read.DtmFileItemReader;
import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.dto.template.setting.SettingType;
import com.itextpdf.dito.manager.dto.template.setting.TemplateImportNameModel;
import com.itextpdf.dito.manager.entity.TemplateTypeEnum;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionFileEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceFileEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;
import com.itextpdf.dito.manager.exception.template.TemplateAlreadyExistsException;
import com.itextpdf.dito.manager.exception.template.TemplateImportProjectException;
import com.itextpdf.dito.manager.exception.template.TemplateNotFoundException;
import com.itextpdf.dito.manager.integration.editor.mapper.resource.ResourceIdMapper;
import com.itextpdf.dito.manager.model.template.dtm.DtmFileDescriptorModel;
import com.itextpdf.dito.manager.model.template.dtm.DtmUsedInDescriptorModel;
import com.itextpdf.dito.manager.model.template.dtm.ItemType;
import com.itextpdf.dito.manager.model.template.dtm.context.DtmFileImportContext;
import com.itextpdf.dito.manager.model.template.dtm.datacollection.DtmDataCollectionDescriptorModel;
import com.itextpdf.dito.manager.model.template.dtm.datacollection.DtmDataCollectionUsedInDescriptorModel;
import com.itextpdf.dito.manager.model.template.dtm.datacollection.DtmDataCollectionVersionDescriptorModel;
import com.itextpdf.dito.manager.model.template.dtm.resource.DtmFontFaceDescriptorModel;
import com.itextpdf.dito.manager.model.template.dtm.resource.DtmResourceDescriptorModel;
import com.itextpdf.dito.manager.model.template.dtm.resource.DtmResourceVersionDescriptorModel;
import com.itextpdf.dito.manager.model.template.dtm.template.DtmTemplateDescriptorModel;
import com.itextpdf.dito.manager.model.template.dtm.template.DtmTemplateVersionDescriptorModel;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionFileRepository;
import com.itextpdf.dito.manager.repository.resource.ResourceFileRepository;
import com.itextpdf.dito.manager.repository.template.TemplateFileRepository;
import com.itextpdf.dito.manager.repository.template.TemplateRepository;
import com.itextpdf.dito.manager.service.template.TemplateDeploymentService;
import com.itextpdf.dito.manager.service.template.TemplateService;
import com.itextpdf.dito.manager.service.user.UserService;
import com.itextpdf.dito.manager.util.TemplateUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class DtmTemplateStandardReaderImpl implements DtmFileItemReader {

    private final TemplateService templateService;
    private final TemplateFileRepository templateFileRepository;
    private final TemplateRepository templateRepository;
    private final ResourceFileRepository resourceFileRepository;
    private final DataCollectionFileRepository dataCollectionFileRepository;
    private final UserService userService;
    private final ResourceIdMapper resourceIdMapper;
    private final TemplateDeploymentService templateDeploymentService;

    public DtmTemplateStandardReaderImpl(final TemplateService templateService,
                                         final TemplateFileRepository templateFileRepository,
                                         final TemplateRepository templateRepository,
                                         final ResourceFileRepository resourceFileRepository,
                                         final DataCollectionFileRepository dataCollectionFileRepository,
                                         final UserService userService,
                                         final ResourceIdMapper resourceIdMapper,
                                         final TemplateDeploymentService templateDeploymentService) {
        this.templateService = templateService;
        this.templateFileRepository = templateFileRepository;
        this.templateRepository = templateRepository;
        this.resourceFileRepository = resourceFileRepository;
        this.dataCollectionFileRepository = dataCollectionFileRepository;
        this.userService = userService;
        this.resourceIdMapper = resourceIdMapper;
        this.templateDeploymentService = templateDeploymentService;
    }

    @Override
    public void read(final DtmFileImportContext context,
                     final DtmFileDescriptorModel model,
                     final Path basePath) {
        model.getTemplates().stream()
                .filter(template -> !Objects.equals(template.getType(), TemplateTypeEnum.COMPOSITION))
                .forEach(templateModel -> {
                            TemplateEntity templateEntity;
                            try {
                                templateEntity = templateService.get(templateModel.getName());

                                final TemplateImportNameModel nameModel = Optional.ofNullable(context.getSettings(SettingType.TEMPLATE))
                                        .map(setting -> setting.get(templateModel.getName()))
                                        .orElseThrow(() -> new TemplateAlreadyExistsException(templateModel.getName()));

                                if (Boolean.TRUE.equals(nameModel.getAllowedNewVersion())) {
                                    templateEntity = makeImportVersions(context, basePath, templateEntity.getName(), templateModel, model, templateEntity);
                                } else {
                                    int currentNumber = templateRepository.findMaxIntegerByNamePattern(context.getFileName()).orElse(0) + 1;
                                    templateEntity = makeImportVersions(context, basePath, new StringBuilder(context.getFileName()).append("(").append(currentNumber).append(")").toString(), templateModel, model, null);
                                }

                            } catch (TemplateNotFoundException e) {
                                templateEntity = makeImportVersions(context, basePath, templateModel.getName(), templateModel, model, null);
                            } catch (TemplateAlreadyExistsException e) {
                                context.putToDuplicates(SettingType.TEMPLATE, templateModel.getName());
                            }
                        }
                );
    }

    private TemplateEntity makeImportVersions(final DtmFileImportContext context,
                                              final Path basePath,
                                              final String templateName,
                                              final DtmTemplateDescriptorModel templateModel,
                                              final DtmFileDescriptorModel model,
                                              final TemplateEntity initialEntity) {
        TemplateEntity templateEntity = initialEntity;
        final List<DtmTemplateVersionDescriptorModel> descriptorModels = Optional.of(templateModel.getVersions())
                .stream().flatMap(List::stream)
                .sorted(Comparator.comparingLong(DtmTemplateVersionDescriptorModel::getVersion))
                .collect(Collectors.toList());
        for (final DtmTemplateVersionDescriptorModel version : descriptorModels) {
            try {
                final DataCollectionFileEntity dataCollectionFileEntity = getDataCollectionFileEntity(model, context, templateModel.getId(), version.getVersion());
                if (templateEntity == null) {
                    final String dataCollectionName = Optional.ofNullable(dataCollectionFileEntity).map(DataCollectionFileEntity::getDataCollection).map(DataCollectionEntity::getName).orElse(null);
                    templateEntity = templateService.create(templateName, templateModel.getType(), dataCollectionName, context.getEmail(), Collections.emptyList());
                    context.map(templateModel.getId(), templateEntity);
                } else {
                    final UserEntity userEntity = userService.findByEmail(context.getEmail());
                    templateEntity = templateService.createNewVersionAsCopy(templateEntity.getLatestFile(), userEntity, version.getComment());
                }

                final TemplateFileEntity templateFileEntity = templateEntity.getLatestFile();
                templateFileEntity.setDataCollectionFile(dataCollectionFileEntity);
                final TemplateFileEntity savedEntity = updateResourceDependencies(templateFileEntity,
                        model,
                        context,
                        Files.readAllBytes(basePath.resolve(version.getLocalPath())),
                        templateModel.getId(),
                        version.getVersion());

                context.map(templateModel.getId(), version.getVersion(), savedEntity);
            } catch (IOException ioException) {
                throw new TemplateImportProjectException("Importing archive is broken or corrupted, could not load file " + version.getLocalPath() + " for template", ioException);
            }
        }
        return templateEntity;
    }

    private DataCollectionFileEntity getDataCollectionFileEntity(final DtmFileDescriptorModel model,
                                                                 final DtmFileImportContext context,
                                                                 final String templateId,
                                                                 final Long version) {
        final DtmDataCollectionUsedInDescriptorModel search = new DtmDataCollectionUsedInDescriptorModel();
        search.setId(Long.valueOf(templateId));
        search.setType(ItemType.TEMPLATE.getPluralName());
        search.setVersion(version);
        for (DtmDataCollectionDescriptorModel dataCollection : model.getDataCollections()) {
            for (DtmDataCollectionVersionDescriptorModel dataCollectionVersion : dataCollection.getVersions()) {
                if (dataCollectionVersion.getUsedIn().contains(search)) {
                    return dataCollectionFileRepository.findByVersionAndDataCollection_Id(context.getCollectionMapping(dataCollection.getId(), dataCollectionVersion.getVersion()),
                            context.getCollectionMapping(dataCollection.getId()))
                            .orElse(null);
                }
            }
        }
        return null;
    }

    private TemplateFileEntity updateResourceDependencies(final TemplateFileEntity templateFileEntity,
                                                          final DtmFileDescriptorModel model,
                                                          final DtmFileImportContext context,
                                                          final byte[] data,
                                                          final String templateId,
                                                          final Long version) {
        String dataString = new String(data, StandardCharsets.UTF_8);
        final DtmUsedInDescriptorModel search = new DtmUsedInDescriptorModel();
        search.setId(Long.valueOf(templateId));
        search.setType(ItemType.TEMPLATE.getPluralName());
        search.setVersion(version);
        final Set<ResourceFileEntity> resourceFiles = templateFileEntity.getResourceFiles();
        resourceFiles.clear();
        for (DtmResourceDescriptorModel resource : model.getResources()) {
            for (DtmResourceVersionDescriptorModel resourceVersion : resource.getVersions()) {
                if (resourceVersion.getUsedIn().contains(search)) {
                    final List<ResourceFileEntity> resourceFileEntities = resourceFileRepository.findByVersionAndResource_Id(
                            context.getResourceMapping(resource.getId(), resourceVersion.getVersion()),
                            context.getResourceMapping(resource.getId()));
                    dataString = updateResourceMapping(dataString, resource, resourceVersion, resourceFileEntities);
                    resourceFiles.add(resourceFileEntities.get(0));

                }
            }
        }
        templateFileEntity.setData(dataString.getBytes(StandardCharsets.UTF_8));
        final TemplateFileEntity fileEntity = templateFileRepository.save(templateFileEntity);
        templateDeploymentService.promoteOnDefaultStage(fileEntity);
        return fileEntity;
    }

    private String updateResourceMapping(final String data,
                                         final DtmResourceDescriptorModel resource,
                                         final DtmResourceVersionDescriptorModel resourceVersion,
                                         final List<ResourceFileEntity> resourceFileEntities) {
        String dataString = data;
        for (final ResourceFileEntity resourceEntity : resourceFileEntities) {
            dataString = dataString.replace(resource.getAlias(), TemplateUtils.DITO_ASSET_TAG.concat(resourceIdMapper.mapToId(resourceEntity.getResource())));
            if (Objects.equals(resource.getType(), ResourceTypeEnum.FONT)) {
                final Optional<String> fontFaceAlias = Optional.ofNullable(resourceVersion.getFontFaces())
                        .map(fontFaces -> fontFaces.get(resourceEntity.getFontName()))
                        .map(DtmFontFaceDescriptorModel::getAlias);
                if (fontFaceAlias.isPresent()) {
                    dataString = dataString.replace(fontFaceAlias.get(), TemplateUtils.DITO_ASSET_TAG.concat(resourceIdMapper.mapToId(resourceEntity)));
                }
            }
        }
        return dataString;
    }

    @Override
    public ItemType getType() {
        return ItemType.TEMPLATE;
    }

    @Override
    public int getPriority() {
        return 60;
    }
}

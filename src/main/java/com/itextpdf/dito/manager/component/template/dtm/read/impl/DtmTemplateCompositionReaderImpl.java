package com.itextpdf.dito.manager.component.template.dtm.read.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.dito.manager.component.template.dtm.read.DtmFileItemReader;
import com.itextpdf.dito.manager.dto.template.setting.SettingType;
import com.itextpdf.dito.manager.dto.template.setting.TemplateImportNameModel;
import com.itextpdf.dito.manager.entity.TemplateTypeEnum;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionFileEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFilePartEntity;
import com.itextpdf.dito.manager.exception.datacollection.DataCollectionAlreadyExistsException;
import com.itextpdf.dito.manager.exception.datacollection.DataCollectionNotFoundException;
import com.itextpdf.dito.manager.exception.template.TemplateImportProjectException;
import com.itextpdf.dito.manager.exception.template.TemplateNotFoundException;
import com.itextpdf.dito.manager.model.template.dtm.DtmFileDescriptorModel;
import com.itextpdf.dito.manager.model.template.dtm.ItemType;
import com.itextpdf.dito.manager.model.template.dtm.context.DtmFileImportContext;
import com.itextpdf.dito.manager.model.template.dtm.datacollection.DtmDataCollectionDescriptorModel;
import com.itextpdf.dito.manager.model.template.dtm.datacollection.DtmDataCollectionUsedInDescriptorModel;
import com.itextpdf.dito.manager.model.template.dtm.datacollection.DtmDataCollectionVersionDescriptorModel;
import com.itextpdf.dito.manager.model.template.dtm.template.DtmTemplateDescriptorModel;
import com.itextpdf.dito.manager.model.template.dtm.template.DtmTemplateUsedInDescriptorModel;
import com.itextpdf.dito.manager.model.template.dtm.template.DtmTemplateVersionDescriptorModel;
import com.itextpdf.dito.manager.model.template.part.PartSettings;
import com.itextpdf.dito.manager.model.template.part.TemplatePartModel;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionFileRepository;
import com.itextpdf.dito.manager.repository.template.TemplateFileRepository;
import com.itextpdf.dito.manager.repository.template.TemplateRepository;
import com.itextpdf.dito.manager.service.template.TemplateDeploymentService;
import com.itextpdf.dito.manager.service.template.TemplateService;
import com.itextpdf.styledxmlparser.jsoup.Jsoup;
import com.itextpdf.styledxmlparser.jsoup.nodes.Document;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class DtmTemplateCompositionReaderImpl implements DtmFileItemReader {
    private final static Logger log = LogManager.getLogger(DtmTemplateCompositionReaderImpl.class);
    private final TemplateService templateService;
    private final TemplateFileRepository templateFileRepository;
    private final TemplateRepository templateRepository;
    private final DataCollectionFileRepository dataCollectionFileRepository;
    private final TemplateDeploymentService templateDeploymentService;
    private final ObjectMapper objectMapper;

    public DtmTemplateCompositionReaderImpl(final TemplateService templateService,
                                            final TemplateFileRepository templateFileRepository,
                                            final TemplateRepository templateRepository,
                                            final DataCollectionFileRepository dataCollectionFileRepository,
                                            final TemplateDeploymentService templateDeploymentService,
                                            final ObjectMapper objectMapper) {
        this.templateService = templateService;
        this.templateFileRepository = templateFileRepository;
        this.templateRepository = templateRepository;
        this.dataCollectionFileRepository = dataCollectionFileRepository;
        this.templateDeploymentService = templateDeploymentService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void read(final DtmFileImportContext context,
                     final DtmFileDescriptorModel model,
                     final Path basePath) {
        model.getTemplates().stream()
                .filter(template -> Objects.equals(template.getType(), TemplateTypeEnum.COMPOSITION))
                .forEach(templateModel -> {
                            TemplateEntity templateEntity;
                            try {
                                templateEntity = templateService.get(templateModel.getName());

                                final TemplateImportNameModel nameModel = Optional.ofNullable(context.getSettings(SettingType.TEMPLATE))
                                        .map(setting -> setting.get(templateModel.getName()))
                                        .orElseThrow(() -> new DataCollectionAlreadyExistsException(templateModel.getName()));

                                if (Boolean.TRUE.equals(nameModel.getAllowedNewVersion())) {
                                    templateEntity = makeImportVersions(context, basePath, templateEntity.getName(), templateModel, model, templateEntity);
                                } else {
                                    int currentNumber = templateRepository.findMaxIntegerByNamePattern(context.getFileName()).orElse(0) + 1;
                                    templateEntity = makeImportVersions(context, basePath, new StringBuilder(context.getFileName()).append("(").append(currentNumber).append(")").toString(), templateModel, model, null);
                                }

                            } catch (DataCollectionNotFoundException e) {
                                templateEntity = makeImportVersions(context, basePath, templateModel.getName(), templateModel, model, null);
                            } catch (DataCollectionAlreadyExistsException e) {
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
                final List<TemplatePartModel> templateParts = getTemplateParts(model,
                        context,
                        Files.readAllBytes(basePath.resolve(version.getLocalPath())),
                        templateModel.getId(),
                        version.getVersion());

                if (templateEntity == null) {
                    final String dataCollectionName = Optional.ofNullable(dataCollectionFileEntity).map(DataCollectionFileEntity::getDataCollection).map(DataCollectionEntity::getName).orElse(null);
                    templateEntity = templateService.create(templateName, templateModel.getType(), dataCollectionName, context.getEmail(), templateParts);
                    context.map(templateModel.getId(), templateEntity);
                } else {
                    templateEntity = templateService.createNewVersion(templateName, templateEntity.getLatestFile().getData(), context.getEmail(), version.getComment(), null, templateParts);
                }

                final TemplateFileEntity templateFileEntity = templateEntity.getLatestFile();
                templateFileEntity.setDataCollectionFile(dataCollectionFileEntity);
                final TemplateFileEntity savedEntity = updateDependencies(templateFileEntity,
                        model,
                        context,
                        templateModel.getId(),
                        version.getVersion());

                context.map(templateModel.getId(), version.getVersion(), savedEntity);
            } catch (IOException ioException) {
                throw new TemplateImportProjectException("Importing archive is broken or corrupted, could not load file " + version.getLocalPath() + " for template", ioException);
            }
        }
        return templateEntity;
    }

    private List<TemplatePartModel> getTemplateParts(final DtmFileDescriptorModel model,
                                                     final DtmFileImportContext context,
                                                     final byte[] data,
                                                     final String templateId,
                                                     final Long version) {
        final Document document = Jsoup.parse(new String(data, StandardCharsets.UTF_8));
        final List<TemplatePartModel> result = new ArrayList<>();
        document.body().getElementsByTag("object").stream()
                .map(element -> element.attr("data-dito-fragment"))
                .forEachOrdered(
                        attr -> {
                            final DtmTemplateDescriptorModel dtmTemplateDescriptorModel = model.getTemplates().stream().filter(templateModel -> Objects.equals(attr, templateModel.getAlias())).findAny().orElse(null);
                            final DtmTemplateUsedInDescriptorModel versionDescriptorModel = Optional.ofNullable(dtmTemplateDescriptorModel).stream()
                                    .flatMap(templateModel -> templateModel.getVersions().stream())
                                    .flatMap(versionModel -> versionModel.getUsedIn().stream())
                                    .filter(usedIn -> Objects.equals(version, usedIn.getVersion()) && Objects.equals(Long.valueOf(templateId), usedIn.getId()) && Objects.equals(usedIn.getType(), ItemType.TEMPLATE.getPluralName()))
                                    .findAny().orElse(null);
                            if (dtmTemplateDescriptorModel != null && versionDescriptorModel != null) {
                                final TemplatePartModel part = new TemplatePartModel();
                                try {
                                    part.setPartSettings(objectMapper.readValue(versionDescriptorModel.getSettings(), PartSettings.class));
                                } catch (JsonProcessingException e) {
                                    log.warn("Unable to parse settings: {}", versionDescriptorModel.getSettings());
                                }
                                part.setCondition(versionDescriptorModel.getConditions());
                                templateRepository.findById(context.getTemplateMapping(dtmTemplateDescriptorModel.getId())).ifPresent(partEntity -> part.setTemplateName(partEntity.getName()));
                                result.add(part);
                            }
                        }
                );
        return result;
    }

    private TemplateFileEntity updateDependencies(final TemplateFileEntity templateFileEntity,
                                                  final DtmFileDescriptorModel model,
                                                  final DtmFileImportContext context,
                                                  final String templateId,
                                                  final Long version) {
        final DtmDataCollectionUsedInDescriptorModel search = new DtmDataCollectionUsedInDescriptorModel();
        search.setId(Long.valueOf(templateId));
        search.setType(ItemType.TEMPLATE.getPluralName());
        search.setVersion(version);
        final List<TemplateFilePartEntity> templateParts = templateFileEntity.getParts();
        for (TemplateFilePartEntity templatePart : templateParts) {
            for (DtmTemplateDescriptorModel template : model.getTemplates()) {
                if (Objects.equals(templatePart.getPart().getTemplate().getId(), context.getTemplateMapping(template.getId()))) {
                    updateTemplatePartVersion(context, templatePart, template, templateId, version);
                }
            }
        }
        final TemplateFileEntity fileEntity = templateFileRepository.save(templateFileEntity);
        templateDeploymentService.promoteOnDefaultStage(fileEntity);
        return fileEntity;
    }

    private void updateTemplatePartVersion(final DtmFileImportContext context,
                                           final TemplateFilePartEntity templatePart,
                                           final DtmTemplateDescriptorModel template,
                                           final String templateId,
                                           final Long version) {
        for (DtmTemplateVersionDescriptorModel templateVersion : template.getVersions()) {
            if (templateVersion.getUsedIn().stream().anyMatch(usedIn -> Objects.equals(version, usedIn.getVersion()) && Objects.equals(Long.valueOf(templateId), usedIn.getId()) && Objects.equals(usedIn.getType(), ItemType.TEMPLATE.getPluralName()))) {
                final TemplateFileEntity templatePartEntity = templateFileRepository.findByVersionAndTemplate_Id(
                        context.getTemplateMapping(template.getId(), templateVersion.getVersion()),
                        context.getTemplateMapping(template.getId())
                ).orElseThrow(() -> new TemplateImportProjectException(new TemplateNotFoundException("Could not find template " + template.getName())));
                templatePart.setPart(templatePartEntity);
                break;
            }
        }
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

    @Override
    public ItemType getType() {
        return ItemType.TEMPLATE;
    }

    @Override
    public int getPriority() {
        return 75;
    }
}

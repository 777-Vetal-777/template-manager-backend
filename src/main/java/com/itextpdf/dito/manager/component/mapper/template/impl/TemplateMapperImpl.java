package com.itextpdf.dito.manager.component.mapper.template.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.itextpdf.dito.manager.component.mapper.template.TemplateMapper;
import com.itextpdf.dito.manager.component.security.PermissionCheckHandler;
import com.itextpdf.dito.manager.dto.template.TemplateDTO;
import com.itextpdf.dito.manager.dto.template.TemplateMetadataDTO;
import com.itextpdf.dito.manager.dto.template.TemplateWithSettingsDTO;
import com.itextpdf.dito.manager.dto.template.create.TemplatePartDTO;
import com.itextpdf.dito.manager.dto.template.deployment.TemplateDescriptorDTO;
import com.itextpdf.dito.manager.dto.template.update.TemplateUpdateRequestDTO;
import com.itextpdf.dito.manager.dto.template.version.TemplateDeployedVersionDTO;
import com.itextpdf.dito.manager.entity.PromotionPathEntity;
import com.itextpdf.dito.manager.entity.StageEntity;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionFileEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFilePartEntity;
import com.itextpdf.dito.manager.entity.template.TemplateLogEntity;
import com.itextpdf.dito.manager.model.template.TemplateModelWithRoles;
import com.itextpdf.dito.manager.model.template.part.PartSettings;
import com.itextpdf.dito.manager.model.template.part.TemplatePartModel;
import com.itextpdf.dito.manager.util.TemplateDeploymentUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class TemplateMapperImpl implements TemplateMapper {
    private final ObjectMapper objectMapper;
    private static final Logger log = LogManager.getLogger(TemplateMapperImpl.class);
    private PermissionCheckHandler permissionHandler;

    public TemplateMapperImpl(final ObjectMapper objectMapper, final PermissionCheckHandler permissionHandler) {
        this.objectMapper = objectMapper;
        this.permissionHandler = permissionHandler;
    }

    @Override
    public TemplateDTO map(final TemplateEntity entity, final String email) {
        final TemplateDTO result = new TemplateDTO();
        return fillTemplateDTO(entity, result, email);
    }

    private TemplateDTO fillTemplateDTO(final TemplateEntity entity, final TemplateDTO result, final String email) {
        log.info("Fill templateDto with template: {} and templateDto: {} was started", entity.getId(), result);
        result.setName(entity.getName());
        result.setType(entity.getType());
        final List<TemplateLogEntity> templateLogs = new ArrayList<>(entity.getTemplateLogs());
        if (!CollectionUtils.isEmpty(templateLogs)) {
            final TemplateLogEntity lastTemplateLog = templateLogs.get(0);
            result.setAuthor(new StringBuilder()
                    .append(lastTemplateLog.getAuthor().getFirstName())
                    .append(" ")
                    .append(lastTemplateLog.getAuthor().getLastName())
                    .toString());
            result.setLastUpdate(lastTemplateLog.getDate());

            final TemplateLogEntity firstTemplateLog = templateLogs.get(templateLogs.size() - 1);
            result.setCreatedBy(new StringBuilder()
                    .append(firstTemplateLog.getAuthor().getFirstName())
                    .append(" ")
                    .append(firstTemplateLog.getAuthor().getLastName())
                    .toString());
            result.setCreatedOn(firstTemplateLog.getDate());
            result.setComment(lastTemplateLog.getComment());
        }
        final List<TemplateFileEntity> files = entity.getFiles();
        if (Objects.nonNull(files) && !files.isEmpty()) {
            final TemplateFileEntity fileEntity = files.get(0);
            result.setVersion(fileEntity.getVersion());
            result.setComment(fileEntity.getComment());
        }
        final TemplateFileEntity latestFile = entity.getLatestFile();
        if (Objects.nonNull(latestFile)) {
            final DataCollectionFileEntity dataCollectionFileEntity = latestFile.getDataCollectionFile();
            result.setDataCollection(Objects.nonNull(dataCollectionFileEntity) ? dataCollectionFileEntity.getDataCollection().getName() : null);
        }
        result.setPermissions(permissionHandler.getPermissionsByTemplate(entity, email));
        log.info("Fill templateDto with template: {} and templateDto: {} was started", entity.getId(), result);
        return result;
    }

    @Override
    public TemplateEntity map(final TemplateUpdateRequestDTO dto) {
        log.info("Convert {} to entity was started", dto);
        final TemplateEntity entity = new TemplateEntity();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        log.info("Convert {} to entity was finished successfully", dto);
        return entity;
    }

    @Override
    public Page<TemplateDTO> mapModels(final Page<TemplateModelWithRoles> models, String email) {
        return models.map(templateModelWithRoles -> mapModel(templateModelWithRoles, email));
    }

    @Override
    public TemplateDTO mapModel(final TemplateModelWithRoles model, String email) {
        final TemplateDTO result = new TemplateDTO();
        result.setName(model.getName());
        result.setType(model.getType());
        result.setVersion(model.getVersion());
        result.setAuthor(model.getAuthor());
        result.setComment(model.getComment());
        result.setCreatedBy(model.getCreatedBy());
        result.setDataCollection(model.getDataCollection());
        result.setCreatedOn(model.getCreatedOn());
        result.setLastUpdate(model.getLastUpdate());
        result.setPermissions(permissionHandler.getPermissionsByTemplate(model, email));
        return result;
    }

    @Override
    public TemplateMetadataDTO mapToMetadata(final TemplateEntity entity, final String email) {
        log.info("Convert template: {} to templateMetadataDTO was started", entity.getId());
        final TemplateMetadataDTO result = new TemplateMetadataDTO();
        result.setName(entity.getName());
        result.setType(entity.getType());
        final List<TemplateFileEntity> templateFiles = entity.getFiles();
        final List<TemplateLogEntity> templateLogs = new ArrayList<>(entity.getTemplateLogs());
        final List<TemplateFileEntity> files = entity.getFiles();
        if (Objects.nonNull(files) && !files.isEmpty()) {
            final TemplateFileEntity fileEntity = files.get(0);
            result.setVersion(fileEntity.getVersion());
            result.setDeployedVersions(files.stream()
                    .map(this::map)
                    .filter(TemplateDeployedVersionDTO::getDeployed)
                    .collect(Collectors.toList()));
        }
        if (!CollectionUtils.isEmpty(templateFiles)) {
            final TemplateLogEntity lastTemplateLog = templateLogs.get(0);
            result.setModifiedBy(new StringBuilder()
                    .append(lastTemplateLog.getAuthor().getFirstName())
                    .append(" ")
                    .append(lastTemplateLog.getAuthor().getLastName())
                    .toString());
            result.setModifiedOn(lastTemplateLog.getDate());

            final TemplateLogEntity firstTemplateLog = templateLogs.get(templateLogs.size() - 1);
            result.setCreatedBy(new StringBuilder()
                    .append(firstTemplateLog.getAuthor().getFirstName())
                    .append(" ")
                    .append(firstTemplateLog.getAuthor().getLastName())
                    .toString());
            result.setCreatedOn(firstTemplateLog.getDate());
        }
        result.setDescription(entity.getDescription());
        final TemplateFileEntity templateFileEntity = entity.getLatestFile();
        final DataCollectionFileEntity dataCollectionFileEntity = templateFileEntity.getDataCollectionFile();
        result.setDataCollection(Objects.nonNull(dataCollectionFileEntity)
                ? dataCollectionFileEntity.getDataCollection().getName()
                : null);
        final UserEntity blockedUser = entity.getBlockedBy();
        if (blockedUser != null) {
            result.setBlocked(true);
            result.setBlockedBy(new StringBuilder().append(blockedUser.getFirstName()).append(" ").append(blockedUser.getLastName()).toString());
        }
        result.setDeployedVersions(mapToDeployedVersions(entity));
        result.setPermissions(permissionHandler.getPermissionsByTemplate(entity, email));
        log.info("Convert template: {} to templateMetadataDTO was finished successfully", entity);
        return result;
    }

    @Override
    public List<TemplateDTO> map(final List<TemplateEntity> entities, final String email) {
        final List<TemplateDTO> result = new ArrayList<>();

        for (final TemplateEntity entity : entities) {
            result.add(map(entity, email));
        }

        return result;
    }

    @Override
    public List<TemplateWithSettingsDTO> mapTemplatesWithPart(final List<TemplateEntity> entities, final String email) {
        return entities.stream().map(templateEntity -> mapTemplateWithPart(templateEntity, email)).collect(Collectors.toList());
    }

    @Override
    public TemplateWithSettingsDTO mapTemplateWithPart(final TemplateEntity entity, final String email) {
        log.info("Convert template: {} to dto with part was started", entity.getId());
        final TemplateWithSettingsDTO templateWithSettingsDTO = new TemplateWithSettingsDTO();
        fillTemplateDTO(entity, templateWithSettingsDTO, email);
        final PartSettings partSettings = mapPartSettings(entity.getLatestFile().getCompositions().get(0));
        templateWithSettingsDTO.setStartOnNewPage(partSettings.getStartOnNewPage());
        log.info("Convert template: {} to dto with part was finished successfully", entity.getId());
        return templateWithSettingsDTO;
    }

    @Override
    public PartSettings mapPartSettings(TemplateFilePartEntity entity) {
        log.info("Get partSettings from {} was started", entity);
        PartSettings partSettings;
        try {
            partSettings = objectMapper.readValue(entity.getSettings(), PartSettings.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to read PartSettings", e);
            partSettings = new PartSettings();
        }
        log.info("Get partSettings from {} was finished successfully", entity);
        return partSettings;
    }

    @Override
    public TemplateDescriptorDTO mapToDescriptor(final TemplateFileEntity templateFileEntity, final boolean versionAliasRequired) {
        log.info("Convert templateFile: {} and {} to templateDescriptor was started", templateFileEntity.getId(), versionAliasRequired);
        final TemplateDescriptorDTO result = new TemplateDescriptorDTO();
        final String templateName = templateFileEntity.getTemplate().getName();
        final String templateAlias = versionAliasRequired
                ? TemplateDeploymentUtils.getTemplateAliasForDefaultInstance(templateFileEntity)
                : templateName;
        result.setTemplateName(templateName);
        result.setAlias(templateAlias);
        result.setVersion(templateFileEntity.getVersion().toString());
        log.info("Convert templateFile: {} and {} to templateDescriptor was finished successfully", templateFileEntity.getId(), versionAliasRequired);
        return result;
    }

    @Override
    public Page<TemplateDTO> map(final Page<TemplateEntity> entities, String email) {
        return entities.map(templateEntity -> map(templateEntity, email));
    }

    @Override
    public TemplateDeployedVersionDTO map(final TemplateFileEntity templateFileEntity) {
        log.info("Convert templateFile: {} to TemplateDeployedVersionDTO was started", templateFileEntity.getId());
        final TemplateDeployedVersionDTO templateDeployedVersionDTO = new TemplateDeployedVersionDTO();
        final StageEntity stageEntity = templateFileEntity.getStage();
        templateDeployedVersionDTO.setStageName(stageEntity != null ? stageEntity.getName() : null);
        templateDeployedVersionDTO.setVersion(templateFileEntity.getVersion());
        templateDeployedVersionDTO.setDeployed(templateFileEntity.getDeployed());
        log.info("Convert templateFile: {} to TemplateDeployedVersionDTO was finished successfully", templateFileEntity.getId());
        return templateDeployedVersionDTO;
    }

    @Override
    public List<TemplateDeployedVersionDTO> mapToDeployedVersions(final TemplateEntity templateEntity) {
        final List<StageEntity> stagesOnPromotionPath = Optional.ofNullable(templateEntity.getLatestFile().getStage())
                .map(StageEntity::getPromotionPath)
                .map(PromotionPathEntity::getStages)
                .orElse(Collections.emptyList());
        final List<TemplateFileEntity> files = templateEntity.getFiles();
        final List<TemplateDeployedVersionDTO> deployedVersions = new ArrayList<>();
        for (final StageEntity stageEntity : stagesOnPromotionPath) {
            if (stageEntity.getSequenceOrder() == 0) {
                final TemplateFileEntity versionOnDevStage = files.stream()
                        .filter(version -> Objects.equals(version.getStage(), stageEntity))
                        .max(Comparator.comparingLong(TemplateFileEntity::getVersion))
                        .orElse(null);
                deployedVersions.add(map(versionOnDevStage, stageEntity));
            } else {
                final TemplateFileEntity versionOnStage = files.stream()
                        .filter(version -> version.getStage().equals(stageEntity))
                        .findFirst()
                        .orElse(null);
                deployedVersions.add(map(versionOnStage, stageEntity));
            }
        }
        return Lists.reverse(deployedVersions);
    }

    private TemplateDeployedVersionDTO map(final TemplateFileEntity templateFileEntity, final StageEntity stageEntity) {
        final TemplateDeployedVersionDTO templateDeployedVersionDTO = new TemplateDeployedVersionDTO();
        templateDeployedVersionDTO.setStageName(stageEntity.getName());
        templateDeployedVersionDTO.setVersion(templateFileEntity != null ? templateFileEntity.getVersion() : null);
        templateDeployedVersionDTO.setDeployed(templateFileEntity != null && templateFileEntity.getDeployed());
        return templateDeployedVersionDTO;
    }

    @Override
    public TemplatePartModel mapPartDto(final TemplatePartDTO dto) {
        log.info("Convert {} to templatePartModel was started", dto);
        final TemplatePartModel model = new TemplatePartModel();
        model.setTemplateName(dto.getName());
        model.setCondition(dto.getCondition());
        final PartSettings partSettings = new PartSettings();
        Optional.ofNullable(dto.getStartOnNewPage()).ifPresent(partSettings::setStartOnNewPage);
        model.setPartSettings(partSettings);
        log.info("Convert {} to templatePartModel was finished successfully", dto);
        return model;
    }

    @Override
    public List<TemplatePartModel> mapPartDto(final List<TemplatePartDTO> dto) {
        log.info("Convert {} to list templatePartModel was started", dto);
        final List<TemplatePartModel> result;
        if (dto != null) {
            result = dto.stream().map(this::mapPartDto).collect(Collectors.toList());
        } else {
            result = null;
        }
        log.info("Convert {} to list templatePartModel was finished successfully", dto);
        return result;
    }

}
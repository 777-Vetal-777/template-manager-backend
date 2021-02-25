package com.itextpdf.dito.manager.component.mapper.template.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.itextpdf.dito.manager.component.mapper.role.RoleMapper;
import com.itextpdf.dito.manager.component.mapper.template.TemplateMapper;
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
import com.itextpdf.dito.manager.model.template.part.PartSettings;
import com.itextpdf.dito.manager.model.template.part.TemplatePartModel;
import com.itextpdf.dito.manager.util.TemplateDeploymentUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class TemplateMapperImpl implements TemplateMapper {
    private final RoleMapper roleMapper;
    private final ObjectMapper objectMapper;
    private static final Logger log = LogManager.getLogger(TemplateMapperImpl.class);

    public TemplateMapperImpl(final RoleMapper roleMapper, final ObjectMapper objectMapper) {
        this.roleMapper = roleMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public TemplateDTO map(final TemplateEntity entity) {
        final TemplateDTO result = new TemplateDTO();
        return fillTemplateDTO(entity, result);
    }

    private TemplateDTO fillTemplateDTO(final TemplateEntity entity, final TemplateDTO result) {
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
        final Collection<TemplateFileEntity> files = entity.getFiles();
        if (Objects.nonNull(files) && !files.isEmpty()) {
            final TemplateFileEntity fileEntity = files.stream().findFirst().get();
            result.setVersion(fileEntity.getVersion());
            result.setComment(fileEntity.getComment());
        }
        final TemplateFileEntity latestFile = entity.getLatestFile();
        if (Objects.nonNull(latestFile)) {
            final DataCollectionFileEntity dataCollectionFileEntity = latestFile.getDataCollectionFile();
            result.setDataCollection(Objects.nonNull(dataCollectionFileEntity) ? dataCollectionFileEntity.getDataCollection().getName() : null);
        }
        return result;
    }

    @Override
    public TemplateEntity map(final TemplateUpdateRequestDTO dto) {
        final TemplateEntity entity = new TemplateEntity();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        return entity;
    }

    @Override
    public TemplateMetadataDTO mapToMetadata(final TemplateEntity entity) {
        final TemplateMetadataDTO result = new TemplateMetadataDTO();
        result.setName(entity.getName());
        result.setType(entity.getType());
        final List<TemplateFileEntity> templateFiles = entity.getFiles();
        final List<TemplateLogEntity> templateLogs = new ArrayList<>(entity.getTemplateLogs());
        final Collection<TemplateFileEntity> files = entity.getFiles();
        if (Objects.nonNull(files) && !files.isEmpty()) {
            final TemplateFileEntity fileEntity = files.stream().findFirst().get();
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
        result.setAppliedRoles(roleMapper.map(entity.getAppliedRoles()));
        final UserEntity blockedUser = entity.getBlockedBy();
        if (blockedUser != null) {
            result.setBlocked(true);
            result.setBlockedBy(new StringBuilder().append(blockedUser.getFirstName()).append(" ").append(blockedUser.getLastName()).toString());
        }
        result.setDeployedVersions(getDeployedVersions(entity));
        return result;
    }

    @Override
    public List<TemplateDTO> map(final List<TemplateEntity> entities) {
        final List<TemplateDTO> result = new ArrayList<>();

        for (final TemplateEntity entity : entities) {
            result.add(map(entity));
        }

        return result;
    }

    @Override
    public List<TemplateWithSettingsDTO> mapTemplatesWithPart(final List<TemplateEntity> entities) {
        return entities.stream().map(this::mapTemplateWithPart).collect(Collectors.toList());
    }

    @Override
    public TemplateWithSettingsDTO mapTemplateWithPart(final TemplateEntity entity) {
        final TemplateWithSettingsDTO templateWithSettingsDTO = new TemplateWithSettingsDTO();
        fillTemplateDTO(entity, templateWithSettingsDTO);
        final PartSettings partSettings = mapPartSettings(entity.getLatestFile().getCompositions().get(0));
        templateWithSettingsDTO.setStartOnNewPage(partSettings.getStartOnNewPage());
        return templateWithSettingsDTO;
    }

    @Override
    public PartSettings mapPartSettings(TemplateFilePartEntity entity) {
        PartSettings partSettings;
        try {
            partSettings = objectMapper.readValue(entity.getSettings(), PartSettings.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to read PartSettings", e);
            partSettings = new PartSettings();
        }
        return partSettings;
    }

    @Override
    public TemplateDescriptorDTO mapToDescriptor(final TemplateFileEntity templateFileEntity, final boolean versionAliasRequired) {

        final TemplateDescriptorDTO result = new TemplateDescriptorDTO();
        final String templateName = templateFileEntity.getTemplate().getName();
        final String templateAlias = versionAliasRequired
                ? TemplateDeploymentUtils.getTemplateAliasForDefaultInstance(templateFileEntity)
                : templateName;
        result.setTemplateName(templateName);
        result.setAlias(templateAlias);
        result.setVersion(templateFileEntity.getVersion().toString());
        return result;
    }

    @Override
    public Page<TemplateDTO> map(final Page<TemplateEntity> entities) {
        return entities.map(this::map);
    }

    @Override
    public TemplateDeployedVersionDTO map(final TemplateFileEntity templateFileEntity) {
        final TemplateDeployedVersionDTO templateDeployedVersionDTO = new TemplateDeployedVersionDTO();
        final StageEntity stageEntity = templateFileEntity.getStage();
        templateDeployedVersionDTO.setStageName(stageEntity != null ? stageEntity.getName() : null);
        templateDeployedVersionDTO.setVersion(templateFileEntity.getVersion());
        templateDeployedVersionDTO.setDeployed(templateFileEntity.getDeployed());
        return templateDeployedVersionDTO;
    }

    private List<TemplateDeployedVersionDTO> getDeployedVersions(final TemplateEntity templateEntity) {
        final PromotionPathEntity promotionPathEntity = templateEntity.getLatestFile().getStage().getPromotionPath();
        final List<TemplateFileEntity> files = templateEntity.getFiles();
        final List<StageEntity> stagesOnPromotionPath = promotionPathEntity.getStages();
        final List<TemplateDeployedVersionDTO> deployedVersions = new ArrayList<>();
        for (final StageEntity stageEntity : stagesOnPromotionPath) {
            if (stageEntity.getSequenceOrder() == 0) {
                final TemplateFileEntity versionOnDevStage = files.stream()
                        .filter(version -> version.getStage().equals(stageEntity))
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
        templateDeployedVersionDTO.setDeployed(templateFileEntity != null ? templateFileEntity.getDeployed() : false);
        return templateDeployedVersionDTO;
    }

    @Override
    public TemplatePartModel mapPartDto(final TemplatePartDTO dto) {
        final TemplatePartModel model = new TemplatePartModel();
        model.setTemplateName(dto.getName());
        model.setCondition(dto.getCondition());
        final PartSettings partSettings = new PartSettings();
        Optional.ofNullable(dto.getStartOnNewPage()).ifPresent(partSettings::setStartOnNewPage);
        model.setPartSettings(partSettings);
        return model;
    }

    @Override
    public List<TemplatePartModel> mapPartDto(final List<TemplatePartDTO> dto) {
        final List<TemplatePartModel> result;
        if (dto != null) {
            result = dto.stream().map(this::mapPartDto).collect(Collectors.toList());
        } else {
            result = null;
        }
        return result;
    }

}
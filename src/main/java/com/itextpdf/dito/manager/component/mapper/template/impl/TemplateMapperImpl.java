package com.itextpdf.dito.manager.component.mapper.template.impl;

import com.itextpdf.dito.manager.component.mapper.role.RoleMapper;
import com.itextpdf.dito.manager.component.mapper.template.TemplateMapper;
import com.itextpdf.dito.manager.dto.template.TemplateDTO;
import com.itextpdf.dito.manager.dto.template.deployment.TemplateDescriptorDTO;
import com.itextpdf.dito.manager.dto.template.TemplateMetadataDTO;
import com.itextpdf.dito.manager.dto.template.update.TemplateUpdateRequestDTO;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionFileEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;
import com.itextpdf.dito.manager.entity.template.TemplateLogEntity;
import com.itextpdf.dito.manager.util.TemplateDeploymentUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Component
public class TemplateMapperImpl implements TemplateMapper {
    private final RoleMapper roleMapper;

    public TemplateMapperImpl(final RoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }

    @Override
    public TemplateDTO map(final TemplateEntity entity) {
        final TemplateDTO result = new TemplateDTO();
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
        final List<TemplateFileEntity> templateFiles = entity.getFiles();
        final List<TemplateLogEntity> templateLogs = new ArrayList<>(entity.getTemplateLogs());
        final Collection<TemplateFileEntity> files = entity.getFiles();
        if (Objects.nonNull(files) && !files.isEmpty()) {
            final TemplateFileEntity fileEntity = files.stream().findFirst().get();
            result.setVersion(fileEntity.getVersion());
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
        if (entity.getBlockedBy() != null) {
            result.setBlocked(true);
        }
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

}

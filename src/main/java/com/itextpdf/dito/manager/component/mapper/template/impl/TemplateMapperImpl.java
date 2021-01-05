package com.itextpdf.dito.manager.component.mapper.template.impl;

import com.itextpdf.dito.manager.component.mapper.template.TemplateMapper;
import com.itextpdf.dito.manager.dto.template.TemplateDTO;
import com.itextpdf.dito.manager.dto.template.TemplateMetadataDTO;
import com.itextpdf.dito.manager.dto.template.TemplateVersionDTO;
import com.itextpdf.dito.manager.dto.template.update.TemplateUpdateRequestDTO;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;
import com.itextpdf.dito.manager.entity.template.TemplateLogEntity;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Component
public class TemplateMapperImpl implements TemplateMapper {
    @Override
    public TemplateDTO map(final TemplateEntity entity) {
        final TemplateDTO result = new TemplateDTO();
        result.setName(entity.getName());
        result.setType(entity.getType());
        final Collection<TemplateLogEntity> logs = entity.getTemplateLogs();
        if (Objects.nonNull(logs) && !logs.isEmpty()) {
            final TemplateLogEntity log = logs.stream().findFirst().get();
            result.setLastUpdate(log.getDate());
            final UserEntity updatedBy = log.getAuthor();
            if (Objects.nonNull(updatedBy)) {
                result.setAuthor(new StringBuilder(updatedBy.getFirstName()).append(" ").append(updatedBy.getLastName()).toString());
            }
        }
        result.setDataCollection(Objects.nonNull(entity.getDataCollection())
                ? entity.getDataCollection().getName()
                : null);
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
        final DataCollectionEntity dataCollection = entity.getDataCollection();
        result.setDataCollection(Objects.nonNull(dataCollection)
                ? dataCollection.getName()
                : null);
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
    public Page<TemplateDTO> map(final Page<TemplateEntity> entities) {
        return entities.map(this::map);
    }

    @Override
    public TemplateVersionDTO map(final TemplateFileEntity entity) {
        final TemplateVersionDTO version = new TemplateVersionDTO();
        version.setVersion(entity.getVersion());
        version.setComment(entity.getComment());
        version.setModifiedOn(entity.getCreatedOn());

        final UserEntity author = entity.getAuthor();
        if (Objects.nonNull(author)) {
            version.setModifiedBy(new StringBuilder(author.getFirstName()).append(" ").append(author.getLastName()).toString());
        }
        return version;
    }

    @Override
    public Page<TemplateVersionDTO> mapVersions(final Page<TemplateFileEntity> entities) {
        return entities.map(this::map);
    }
}

package com.itextpdf.dito.manager.component.mapper.template.impl;

import com.itextpdf.dito.manager.component.mapper.template.TemplateMapper;
import com.itextpdf.dito.manager.dto.template.TemplateDTO;
import com.itextpdf.dito.manager.dto.template.TemplateMetadataDTO;
import com.itextpdf.dito.manager.dto.template.update.TemplateUpdateRequestDTO;
import com.itextpdf.dito.manager.entity.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.TemplateEntity;
import com.itextpdf.dito.manager.entity.TemplateFileEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class TemplateMapperImpl implements TemplateMapper {
    @Override
    public TemplateDTO map(final TemplateEntity entity) {
        final TemplateDTO result = new TemplateDTO();
        result.setName(entity.getName());
        result.setType(entity.getType());
        if (entity.getFiles() != null) {
            final TemplateFileEntity file = entity.getFiles().get(0);
            result.setAuthor(new StringBuilder()
                    .append(file.getAuthor().getFirstName())
                    .append(" ")
                    .append(file.getAuthor().getLastName())
                    .toString());
            result.setLastUpdate(file.getVersion());
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
        if (!CollectionUtils.isEmpty(templateFiles)) {
            final TemplateFileEntity lastFileVersion = templateFiles.get(0);
            result.setModifiedBy(new StringBuilder()
                    .append(lastFileVersion.getAuthor().getFirstName())
                    .append(" ")
                    .append(lastFileVersion.getAuthor().getLastName())
                    .toString());
            result.setModifiedOn(lastFileVersion.getVersion());

            final TemplateFileEntity firstFileVersion = templateFiles.get(templateFiles.size() - 1);
            result.setCreatedBy(new StringBuilder()
                    .append(firstFileVersion.getAuthor().getFirstName())
                    .append(" ")
                    .append(firstFileVersion.getAuthor().getLastName())
                    .toString());
            result.setCreatedOn(firstFileVersion.getVersion());
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
}

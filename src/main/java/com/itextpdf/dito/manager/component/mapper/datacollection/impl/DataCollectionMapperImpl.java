package com.itextpdf.dito.manager.component.mapper.datacollection.impl;

import com.itextpdf.dito.manager.component.mapper.datacollection.DataCollectionMapper;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionDTO;
import com.itextpdf.dito.manager.dto.datacollection.update.DataCollectionUpdateRequestDTO;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;

import com.itextpdf.dito.manager.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class DataCollectionMapperImpl implements DataCollectionMapper {
    @Override
    public DataCollectionEntity map(DataCollectionUpdateRequestDTO dto) {
        final DataCollectionEntity entity = new DataCollectionEntity();
        entity.setName(dto.getName());
        entity.setType(dto.getType());
        entity.setDescription(dto.getDescription());
        return entity;
    }

    @Override
    public DataCollectionDTO map(final DataCollectionEntity entity) {
        final DataCollectionDTO dto = new DataCollectionDTO();
        final UserEntity modifiedBy = entity.getModifiedBy();
        dto.setName(entity.getName());
        dto.setType(entity.getType());
        dto.setModifiedBy(new StringBuilder(modifiedBy.getFirstName()).append(" ").append(modifiedBy.getLastName()).toString());
        dto.setModifiedOn(entity.getModifiedOn());
        dto.setCreatedOn(entity.getCreatedOn());
        dto.setFileName(entity.getFileName());
        dto.setAuthorFirstName(entity.getAuthor().getFirstName());
        dto.setAuthorLastName(entity.getAuthor().getLastName());
        dto.setDescription(entity.getDescription());
        dto.setAttachment(new String(entity.getData()));
        return dto;
    }

    @Override
    public Page<DataCollectionDTO> map(final Page<DataCollectionEntity> entities) {
        return entities.map(this::map);
    }
}

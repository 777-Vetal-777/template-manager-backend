package com.itextpdf.dito.manager.component.mapper.datacollection.impl;

import com.itextpdf.dito.manager.component.mapper.datacollection.DataCollectionMapper;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionDTO;
import com.itextpdf.dito.manager.dto.datacollection.update.DataCollectionUpdateRequestDTO;
import com.itextpdf.dito.manager.entity.DataCollectionEntity;

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
        dto.setName(entity.getName());
        dto.setType(entity.getType());
        dto.setModifiedBy(new StringBuilder().append(entity.getAuthor().getFirstName()).append(" ").append(entity.getAuthor().getLastName()).toString());
        dto.setModifiedOn(entity.getModifiedOn());
        dto.setCreatedOn(entity.getCreatedOn());
        dto.setFileName(entity.getFileName());
        dto.setDescription(entity.getDescription());
        dto.setAttachment(new String(entity.getData()));
        return dto;
    }

    @Override
    public Page<DataCollectionDTO> map(final Page<DataCollectionEntity> entities) {
        return entities.map(this::map);
    }
}

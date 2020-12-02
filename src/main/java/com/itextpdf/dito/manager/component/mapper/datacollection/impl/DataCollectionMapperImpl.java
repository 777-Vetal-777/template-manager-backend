package com.itextpdf.dito.manager.component.mapper.datacollection.impl;

import com.itextpdf.dito.manager.component.mapper.datacollection.DataCollectionMapper;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionCreateRequestDTO;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionDTO;
import com.itextpdf.dito.manager.entity.DataCollectionEntity;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class DataCollectionMapperImpl implements DataCollectionMapper {
    @Override
    public DataCollectionEntity map(final DataCollectionCreateRequestDTO dto) {
        final DataCollectionEntity entity = new DataCollectionEntity();
        entity.setName(dto.getName());
        entity.setType(dto.getType());
        return entity;
    }

    @Override
    public DataCollectionDTO map(final DataCollectionEntity entity) {
        final DataCollectionDTO dto = new DataCollectionDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setType(entity.getType());
        dto.setAuthorFirstName(entity.getAuthor().getFirstName());
        dto.setAuthorLastName(entity.getAuthor().getLastName());
        dto.setModifiedOn(entity.getModifiedOn());
        dto.setAttachment(new String(entity.getData()));
        return dto;
    }

    @Override
    public Page<DataCollectionDTO> map(final Page<DataCollectionEntity> entities) {
        return entities.map(this::map);
    }
}

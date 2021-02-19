package com.itextpdf.dito.manager.component.mapper.datacollection.impl;

import com.itextpdf.dito.manager.component.mapper.datacollection.DataCollectionMapper;
import com.itextpdf.dito.manager.component.mapper.role.RoleMapper;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionDTO;
import com.itextpdf.dito.manager.dto.datacollection.update.DataCollectionUpdateRequestDTO;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionFileEntity;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DataCollectionMapperImpl implements DataCollectionMapper {

    private final RoleMapper roleMapper;

    public DataCollectionMapperImpl(final RoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }

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
        dto.setAuthorFirstName(entity.getAuthor().getFirstName());
        dto.setAuthorLastName(entity.getAuthor().getLastName());
        dto.setDescription(entity.getDescription());
        dto.setAppliedRoles(roleMapper.map(entity.getAppliedRoles()));
        return dto;
    }

    @Override
    public DataCollectionDTO mapWithFileWithoutRoles(DataCollectionEntity entity) {
        final DataCollectionDTO dto =map(entity);
        dto.setAppliedRoles(null);
        return dto;
    }

    @Override
    public DataCollectionDTO mapWithFile(final DataCollectionEntity entity) {
        final DataCollectionDTO dto = map(entity);
        final DataCollectionFileEntity latestVersion = entity.getLatestVersion();
        dto.setAttachment(new String(latestVersion.getData()));
        dto.setVersion(latestVersion.getVersion());
        dto.setComment(latestVersion.getComment());
        dto.setFileName(latestVersion.getFileName());
        return dto;
    }

    @Override
    public Page<DataCollectionDTO> map(final Page<DataCollectionEntity> entities) {
        return entities.map(this::map);
    }

    @Override
    public List<DataCollectionDTO> map(final Collection<DataCollectionEntity> entities) {
        return entities.stream().map(this::map).collect(Collectors.toList());
    }


}

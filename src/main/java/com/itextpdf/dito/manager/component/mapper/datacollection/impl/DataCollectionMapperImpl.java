package com.itextpdf.dito.manager.component.mapper.datacollection.impl;

import com.itextpdf.dito.manager.component.mapper.datacollection.DataCollectionMapper;
import com.itextpdf.dito.manager.component.mapper.role.RoleMapper;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionDTO;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionVersionDTO;
import com.itextpdf.dito.manager.dto.datacollection.update.DataCollectionUpdateRequestDTO;
import com.itextpdf.dito.manager.dto.resource.ResourceDTO;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionFileEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceFileEntity;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Objects;

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
        final DataCollectionFileEntity file = entity.getLatestVersion();
        dto.setVersion(file.getVersion());
        dto.setComment(file.getComment());
        dto.setFileName(file.getFileName());
        dto.setAppliedRoles(roleMapper.map(entity.getAppliedRoles()));
        return dto;
    }

    @Override
    public DataCollectionDTO mapWithFile(final DataCollectionEntity entity) {
        final DataCollectionDTO dto = map(entity);
        final Collection<DataCollectionFileEntity> files = entity.getVersions();
        if (Objects.nonNull(files) && !files.isEmpty()) {
            final DataCollectionFileEntity fileEntity = files.stream().findFirst().get();
            dto.setAttachment(new String(fileEntity.getData()));
        }
        return dto;
    }

    @Override
    public Page<DataCollectionDTO> map(final Page<DataCollectionEntity> entities) {
        return entities.map(this::map);
    }

    @Override
    public Page<DataCollectionVersionDTO> mapVersions(final Page<DataCollectionFileEntity> entities) {
        return entities.map(this::mapVersion);
    }

    public DataCollectionVersionDTO mapVersion(final DataCollectionFileEntity entity) {
        final DataCollectionVersionDTO dto = new DataCollectionVersionDTO();
        final UserEntity modifiedBy = entity.getAuthor();

        dto.setVersion(entity.getVersion());
        dto.setModifiedBy(new StringBuilder(modifiedBy.getFirstName()).append(" ").append(modifiedBy.getLastName()).toString());
        dto.setModifiedOn(entity.getCreatedOn());
        dto.setComment(entity.getComment());
        //TODO: DTM-985 add a way to determine deployment status
        dto.setDeploymentStatus(false);

        return dto;
    }

}

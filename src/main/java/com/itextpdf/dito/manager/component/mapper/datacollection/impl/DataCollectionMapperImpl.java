package com.itextpdf.dito.manager.component.mapper.datacollection.impl;

import com.itextpdf.dito.manager.component.mapper.datacollection.DataCollectionMapper;
import com.itextpdf.dito.manager.component.mapper.role.RoleMapper;
import com.itextpdf.dito.manager.component.security.PermissionCheckHandler;
import com.itextpdf.dito.manager.component.security.PermissionHandler;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionDTO;
import com.itextpdf.dito.manager.dto.datacollection.update.DataCollectionUpdateRequestDTO;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.model.datacollection.DataCollectionModelWithRoles;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionFileEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DataCollectionMapperImpl implements DataCollectionMapper {
    private static final Logger log = LogManager.getLogger(DataCollectionMapperImpl.class);
    private final RoleMapper roleMapper;
    @Autowired
    private PermissionCheckHandler permissionHandler;

    public DataCollectionMapperImpl(final RoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }

    @Override
    public DataCollectionEntity map(DataCollectionUpdateRequestDTO dto) {
        log.info("Convert {} to entity was  started", dto);
        final DataCollectionEntity entity = new DataCollectionEntity();
        entity.setName(dto.getName());
        entity.setType(dto.getType());
        entity.setDescription(dto.getDescription());
        log.info("Convert {} to entity was  finished successfully", dto);
        return entity;
    }

    @Override
    public DataCollectionDTO map(final DataCollectionEntity entity, String email) {
        log.info("Convert dataCollection: {} to dto was started", entity.getId());
        final DataCollectionDTO dto = new DataCollectionDTO();
        final UserEntity modifiedBy = entity.getModifiedBy();
        dto.setName(entity.getName());
        dto.setType(entity.getType());
        dto.setModifiedBy(new StringBuilder(modifiedBy.getFirstName()).append(" ").append(modifiedBy.getLastName()).toString());
        dto.setModifiedOn(entity.getModifiedOn());
        dto.setCreatedOn(entity.getCreatedOn());
        dto.setCreatedBy(new StringBuilder(entity.getAuthor().getFirstName())
                .append(" ")
                .append(entity.getAuthor().getLastName())
                .toString());
        dto.setDescription(entity.getDescription());
        dto.setAppliedRoles(roleMapper.map(entity.getAppliedRoles()));
        dto.setPermissions(permissionHandler.getPermissionsByDataCollection(entity, email));
        log.info("Convert dataCollection: {}  to dto was finished successfully", entity.getId());
        return dto;
    }

    @Override
    public DataCollectionDTO mapWithFile(final DataCollectionEntity entity, String email) {
        log.info("Convert dataCollection: {} to dto wit file was started", entity.getId());
        final DataCollectionDTO dto = map(entity, email);
        final DataCollectionFileEntity latestVersion = entity.getLatestVersion();
        dto.setAttachment(new String(latestVersion.getData(), StandardCharsets.UTF_8));
        dto.setVersion(latestVersion.getVersion());
        dto.setComment(latestVersion.getComment());
        dto.setFileName(latestVersion.getFileName());
        log.info("Convert dataCollection: {} to dto wit file was finished successfully", entity.getId());
        return dto;
    }

    @Override
    public Page<DataCollectionDTO> map(final Page<DataCollectionEntity> entities, final String email) {
        return entities.map(dataCollectionEntity -> map(dataCollectionEntity, email));
    }

    @Override
    public List<DataCollectionDTO> map(final Collection<DataCollectionEntity> entities, final String email) {
        return entities.stream().map(dataCollectionEntity -> map(dataCollectionEntity, email)).collect(Collectors.toList());
    }

    @Override
    public Page<DataCollectionDTO> mapModels(final Page<DataCollectionModelWithRoles> entities, final String email) {
        return entities.map(dataCollectionModelWithRoles -> mapModel(dataCollectionModelWithRoles, email));

    }

    @Override
    public DataCollectionDTO mapModel(final DataCollectionModelWithRoles entity, final String email) {
        final DataCollectionDTO dataCollectionDTO = new DataCollectionDTO();
        dataCollectionDTO.setName(entity.getName());
        dataCollectionDTO.setType(entity.getType());
        dataCollectionDTO.setComment(entity.getComment());
        dataCollectionDTO.setModifiedOn(entity.getModifiedOn());
        dataCollectionDTO.setModifiedBy(entity.getModifiedBy());
        dataCollectionDTO.setCreatedOn(entity.getCreatedOn());
        dataCollectionDTO.setPermissions(permissionHandler.getPermissionsByDataCollection(entity, email));
        dataCollectionDTO.setCreatedBy(new StringBuilder(entity.getAuthorFirstName())
                .append(" ")
                .append(entity.getAuthorLastName())
                .toString());
        dataCollectionDTO.setVersion(entity.getVersion());
        return dataCollectionDTO;
    }
}

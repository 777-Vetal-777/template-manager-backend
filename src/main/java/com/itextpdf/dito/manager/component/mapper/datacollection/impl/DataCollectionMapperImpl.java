package com.itextpdf.dito.manager.component.mapper.datacollection.impl;

import com.itextpdf.dito.manager.component.mapper.datacollection.DataCollectionMapper;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionDTO;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionVersionDTO;
import com.itextpdf.dito.manager.dto.datacollection.update.DataCollectionUpdateRequestDTO;
import com.itextpdf.dito.manager.dto.dependency.DependencyDTO;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionFileEntity;
import com.itextpdf.dito.manager.model.datacollection.DataCollectionDependencyModel;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.itextpdf.dito.manager.dto.dependency.DependencyDirectionType.SOFT;
import static com.itextpdf.dito.manager.dto.dependency.DependencyType.TEMPLATE;

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
        dto.setAuthorFirstName(entity.getAuthor().getFirstName());
        dto.setAuthorLastName(entity.getAuthor().getLastName());
        dto.setDescription(entity.getDescription());
        final DataCollectionFileEntity file = entity.getLatestVersion();
        dto.setVersion(file.getVersion());
        dto.setAttachment(new String(file.getData()));
        dto.setComment(file.getComment());
        dto.setFileName(file.getFileName());
        return dto;
    }

    @Override
    public Page<DataCollectionDTO> map(final Page<DataCollectionEntity> entities) {
        return entities.map(this::map);
    }

    @Override
    public DependencyDTO map(final DataCollectionDependencyModel model) {
        final DependencyDTO dependencyDTO = new DependencyDTO();
        dependencyDTO.setVersion(model.getVersion());
        dependencyDTO.setName(model.getName());
        dependencyDTO.setDirectionType(SOFT);
        dependencyDTO.setDependencyType(TEMPLATE);
        dependencyDTO.setActive(Objects.nonNull(model.getOrder()) && !Objects.equals(model.getOrder(), 0));
        return dependencyDTO;
    }

    @Override
    public List<DependencyDTO> map(final List<DataCollectionDependencyModel> models) {
        return models.stream().map(this::map).collect(Collectors.toList());
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

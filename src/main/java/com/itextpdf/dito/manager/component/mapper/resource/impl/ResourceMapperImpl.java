package com.itextpdf.dito.manager.component.mapper.resource.impl;

import com.itextpdf.dito.manager.component.mapper.resource.ResourceMapper;
import com.itextpdf.dito.manager.component.mapper.role.RoleMapper;
import com.itextpdf.dito.manager.dto.dependency.DependencyDTO;
import com.itextpdf.dito.manager.dto.dependency.DependencyDirectionType;
import com.itextpdf.dito.manager.dto.dependency.DependencyType;
import com.itextpdf.dito.manager.dto.resource.ResourceDTO;
import com.itextpdf.dito.manager.dto.resource.ResourceFileDTO;
import com.itextpdf.dito.manager.dto.resource.update.ResourceUpdateRequestDTO;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceFileEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceLogEntity;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.itextpdf.dito.manager.model.resource.ResourceDependencyModel;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class ResourceMapperImpl implements ResourceMapper {
    private final RoleMapper roleMapper;

    public ResourceMapperImpl(final RoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }

    @Override
    public ResourceDTO map(final ResourceEntity entity) {
        final ResourceDTO result = new ResourceDTO();
        result.setName(entity.getName());
        result.setType(entity.getType());
        result.setDescription(entity.getDescription());
        result.setCreatedOn(entity.getCreatedOn());
        final UserEntity author = entity.getCreatedBy();
        if (Objects.nonNull(author)) {
            result.setAuthorFirstName(author.getFirstName());
            result.setAuthorLastName(author.getLastName());
        }

        final Collection<ResourceFileEntity> files = entity.getResourceFiles();
        if (Objects.nonNull(files) && !files.isEmpty()) {
            final ResourceFileEntity fileEntity = files.stream().findFirst().get();
            result.setVersion(fileEntity.getVersion());
            result.setFileName(fileEntity.getFileName());
            result.setComment(fileEntity.getComment());
            result.setDeployed(fileEntity.getDeployed());
        }

        final Collection<ResourceLogEntity> logs = entity.getResourceLogs();
        if (Objects.nonNull(logs) && !logs.isEmpty()) {
            final ResourceLogEntity log = logs.stream().findFirst().get();
            result.setModifiedOn(log.getDate());
            final UserEntity updatedBy = log.getAuthor();
            if (Objects.nonNull(updatedBy)) {
                result.setModifiedBy(
                        new StringBuilder(updatedBy.getFirstName()).append(" ").append(updatedBy.getLastName())
                                .toString());
            }
        }

        result.setAppliedRoles(roleMapper.map(entity.getAppliedRoles()));

        return result;
    }

    @Override
    public ResourceDTO mapWithFile(final ResourceEntity entity) {
        final ResourceDTO dto = map(entity);
        final Collection<ResourceFileEntity> files = entity.getResourceFiles();
        if (Objects.nonNull(files) && !files.isEmpty()) {
            final ResourceFileEntity fileEntity = files.stream().findFirst().get();
            dto.setFile(fileEntity.getFile());
        }
        return dto;
    }

    @Override
    public ResourceEntity map(final ResourceUpdateRequestDTO dto) {
        final ResourceEntity entity = new ResourceEntity();
        entity.setName(dto.getName());
        entity.setType(dto.getType());
        entity.setDescription(dto.getDescription());
        return entity;
    }

    @Override
    public Page<ResourceDTO> map(final Page<ResourceEntity> entities) {
        return entities.map(this::map);
    }

    @Override
    public ResourceFileDTO map(final ResourceFileEntity entity) {
        final ResourceFileDTO version = new ResourceFileDTO();
        version.setVersion(entity.getVersion());
        version.setComment(entity.getComment());
        version.setDeployed(entity.getDeployed());
        version.setModifiedOn(entity.getCreatedOn());

        final UserEntity author = entity.getAuthor();
        if (Objects.nonNull(author)) {
            version.setModifiedBy(new StringBuilder(author.getFirstName()).append(" ").append(author.getLastName()).toString());
        }
        return version;
    }

    @Override
    public Page<ResourceFileDTO> mapVersions(final Page<ResourceFileEntity> entities) {
        return entities.map(this::map);
    }

    @Override
    public DependencyDTO map(final ResourceDependencyModel model) {
        final DependencyDTO dependencyDTO = new DependencyDTO();
        dependencyDTO.setActive(model.getActive());
        dependencyDTO.setName(model.getName());
        dependencyDTO.setVersion(model.getVersion());
        dependencyDTO.setDependencyType(DependencyType.IMAGE);
        dependencyDTO.setDirectionType(DependencyDirectionType.HARD);
        return dependencyDTO;
    }

    @Override
    public Page<DependencyDTO> mapDependencies(final Page<ResourceDependencyModel> entities) {
        return entities.map(this::map);
    }

    @Override
    public List<DependencyDTO> map(final List<ResourceDependencyModel> models) {
        return models.stream().map(this::map).collect(Collectors.toList());
    }
}

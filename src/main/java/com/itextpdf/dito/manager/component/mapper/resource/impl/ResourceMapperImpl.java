package com.itextpdf.dito.manager.component.mapper.resource.impl;

import com.itextpdf.dito.manager.component.mapper.resource.ResourceMapper;
import com.itextpdf.dito.manager.component.mapper.role.RoleMapper;
import com.itextpdf.dito.manager.dto.resource.ResourceDTO;
import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.dto.resource.update.ResourceUpdateRequestDTO;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceFileEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceLogEntity;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
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
            final Map<String, String> uuids = files.stream().collect(Collectors
                    .toMap(entity.getType() == ResourceTypeEnum.FONT ? ResourceFileEntity::getFontName
                            : ResourceFileEntity::getFileName, ResourceFileEntity::getUuid));
            result.setVersion(fileEntity.getVersion());
            result.setComment(fileEntity.getComment());
            result.setDeployed(fileEntity.getDeployed());
            result.setMetadataUrls(uuids);
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

}

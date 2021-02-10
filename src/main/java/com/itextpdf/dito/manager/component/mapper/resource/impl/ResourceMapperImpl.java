package com.itextpdf.dito.manager.component.mapper.resource.impl;

import com.itextpdf.dito.manager.component.mapper.resource.ResourceMapper;
import com.itextpdf.dito.manager.component.mapper.role.RoleMapper;
import com.itextpdf.dito.manager.dto.resource.FileMetaInfoDTO;
import com.itextpdf.dito.manager.dto.resource.ResourceDTO;
import com.itextpdf.dito.manager.dto.resource.ResourceIdDTO;
import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.dto.resource.update.ResourceUpdateRequestDTO;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceFileEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceLogEntity;
import com.itextpdf.dito.manager.exception.template.TemplatePreviewGenerationException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class ResourceMapperImpl implements ResourceMapper {
    private final RoleMapper roleMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

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

        final Collection<ResourceFileEntity> files = entity.getLatestFile();
        if (Objects.nonNull(files) && !files.isEmpty()) {
            final ResourceFileEntity fileEntity = files.stream().findFirst().get();
            final List<FileMetaInfoDTO> fileMetaInfoDTOS = files.stream().map(file -> map(file))
                    .collect(Collectors.toList());
            result.setVersion(fileEntity.getVersion());
            result.setComment(fileEntity.getComment());
            result.setDeployed(fileEntity.getDeployed());
            result.setMetadataUrls(fileMetaInfoDTOS);
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

    private static FileMetaInfoDTO map(final ResourceFileEntity file) {
        final FileMetaInfoDTO fileMetaInfoDTO = new FileMetaInfoDTO();
        fileMetaInfoDTO.setUuid(file.getUuid());
        fileMetaInfoDTO.setFileName(file.getFileName());
        fileMetaInfoDTO.setFontType(file.getFontName());
        return fileMetaInfoDTO;
    }
    //TODO In the future, replace this code with an integration code.
    @Override
    public String encodeId(final String name, final ResourceTypeEnum resourceTypeEnum, final String subName) {
        String result;

        final ResourceIdDTO resourceIdDTO = new ResourceIdDTO();
        resourceIdDTO.setName(name);
        resourceIdDTO.setType(resourceTypeEnum);
        resourceIdDTO.setSubName(subName);
        final String json = serialize(resourceIdDTO);
        result = encode(json);

        return result;
    }

    //TODO In the future, replace this code with an integration code.
    @Override
    public ResourceIdDTO deserialize(final String data) {
        ResourceIdDTO result = null;
        try {
            result = objectMapper.readValue(data, ResourceIdDTO.class);
        } catch (JsonProcessingException e) {
            throw new TemplatePreviewGenerationException( new StringBuilder("Exception when writing resources from the template. Exception: ").append(e.getMessage()).toString());
        }
        return result;
    }
    //TODO In the future, replace this code with an integration code.
    @Override
    public String serialize(final Object data) {
        String result = null;

        try {
            result = objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new TemplatePreviewGenerationException( new StringBuilder("Exception when reading resources from the template. Exception: ").append(e.getMessage()).toString());
        }

        return result;
    }

    @Override
    public String encode(final String name) {
        return Base64.getUrlEncoder().encodeToString(name.getBytes());
    }

    @Override
    public String decode(final String name) {
        return new String(Base64.getUrlDecoder().decode(name));
    }
}

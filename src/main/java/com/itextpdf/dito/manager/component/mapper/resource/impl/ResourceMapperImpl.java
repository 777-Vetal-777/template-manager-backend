package com.itextpdf.dito.manager.component.mapper.resource.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.dito.manager.component.mapper.resource.ResourceMapper;
import com.itextpdf.dito.manager.component.mapper.role.RoleMapper;
import com.itextpdf.dito.manager.component.security.PermissionCheckHandler;
import com.itextpdf.dito.manager.component.security.PermissionHandler;
import com.itextpdf.dito.manager.model.resource.MetaInfoModel;
import com.itextpdf.dito.manager.model.resource.ResourceModelWithRoles;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class ResourceMapperImpl implements ResourceMapper {
    private static final Logger log = LogManager.getLogger(ResourceMapperImpl.class);
    private final RoleMapper roleMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private PermissionCheckHandler permissionHandler;


    public ResourceMapperImpl(final RoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }

    @Override
    public ResourceDTO map(final ResourceEntity entity, final String email) {
        log.info("Convert resource:{} to resource dto was started", entity.getId());
        final ResourceDTO result = new ResourceDTO();
        result.setName(entity.getName());
        result.setType(entity.getType());
        result.setDescription(entity.getDescription());
        result.setCreatedOn(entity.getCreatedOn());
        final UserEntity author = entity.getCreatedBy();
        if (Objects.nonNull(author)) {
            result.setCreatedBy(new StringBuilder(author.getFirstName())
                    .append(" ")
                    .append(author.getLastName())
                    .toString());
        }

        final Collection<ResourceFileEntity> files = entity.getLatestFile();
        if (Objects.nonNull(files) && !files.isEmpty()) {
            final ResourceFileEntity fileEntity = files.stream().findFirst().get();
            final List<FileMetaInfoDTO> fileMetaInfoDTOS = files.stream().map(ResourceMapperImpl::map)
                    .collect(Collectors.toList());
            result.setVersion(fileEntity.getVersion());
            result.setComment(fileEntity.getComment());
            result.setDeployed(fileEntity.getDeployed());
            result.setMetadataUrls(fileMetaInfoDTOS);
        }
        final Collection<ResourceLogEntity> logs = entity.getResourceLogs();
        if (Objects.nonNull(logs) && !logs.isEmpty()) {
            final ResourceLogEntity logEntity = logs.stream().findFirst().get();
            result.setModifiedOn(logEntity.getDate());
            final UserEntity updatedBy = logEntity.getAuthor();
            if (Objects.nonNull(updatedBy)) {
                result.setModifiedBy(
                        new StringBuilder(updatedBy.getFirstName()).append(" ").append(updatedBy.getLastName())
                                .toString());
            }
        }

        result.setAppliedRoles(roleMapper.map(entity.getAppliedRoles()));
        result.setPermissions(permissionHandler.getPermissionsByResource(entity, email));
        log.info("Convert resource:{} to resource dto was finished successfully", entity.getId());
        return result;
    }

    @Override
    public ResourceEntity map(final ResourceUpdateRequestDTO dto) {
        log.info("Convert {} to entity was started", dto);
        final ResourceEntity entity = new ResourceEntity();
        entity.setName(dto.getName());
        entity.setType(dto.getType());
        entity.setDescription(dto.getDescription());
        log.info("Convert {} to entity was finished successfully", dto);
        return entity;
    }

    @Override
    public Page<ResourceDTO> mapModels(final Page<ResourceModelWithRoles> models, String email) {
        return models.map(resourceModelWithRoles -> mapModel(resourceModelWithRoles, email));
    }

    @Override
    public ResourceDTO mapModel(final ResourceModelWithRoles model, final String email) {
        final ResourceDTO resourceDTO = new ResourceDTO();
        resourceDTO.setAppliedRoles(model.getAppliedRoles());
        resourceDTO.setMetadataUrls(map(model.getMetadataUrls()));
        resourceDTO.setName(model.getName());
        resourceDTO.setComment(model.getComment());
        resourceDTO.setDescription(model.getDescription());
        resourceDTO.setType(model.getType());
        resourceDTO.setDeployed(model.getDeployed());
        resourceDTO.setCreatedBy(new StringBuilder(model.getAuthorFirstName())
                .append(" ")
                .append(model.getAuthorLastName())
                .toString());
        resourceDTO.setCreatedOn(model.getCreatedOn());
        resourceDTO.setModifiedBy(model.getModifiedBy());
        resourceDTO.setModifiedOn(model.getModifiedOn());
        resourceDTO.setVersion(model.getVersion());
        resourceDTO.setPermissions(permissionHandler.getPermissionsByResource(model, email));
        return resourceDTO;
    }

    @Override
    public Page<ResourceDTO> map(final Page<ResourceEntity> entities, final String email) {
        return entities.map(resourceEntity -> map(resourceEntity, email));
    }

    private List<FileMetaInfoDTO> map(final List<MetaInfoModel> models) {
        return models.stream().map(this::map).collect(Collectors.toList());

    }

    private FileMetaInfoDTO map(final MetaInfoModel model) {
        final FileMetaInfoDTO fileMetaInfoDTO = new FileMetaInfoDTO();
        fileMetaInfoDTO.setFileName(model.getFileName());
        fileMetaInfoDTO.setUuid(model.getUuid());
        fileMetaInfoDTO.setFontType(model.getFontType());
        return fileMetaInfoDTO;
    }

    private static FileMetaInfoDTO map(final ResourceFileEntity file) {
        log.info("Convert resourceFile: {} to fileMetaInfoDto was started", file.getId());
        final FileMetaInfoDTO fileMetaInfoDTO = new FileMetaInfoDTO();
        fileMetaInfoDTO.setUuid(file.getUuid());
        fileMetaInfoDTO.setFileName(file.getFileName());
        fileMetaInfoDTO.setFontType(file.getFontName());
        log.info("Convert resourceFile: {} to fileMetaInfoDto was finished successfully", file.getId());
        return fileMetaInfoDTO;
    }

    //TODO In the future, replace this code with an integration code.
    @Override
    public String encodeId(final String name, final ResourceTypeEnum resourceTypeEnum, final String subName) {
        log.info("Encode resource with name: {} and type: {} and subName: {} was started", name, resourceTypeEnum, subName);
        String result;

        final ResourceIdDTO resourceIdDTO = new ResourceIdDTO();
        resourceIdDTO.setName(name);
        resourceIdDTO.setType(resourceTypeEnum);
        resourceIdDTO.setSubName(subName);
        final String json = serialize(resourceIdDTO);
        result = encode(json);

        log.info("Encode resource with name: {} and type: {} and subName: {} was finished successfully", name, resourceTypeEnum, subName);
        return result;
    }

    //TODO In the future, replace this code with an integration code.
    @Override
    public ResourceIdDTO deserialize(final String data) {
        final ResourceIdDTO result;
        try {
            result = objectMapper.readValue(data, ResourceIdDTO.class);
        } catch (JsonProcessingException e) {
            log.info(e.getMessage());
            throw new TemplatePreviewGenerationException(new StringBuilder("Exception when writing resources from the template. Exception: ").append(e.getMessage()).toString());
        }
        return result;
    }

    //TODO In the future, replace this code with an integration code.
    @Override
    public String serialize(final Object data) {
        final String result;

        try {
            result = objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.info(e.getMessage());
            throw new TemplatePreviewGenerationException(new StringBuilder("Exception when reading resources from the template. Exception: ").append(e.getMessage()).toString());
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

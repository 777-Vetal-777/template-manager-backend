package com.itextpdf.dito.manager.component.mapper.resource.impl;

import com.itextpdf.dito.manager.component.mapper.resource.ResourceMapper;
import com.itextpdf.dito.manager.component.security.PermissionCheckHandler;
import com.itextpdf.dito.manager.model.resource.MetaInfoModel;
import com.itextpdf.dito.manager.model.resource.ResourceModelWithRoles;
import com.itextpdf.dito.manager.dto.resource.FileMetaInfoDTO;
import com.itextpdf.dito.manager.dto.resource.ResourceDTO;
import com.itextpdf.dito.manager.dto.resource.update.ResourceUpdateRequestDTO;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceFileEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceLogEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class ResourceMapperImpl implements ResourceMapper {
    private static final Logger log = LogManager.getLogger(ResourceMapperImpl.class);
    private final PermissionCheckHandler permissionHandler;


    public ResourceMapperImpl(final PermissionCheckHandler permissionHandler) {
        this.permissionHandler = permissionHandler;
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

}

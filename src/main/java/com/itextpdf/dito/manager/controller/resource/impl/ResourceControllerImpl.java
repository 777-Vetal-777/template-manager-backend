package com.itextpdf.dito.manager.controller.resource.impl;

import com.itextpdf.dito.manager.component.mapper.resource.ResourceMapper;
import com.itextpdf.dito.manager.component.mapper.role.RoleMapper;
import com.itextpdf.dito.manager.controller.AbstractController;
import com.itextpdf.dito.manager.controller.resource.ResourceController;
import com.itextpdf.dito.manager.dto.dependency.DependencyDTO;
import com.itextpdf.dito.manager.dto.dependency.filter.DependencyFilter;
import com.itextpdf.dito.manager.dto.resource.ResourceDTO;
import com.itextpdf.dito.manager.dto.resource.ResourceFileDTO;
import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.dto.resource.update.ApplyRoleRequestDTO;
import com.itextpdf.dito.manager.dto.resource.update.ResourceUpdateRequestDTO;
import com.itextpdf.dito.manager.dto.role.RoleDTO;
import com.itextpdf.dito.manager.entity.RoleEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.exception.resource.NoSuchResourceTypeException;
import com.itextpdf.dito.manager.exception.resource.ResourceExtensionNotSupportedException;
import com.itextpdf.dito.manager.exception.resource.ResourceFileSizeExceedLimitException;
import com.itextpdf.dito.manager.exception.resource.UnreadableResourceException;
import com.itextpdf.dito.manager.filter.resource.ResourceFilter;
import com.itextpdf.dito.manager.filter.role.RoleFilter;
import com.itextpdf.dito.manager.filter.version.VersionFilter;
import com.itextpdf.dito.manager.service.resource.ResourceDependencyService;
import com.itextpdf.dito.manager.service.resource.ResourceService;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import javax.validation.Valid;

import com.itextpdf.dito.manager.service.resource.ResourceVersionsService;
import liquibase.util.file.FilenameUtils;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ResourceControllerImpl extends AbstractController implements ResourceController {
    private final ResourceService resourceService;
    private final ResourceVersionsService resourceVersionsService;
    private final ResourceDependencyService resourceDependencyService;
    private final ResourceMapper resourceMapper;
    private final RoleMapper roleMapper;
    private final List<String> supportedPictureExtensions;
    private final Long sizePictureLimit;

    public ResourceControllerImpl(
            @Value("${resources.pictures.extensions.supported}") final List<String> supportedPictureExtensions,
            @Value("${resources.pictures.size-limit}") final Long sizePictureLimit,
            final ResourceService resourceService,
            final ResourceDependencyService resourceDependencyService,
            final ResourceVersionsService resourceVersionsService,
            final ResourceMapper resourceMapper,
            final RoleMapper roleMapper) {
        this.supportedPictureExtensions = supportedPictureExtensions;
        this.sizePictureLimit = sizePictureLimit;
        this.resourceService = resourceService;
        this.resourceDependencyService = resourceDependencyService;
        this.resourceVersionsService = resourceVersionsService;
        this.resourceMapper = resourceMapper;
        this.roleMapper = roleMapper;
    }

    @Override
    public ResponseEntity<Page<ResourceFileDTO>> getVersions(final Principal principal, final Pageable pageable, final String encodedName, final String type, final VersionFilter filter, final String searchParam) {
        final String decodedName = decodeBase64(encodedName);
        final Page<ResourceFileDTO> versionsDTOs = resourceMapper.mapVersions(resourceVersionsService.list(pageable, decodedName, getResourceType(decodeBase64(type)), filter, searchParam));
        return new ResponseEntity<>(versionsDTOs, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResourceDTO> create(final Principal principal, final String name, final String comment,
            final String type, final MultipartFile file) {
        final ResourceTypeEnum resourceTypeEnum = getResourceType(type);
        checkFileExtensionIsSupported(file);
        checkFileSizeIsNotExceededLimit(file.getSize());
        final byte[] data = getFileBytes(file);
        final ResourceEntity resourceEntity = resourceService
                .createNewVersion(name, resourceTypeEnum, data, file.getOriginalFilename(), principal.getName(),
                        comment);
        return new ResponseEntity<>(resourceMapper.map(resourceEntity), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Page<DependencyDTO>> list(final Pageable pageable, final String resource, final String type, final String searchParam, final DependencyFilter dependencyFilter) {
        final String decodedName = decodeBase64(resource);
        final Page<DependencyDTO> dependencyDTOs = resourceMapper.mapDependencies(resourceDependencyService.list(pageable, decodedName, getResourceType(decodeBase64(type)), dependencyFilter,searchParam));
        return new ResponseEntity<>(dependencyDTOs, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Page<ResourceDTO>> list(final Pageable pageable,
            final ResourceFilter filter,
            final String searchParam) {
        final Page<ResourceDTO> dtos = resourceMapper.map(resourceService.list(pageable, filter, searchParam));
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResourceDTO> get(final String name, final String type) {
        final ResourceEntity entity = resourceService.get(decodeBase64(name), getResourceType(decodeBase64(type)));
        return new ResponseEntity<>(resourceMapper.mapWithFile(entity), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResourceDTO> create(final Principal principal, final String name, final String type,
            final MultipartFile multipartFile) {
        final ResourceTypeEnum resourceTypeEnum = getResourceType(type);
        checkFileExtensionIsSupported(multipartFile);
        checkFileSizeIsNotExceededLimit(multipartFile.getSize());
        byte[] data = getFileBytes(multipartFile);

        final ResourceEntity resourceEntity = resourceService
                .create(name, resourceTypeEnum, data, multipartFile.getOriginalFilename(), principal.getName());
        return new ResponseEntity<>(resourceMapper.map(resourceEntity), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<ResourceDTO> update(final String name, @Valid final ResourceUpdateRequestDTO updateRequestDTO,
            final Principal principal) {
        final ResourceEntity entity = resourceService
                .update(decodeBase64(name), resourceMapper.map(updateRequestDTO), principal.getName());
        final ResourceDTO dto = resourceMapper.mapWithFile(entity);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResourceDTO> applyRole(final String name, final String type,
            @Valid final ApplyRoleRequestDTO applyRoleRequestDTO) {
        final ResourceEntity resourceEntity = resourceService
                .applyRole(decodeBase64(name), getResourceType(decodeBase64(type)), applyRoleRequestDTO.getRoleName(),
                        applyRoleRequestDTO.getPermissions());
        return new ResponseEntity<>(resourceMapper.map(resourceEntity), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Page<RoleDTO>> getRoles(final Pageable pageable, final String name,
                                                  final String type,  RoleFilter filter) {
        final Page<RoleEntity> roleEntities = resourceService.getRoles(pageable, decodeBase64(name), getResourceType(decodeBase64(type)), filter);
        return new ResponseEntity<>(roleMapper.map(roleEntities), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResourceDTO> deleteRole(final String name, final String type,
            final String roleName) {
        final ResourceEntity resourceEntity = resourceService
                .detachRole(decodeBase64(name), getResourceType(decodeBase64(type)), decodeBase64(roleName));
        return new ResponseEntity<>(resourceMapper.map(resourceEntity), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> delete(final String name, final String type) {
    	resourceService.delete(decodeBase64(name), getResourceType(decodeBase64(type)));
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    private ResourceTypeEnum getResourceType(final String type) {
        if (!EnumUtils.isValidEnum(ResourceTypeEnum.class, type)) {
            throw new NoSuchResourceTypeException(type);
        }
        return ResourceTypeEnum.valueOf(type);
    }

    private void checkFileSizeIsNotExceededLimit(final Long fileSize) {
        if (fileSize > sizePictureLimit) {
            throw new ResourceFileSizeExceedLimitException(fileSize);
        }
    }

    private byte[] getFileBytes(final MultipartFile file) {
        byte[] data;
        try {
            data = file.getBytes();
        } catch (IOException e) {
            throw new UnreadableResourceException(file.getOriginalFilename());
        }
        return data;
    }

    private void checkFileExtensionIsSupported(final MultipartFile resource) {
        final String resourceExtension = FilenameUtils.getExtension(resource.getOriginalFilename()).toLowerCase();
        if (!supportedPictureExtensions.contains(resourceExtension)) {
            throw new ResourceExtensionNotSupportedException(resourceExtension);
        }
    }
}

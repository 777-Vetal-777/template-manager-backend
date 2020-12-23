package com.itextpdf.dito.manager.controller.resource.impl;

import com.itextpdf.dito.manager.component.mapper.resource.ResourceMapper;
import com.itextpdf.dito.manager.controller.AbstractController;
import com.itextpdf.dito.manager.controller.resource.ResourceController;
import com.itextpdf.dito.manager.dto.dependency.DependencyDTO;
import com.itextpdf.dito.manager.dto.dependency.filter.DependencyFilterDTO;
import com.itextpdf.dito.manager.dto.resource.ResourceDTO;
import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.dto.resource.update.ResourceUpdateRequestDTO;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.exception.resource.NoSuchResourceTypeException;
import com.itextpdf.dito.manager.exception.resource.ResourceExtensionNotSupportedException;
import com.itextpdf.dito.manager.exception.resource.UnreadableResourceException;
import com.itextpdf.dito.manager.filter.resource.ResourceFilter;
import com.itextpdf.dito.manager.filter.version.VersionFilter;
import com.itextpdf.dito.manager.service.resource.ResourceService;
import liquibase.util.file.FilenameUtils;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RestController
public class ResourceControllerImpl extends AbstractController implements ResourceController {
    private final ResourceService resourceService;
    private final ResourceMapper resourceMapper;
    private final List<String> supportedPictureExtensions;

    public ResourceControllerImpl(@Value("${resources.pictures.extensions.supported}") final List<String> supportedPictureExtensions,
                                  final ResourceService resourceService,
                                  final ResourceMapper resourceMapper) {
        this.supportedPictureExtensions = supportedPictureExtensions;
        this.resourceService = resourceService;
        this.resourceMapper = resourceMapper;
    }

    @Override
    public ResponseEntity<Page<ResourceDTO>> getVersions(final Principal principal, final Pageable pageable, final String name, final ResourceTypeEnum type, final VersionFilter versionFilter, final String searchParam) {
        return null;
    }

    @Override
    public ResponseEntity<ResourceDTO> create(final Principal principal, final String name, final String comment, final String type, final MultipartFile resource) {
        return null;
    }

    @Override
    public ResponseEntity<Page<DependencyDTO>> listDependencies(final Pageable pageable, final String name, final DependencyFilterDTO dependencyFilterDTO, final String searchParam) {
        return null;
    }


    @Override
    public ResponseEntity<Page<ResourceDTO>> list(final Pageable pageable,
                                                  final ResourceFilter filter,
                                                  final String searchParam) {
        final Page<ResourceDTO> dtos = resourceMapper.map(resourceService.list(pageable, filter, searchParam));
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResourceDTO> get(final String name, final ResourceTypeEnum type) {
        final ResourceEntity entity = resourceService.get(decodeBase64(name), type);
        return new ResponseEntity<>(resourceMapper.mapWithFile(entity), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResourceDTO> create(final Principal principal, final String name, final String type, final MultipartFile multipartFile) {
        if (!EnumUtils.isValidEnum(ResourceTypeEnum.class, type)) {
            throw new NoSuchResourceTypeException(type);
        }
        final ResourceTypeEnum resourceTypeEnum = ResourceTypeEnum.valueOf(type);

        final String resourceExtension = FilenameUtils.getExtension(multipartFile.getOriginalFilename()).toLowerCase();
        if (!supportedPictureExtensions.contains(resourceExtension)) {
            throw new ResourceExtensionNotSupportedException(resourceExtension);
        }

        byte[] data;
        try {
            data = multipartFile.getBytes();
        } catch (IOException e) {
            throw new UnreadableResourceException(multipartFile.getOriginalFilename());
        }
        final ResourceEntity resourceEntity = resourceService.create(name, resourceTypeEnum, data, multipartFile.getOriginalFilename(), principal.getName());
        return new ResponseEntity<>(resourceMapper.map(resourceEntity), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<ResourceDTO> update(final String name, @Valid final ResourceUpdateRequestDTO updateRequestDTO, final Principal principal) {
        final ResourceEntity entity = resourceService.update(decodeBase64(name), resourceMapper.map(updateRequestDTO), principal.getName());
        final ResourceDTO dto = resourceMapper.mapWithFile(entity);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> delete(final String name, final ResourceTypeEnum type) {
        return null;
    }
}

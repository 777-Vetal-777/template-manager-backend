package com.itextpdf.dito.manager.controller.resource.impl;

import com.itextpdf.dito.manager.component.encoder.Encoder;
import com.itextpdf.dito.manager.component.mapper.dependency.DependencyMapper;
import com.itextpdf.dito.manager.component.mapper.file.FileVersionMapper;
import com.itextpdf.dito.manager.component.mapper.permission.PermissionMapper;
import com.itextpdf.dito.manager.component.mapper.resource.ResourceMapper;
import com.itextpdf.dito.manager.controller.AbstractController;
import com.itextpdf.dito.manager.controller.resource.ResourceController;
import com.itextpdf.dito.manager.dto.dependency.DependencyDTO;
import com.itextpdf.dito.manager.dto.dependency.filter.DependencyFilter;
import com.itextpdf.dito.manager.dto.file.FileVersionDTO;
import com.itextpdf.dito.manager.dto.permission.ResourcePermissionDTO;
import com.itextpdf.dito.manager.dto.resource.ResourceDTO;
import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.dto.resource.update.ApplyRoleRequestDTO;
import com.itextpdf.dito.manager.dto.resource.update.ResourceUpdateRequestDTO;
import com.itextpdf.dito.manager.entity.resource.FontTypeEnum;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceFileEntity;
import com.itextpdf.dito.manager.exception.resource.IncorrectResourceTypeException;
import com.itextpdf.dito.manager.exception.resource.NoSuchResourceTypeException;
import com.itextpdf.dito.manager.exception.resource.ResourceExtensionNotSupportedException;
import com.itextpdf.dito.manager.exception.resource.ResourceFileSizeExceedLimitException;
import com.itextpdf.dito.manager.filter.resource.ResourceFilter;
import com.itextpdf.dito.manager.filter.resource.ResourcePermissionFilter;
import com.itextpdf.dito.manager.filter.version.VersionFilter;
import com.itextpdf.dito.manager.integration.editor.component.resource.ResponseHeadersUpdater;
import com.itextpdf.dito.manager.model.resource.ResourceModelWithRoles;
import com.itextpdf.dito.manager.model.resource.ResourcePermissionModel;
import com.itextpdf.dito.manager.service.resource.ResourceDependencyService;
import com.itextpdf.dito.manager.service.resource.ResourcePermissionService;
import com.itextpdf.dito.manager.service.resource.ResourceService;
import com.itextpdf.dito.manager.service.resource.ResourceVersionsService;
import liquibase.util.file.FilenameUtils;
import org.apache.commons.lang3.EnumUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.security.Principal;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum.FONT;
import static com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum.IMAGE;
import static com.itextpdf.dito.manager.entity.resource.FontTypeEnum.BOLD;
import static com.itextpdf.dito.manager.entity.resource.FontTypeEnum.BOLD_ITALIC;
import static com.itextpdf.dito.manager.entity.resource.FontTypeEnum.ITALIC;
import static com.itextpdf.dito.manager.entity.resource.FontTypeEnum.REGULAR;
import static com.itextpdf.dito.manager.util.FilesUtils.getFileBytes;

@RestController
public class ResourceControllerImpl extends AbstractController implements ResourceController {
    private static final Logger log = LogManager.getLogger(ResourceControllerImpl.class);
    private final ResourceService resourceService;
    private final ResourceVersionsService resourceVersionsService;
    private final ResourceDependencyService resourceDependencyService;
    private final ResourceMapper resourceMapper;
    private final DependencyMapper dependencyMapper;
    private final Map<ResourceTypeEnum, List<String>> supportedExtensions = new EnumMap<>(ResourceTypeEnum.class);
    private final ResourcePermissionService resourcePermissionService;
    private final PermissionMapper permissionMapper;
    private final FileVersionMapper fileVersionMapper;
    private final Map<ResourceTypeEnum, Long> sizeLimit = new EnumMap<>(ResourceTypeEnum.class);
    private final Encoder encoder;
    private final ResponseHeadersUpdater responseHeadersUpdater;

    public ResourceControllerImpl(
            @Value("${resources.pictures.extensions.supported}") final List<String> supportedPictureExtensions,
            @Value("${resources.stylesheets.extensions.supported}") final List<String> supportedStylesheetExtensions,
            @Value("${resources.fonts.extensions.supported}") final List<String> supportedFontExtensions,
            @Value("${resources.pictures.size-limit}") final Long sizePictureLimit,
            final ResourceService resourceService,
            final ResourceDependencyService resourceDependencyService,
            final ResourceVersionsService resourceVersionsService,
            final ResourceMapper resourceMapper,
            final ResourcePermissionService resourcePermissionService,
            final PermissionMapper permissionMapper,
            final DependencyMapper dependencyMapper,
            final FileVersionMapper fileVersionMapper,
            final ResponseHeadersUpdater responseHeadersUpdater,
            final Encoder encoder) {
        this.supportedExtensions.put(IMAGE, supportedPictureExtensions);
        this.supportedExtensions.put(ResourceTypeEnum.STYLESHEET, supportedStylesheetExtensions);
        this.supportedExtensions.put(ResourceTypeEnum.FONT, supportedFontExtensions);
        this.sizeLimit.put(IMAGE, sizePictureLimit);
        this.resourceService = resourceService;
        this.resourceDependencyService = resourceDependencyService;
        this.resourceVersionsService = resourceVersionsService;
        this.resourceMapper = resourceMapper;
        this.dependencyMapper = dependencyMapper;
        this.resourcePermissionService = resourcePermissionService;
        this.permissionMapper = permissionMapper;
        this.fileVersionMapper = fileVersionMapper;
        this.responseHeadersUpdater = responseHeadersUpdater;
        this.encoder = encoder;
    }

    @Override
    public ResponseEntity<byte[]> getFile(final String uuid) {
        log.info("Get the file using uuid: {} was started ", uuid);
        final ResourceFileEntity file = resourceService.getFile(uuid);
        final HttpHeaders httpHeaders = new HttpHeaders();
        responseHeadersUpdater.updateHeaders(file, IMAGE, httpHeaders);
        log.info("Get the file using uuid: {} was finished successfully", uuid);
        return new ResponseEntity<>(file.getFile(), httpHeaders, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResourceDTO> createFont(final Principal principal, final String name, final String type,
                                                  final MultipartFile regular, final MultipartFile bold, final MultipartFile italic, final MultipartFile boldItalic) {
        log.info("Create resource(font) with name: {} and type: {} was started", name, type);
        final ResourceTypeEnum typeEnum = parseResourceType(type);
        if (typeEnum != FONT) {
            throw new IncorrectResourceTypeException(type);
        }
        final Map<FontTypeEnum, MultipartFile> fileMap = new EnumMap<>(FontTypeEnum.class);
        fileMap.put(REGULAR, regular);
        fileMap.put(BOLD, bold);
        fileMap.put(ITALIC, italic);
        fileMap.put(BOLD_ITALIC, boldItalic);

        fileMap.forEach((key, value) -> checkFileExtensionIsSupported(typeEnum, value));
        final ResourceEntity resourceEntity = resourceService
                .createNewFont(principal.getName(), name, typeEnum, fileMap);
        log.info("Create  resource(font) with name: {} and type: {} was finished successfully", name, type);
        return new ResponseEntity<>(resourceMapper.map(resourceEntity, principal.getName()), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Page<FileVersionDTO>> getVersions(final Principal principal, final Pageable pageable,
                                                            final String encodedName, final String type, final VersionFilter filter, final String searchParam) {
        final String decodedName = encoder.decode(encodedName);
        log.info("Get resource versions by resourceName: {} and resourceType: {} and filter: {} was started", decodedName, type, filter);
        final Page<FileVersionDTO> versionsDTOs = fileVersionMapper.map(resourceVersionsService
                .list(pageable, decodedName, parseResourceTypeFromPath(type), filter, searchParam));
        log.info("Get resource versions by resourceName: {} and resourceType: {} and filter: {} was finished successfully", decodedName, type, filter);
        return new ResponseEntity<>(versionsDTOs, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResourceDTO> createVersion(final Principal principal, final String name, final String comment,
                                                     final String type, final MultipartFile file) {
        log.info("Create new version of resource by resourceName: {} and type: {} and comment: {} was started", name, type, comment);
        final ResourceTypeEnum resourceType = parseResourceType(type);
        if (resourceType == FONT) {
            throw new IncorrectResourceTypeException(type);
        }
        checkFileExtensionIsSupported(resourceType, file);
        checkFileSizeIsNotExceededLimit(resourceType, file.getSize());
        final byte[] data = getFileBytes(file);
        final String originalFilename = file.getOriginalFilename();
        final ResourceEntity resourceEntity = resourceService
                .createNewVersion(name, resourceType, data, originalFilename, principal.getName(), comment);
        log.info("Create new version of resource by resourceName: {} and type: {} and comment: {} was finished successfully", name, type, comment);
        return new ResponseEntity<>(resourceMapper.map(resourceEntity, principal.getName()), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Page<DependencyDTO>> list(final Pageable pageable, final String resource, final String type,
                                                    final String searchParam, final DependencyFilter filter) {
        log.info("Get list dependencies by resourceName: {} and type: {} and searchParam: {} and filter: {} was started", resource, type, searchParam, filter);
        final String decodedName = encoder.decode(resource);
        final Page<DependencyDTO> dependencyDTOs = dependencyMapper.map(resourceDependencyService
                .list(pageable, decodedName, parseResourceTypeFromPath(type), filter, searchParam));
        log.info("Get list dependencies by resourceName: {} and type: {} and searchParam: {} and filter: {} was finished successfully", resource, type, searchParam, filter);
        return new ResponseEntity<>(dependencyDTOs, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<DependencyDTO>> list(final String name, final String type) {
        log.info("Get list dependencies by resourceName: {} and type: {} was started", name, type);
        final String decodedName = encoder.decode(name);
        final ResourceTypeEnum typeEnum = parseResourceTypeFromPath(type);
        final List<DependencyDTO> dependencyDTOs = dependencyMapper
                .map(resourceDependencyService.list(decodedName, typeEnum));
        log.info("Get list dependencies by resourceName: {} and type: {} was finished successfully", name, type);
        return new ResponseEntity<>(dependencyDTOs, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Page<ResourceDTO>> list(final Pageable pageable,
                                                  final ResourceFilter filter, final String searchParam, final Principal principal) {
        log.info("Get list resources by filter: {} and searchParam: {} was started", filter, searchParam);
        final Page<ResourceModelWithRoles> resourceModelWithRoles = resourceService.list(pageable, filter, searchParam);
        log.info("Get list resources by filter: {} and searchParam: {} was finished successfully", filter, searchParam);
        return new ResponseEntity<>(resourceMapper.mapModels(resourceModelWithRoles, principal.getName()), HttpStatus.OK);

    }

    @Override
    public ResponseEntity<ResourceDTO> get(final String name, final String type, final Principal principal) {
        log.info("Get resource by name: {} and type: {} was started", name, type);
        final ResourceEntity entity = resourceService.get(encoder.decode(name), parseResourceTypeFromPath(type));
        log.info("Get resource by name: {} and type: {} was finished successfully", name, type);
        return new ResponseEntity<>(resourceMapper.map(entity, principal.getName()), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResourceDTO> create(final Principal principal, final String name, final String type,
                                              final MultipartFile multipartFile) {
        log.info("Create newResource by name: {} and type: {} was started", name, type);
        final ResourceTypeEnum resourceType = parseResourceType(type);
        if (resourceType == FONT) {
            throw new IncorrectResourceTypeException(type);
        }
        checkFileExtensionIsSupported(resourceType, multipartFile);
        checkFileSizeIsNotExceededLimit(resourceType, multipartFile.getSize());
        final byte[] data = getFileBytes(multipartFile);
        final String originalFilename = multipartFile.getOriginalFilename();

        final ResourceEntity resourceEntity = resourceService.create(name, resourceType, data, originalFilename, principal.getName());
        log.info("Create newResource by name: {} and type: {} was finished successfully", name, type);
        return new ResponseEntity<>(resourceMapper.map(resourceEntity, principal.getName()), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<ResourceDTO> update(final String name, @Valid final ResourceUpdateRequestDTO updateRequestDTO,
                                              final Principal principal) {
        log.info("Update resource by name: {} and updateRequestDTO: {} was started", name, updateRequestDTO);
        final ResourceEntity entity = resourceService
                .update(encoder.decode(name), resourceMapper.map(updateRequestDTO), principal.getName());
        final ResourceDTO dto = resourceMapper.map(entity, principal.getName());
        log.info("Update resource by name: {} and updateRequestDTO: {} was finished successfully", name, updateRequestDTO);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResourceDTO> applyRole(final String name, final String type,
                                                 @Valid final ApplyRoleRequestDTO applyRoleRequestDTO, final Principal principal) {
        log.info("Apply role by resourceName: {} and type: {} and applyRoleRequestDTO: {} was started", name, type, applyRoleRequestDTO);
        final ResourceEntity resourceEntity = resourceService
                .applyRole(encoder.decode(name), parseResourceTypeFromPath(type),
                        applyRoleRequestDTO.getRoleName(),
                        applyRoleRequestDTO.getPermissions());
        log.info("Apply role by resourceName: {} and type: {} and applyRoleRequestDTO: {} was finished successfully", name, type, applyRoleRequestDTO);
        return new ResponseEntity<>(resourceMapper.map(resourceEntity, principal.getName()), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Page<ResourcePermissionDTO>> getRoles(final Pageable pageable, final String name,
                                                                final String type, final ResourcePermissionFilter filter, final String search) {
        log.info("Get roles by resourceName: {} and type: {} and filter: {} and searchParam: {} was started", name, type, filter, search);
        final Page<ResourcePermissionModel> resourcePermissions = resourcePermissionService
                .getRoles(pageable, encoder.decode(name), parseResourceTypeFromPath(type), filter, search);
        log.info("Get roles by resourceName: {} and type: {} and filter: {} and searchParam: {} was finished successfully", name, type, filter, search);
        return new ResponseEntity<>(permissionMapper.mapResourcePermissions(resourcePermissions), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResourceDTO> deleteRole(final String name, final String type,
                                                  final String roleName, final Principal principal) {
        log.info("Delete role by resourceName: {} and type: {} and roleName: {} was started", name, type, roleName);
        final ResourceEntity resourceEntity = resourceService
                .detachRole(encoder.decode(name), parseResourceTypeFromPath(type), encoder.decode(roleName));
        log.info("Delete role by resourceName: {} and type: {} and roleName: {} was finished", name, type, roleName);
        return new ResponseEntity<>(resourceMapper.map(resourceEntity, principal.getName()), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> delete(final Principal principal, final String name, final String type) {
        log.info("Delete resource by resourceName: {} and type: {} was started", name, type);
        resourceService.delete(encoder.decode(name), parseResourceTypeFromPath(type), principal.getName());
        log.info("Delete resource by resourceName: {} and type: {} was finished successfully", name, type);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResourceDTO> rollbackVersion(final Principal principal, final String name, final String type, final Long version) {
        log.info("Rollback resource to selected version by name: {} and type: {} and version: {} was started", name, type, version);
        final ResourceEntity rollBackEntity = resourceVersionsService.rollbackVersion(encoder.decode(name), parseResourceTypeFromPath(type), principal.getName(), version);
        log.info("Rollback resource to selected version by name: {} and type: {} and version: {} was finished successfully", name, type, version);
        return new ResponseEntity<>(resourceMapper.map(rollBackEntity, principal.getName()), HttpStatus.OK);
    }

    private void checkFileSizeIsNotExceededLimit(final ResourceTypeEnum resourceType, final Long fileSize) {
        if (this.sizeLimit.containsKey(resourceType) && fileSize > this.sizeLimit.get(resourceType)) {
            throw new ResourceFileSizeExceedLimitException(fileSize);
        }
    }

    private void checkFileExtensionIsSupported(final ResourceTypeEnum resourceType, final MultipartFile resource) {
        final String resourceExtension = Optional.ofNullable(FilenameUtils.getExtension(resource.getOriginalFilename())).map(String::toLowerCase).orElse("");
        if (this.supportedExtensions.containsKey(resourceType) && !this.supportedExtensions.get(resourceType)
                .contains(resourceExtension)) {
            throw new ResourceExtensionNotSupportedException(resourceExtension);
        }
    }

    private ResourceTypeEnum parseResourceTypeFromPath(final String path) {
        final ResourceTypeEnum result = ResourceTypeEnum.getFromPluralName(path);

        if (result == null) {
            throw new NoSuchResourceTypeException(path);
        }

        return result;
    }

    private ResourceTypeEnum parseResourceType(final String resourceType) {
        if (!EnumUtils.isValidEnum(ResourceTypeEnum.class, resourceType)) {
            throw new NoSuchResourceTypeException(resourceType);
        }

        return ResourceTypeEnum.valueOf(resourceType);
    }
}

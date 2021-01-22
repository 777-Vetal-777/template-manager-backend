package com.itextpdf.dito.manager.controller.resource.impl;

import com.itextpdf.dito.manager.component.mapper.dependency.DependencyMapper;
import com.itextpdf.dito.manager.component.mapper.file.FileVersionMapper;
import com.itextpdf.dito.manager.component.mapper.permission.PermissionMapper;
import com.itextpdf.dito.manager.component.mapper.resource.ResourceMapper;
import com.itextpdf.dito.manager.component.mapper.role.RoleMapper;
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
import com.itextpdf.dito.manager.exception.resource.IncorrectResourceTypeException;
import com.itextpdf.dito.manager.exception.resource.NoSuchResourceTypeException;
import com.itextpdf.dito.manager.exception.resource.ResourceExtensionNotSupportedException;
import com.itextpdf.dito.manager.exception.resource.ResourceFileSizeExceedLimitException;
import com.itextpdf.dito.manager.filter.resource.ResourceFilter;
import com.itextpdf.dito.manager.filter.resource.ResourcePermissionFilter;
import com.itextpdf.dito.manager.filter.version.VersionFilter;
import com.itextpdf.dito.manager.model.resource.ResourcePermissionModel;
import com.itextpdf.dito.manager.service.resource.ResourceDependencyService;
import com.itextpdf.dito.manager.service.resource.ResourcePermissionService;
import com.itextpdf.dito.manager.service.resource.ResourceService;
import com.itextpdf.dito.manager.service.resource.ResourceVersionsService;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import liquibase.util.file.FilenameUtils;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import static com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum.FONT;
import static com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum.IMAGE;
import static com.itextpdf.dito.manager.entity.resource.FontTypeEnum.BOLD;
import static com.itextpdf.dito.manager.entity.resource.FontTypeEnum.BOLD_ITALIC;
import static com.itextpdf.dito.manager.entity.resource.FontTypeEnum.ITALIC;
import static com.itextpdf.dito.manager.entity.resource.FontTypeEnum.REGULAR;
import static com.itextpdf.dito.manager.util.FilesUtils.getFileBytes;

@RestController
public class ResourceControllerImpl extends AbstractController implements ResourceController {
    private final ResourceService resourceService;
    private final ResourceVersionsService resourceVersionsService;
    private final ResourceDependencyService resourceDependencyService;
    private final ResourceMapper resourceMapper;
    private final DependencyMapper dependencyMapper;
    private final RoleMapper roleMapper;
    private final Map<ResourceTypeEnum, List<String>> supportedExtensions = new HashMap<>();
    private final ResourcePermissionService resourcePermissionService;
    private final PermissionMapper permissionMapper;
    private final FileVersionMapper fileVersionMapper;
    private final Map<ResourceTypeEnum, Long> sizeLimit = new HashMap<>();

    public ResourceControllerImpl(
            @Value("${resources.pictures.extensions.supported}") final List<String> supportedPictureExtensions,
            @Value("${resources.stylesheets.extensions.supported}") final List<String> supportedStylesheetExtensions,
            @Value("${resources.fonts.extensions.supported}") final List<String> supportedFontExtensions,
            @Value("${resources.pictures.size-limit}") final Long sizePictureLimit,
            final ResourceService resourceService,
            final ResourceDependencyService resourceDependencyService,
            final ResourceVersionsService resourceVersionsService,
            final ResourceMapper resourceMapper,
            final RoleMapper roleMapper,
            final ResourcePermissionService resourcePermissionService,
            final PermissionMapper permissionMapper,
            final DependencyMapper dependencyMapper,
            final FileVersionMapper fileVersionMapper) {
        this.supportedExtensions.put(IMAGE, supportedPictureExtensions);
        this.supportedExtensions.put(ResourceTypeEnum.STYLESHEET, supportedStylesheetExtensions);
        this.supportedExtensions.put(ResourceTypeEnum.FONT, supportedFontExtensions);
        this.sizeLimit.put(IMAGE, sizePictureLimit);
        this.resourceService = resourceService;
        this.resourceDependencyService = resourceDependencyService;
        this.resourceVersionsService = resourceVersionsService;
        this.resourceMapper = resourceMapper;
        this.roleMapper = roleMapper;
        this.dependencyMapper = dependencyMapper;
        this.resourcePermissionService = resourcePermissionService;
        this.permissionMapper = permissionMapper;
        this.fileVersionMapper = fileVersionMapper;
    }

    @Override
    public ResponseEntity<byte[]> getFile(final String uuid) {
        return new ResponseEntity<>(resourceService.getFile(uuid), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResourceDTO> createFont(final Principal principal, final String name, final String type,
            final MultipartFile regular, final MultipartFile bold, final MultipartFile italic, final MultipartFile boldItalic) {
        final ResourceTypeEnum typeEnum = parseResourceType(type);
        if (typeEnum != FONT) {
            throw new IncorrectResourceTypeException(type);
        }
        final HashMap<FontTypeEnum, MultipartFile> fileMap = new HashMap<>();
        fileMap.put(REGULAR, regular);
        fileMap.put(BOLD, bold);
        fileMap.put(ITALIC, italic);
        fileMap.put(BOLD_ITALIC, boldItalic);

        fileMap.entrySet().forEach(entry -> checkFileExtensionIsSupported(typeEnum, entry.getValue()));
        final ResourceEntity resourceEntity = resourceService
                .createNewFont(principal.getName(), name, typeEnum, fileMap);
        return new ResponseEntity<>(resourceMapper.map(resourceEntity), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Page<FileVersionDTO>> getVersions(final Principal principal, final Pageable pageable,
            final String encodedName, final String type, final VersionFilter filter, final String searchParam) {
        final String decodedName = decodeBase64(encodedName);
        final Page<FileVersionDTO> versionsDTOs = fileVersionMapper.map(resourceVersionsService
                .list(pageable, decodedName, parseResourceTypeFromPath(type), filter, searchParam));
        return new ResponseEntity<>(versionsDTOs, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResourceDTO> create(final Principal principal, final String name, final String comment,
                                              final String type, final MultipartFile file) {
        final ResourceTypeEnum resourceType = parseResourceType(type);
        if (resourceType == FONT) {
            throw new IncorrectResourceTypeException(type);
        }
        checkFileExtensionIsSupported(resourceType, file);
        checkFileSizeIsNotExceededLimit(resourceType, file.getSize());
        final byte[] data = getFileBytes(file);
        final ResourceEntity resourceEntity = resourceService
                .createNewVersion(name, resourceType, data, file.getOriginalFilename(), principal.getName(), comment);
        return new ResponseEntity<>(resourceMapper.map(resourceEntity), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Page<DependencyDTO>> list(final Pageable pageable, final String resource, final String type,
            final String searchParam, final DependencyFilter filter) {
        final String decodedName = decodeBase64(resource);
        final Page<DependencyDTO> dependencyDTOs = dependencyMapper.map(resourceDependencyService
                .list(pageable, decodedName, parseResourceTypeFromPath(type), filter, searchParam));
        return new ResponseEntity<>(dependencyDTOs, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<DependencyDTO>> list(final String name, final String type) {
        final String decodedName = decodeBase64(name);
        final ResourceTypeEnum typeEnum = parseResourceTypeFromPath(type);
        final List<DependencyDTO> dependencyDTOs = dependencyMapper
                .map(resourceDependencyService.list(decodedName, typeEnum));
        return new ResponseEntity<>(dependencyDTOs, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Page<ResourceDTO>> list(final Pageable pageable,
            final ResourceFilter filter, final String searchParam) {
        final Page<ResourceDTO> dtos = resourceMapper.map(resourceService.list(pageable, filter, searchParam));
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResourceDTO> get(final String name, final String type) {
        final ResourceEntity entity = resourceService.get(decodeBase64(name), parseResourceTypeFromPath(type));
        return new ResponseEntity<>(resourceMapper.map(entity), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResourceDTO> create(final Principal principal, final String name, final String type,
                                              final MultipartFile multipartFile) {
        final ResourceTypeEnum resourceType = parseResourceType(type);
        if (resourceType == FONT) {
            throw new IncorrectResourceTypeException(type);
        }
        checkFileExtensionIsSupported(resourceType, multipartFile);
        checkFileSizeIsNotExceededLimit(resourceType, multipartFile.getSize());
        final byte[] data = getFileBytes(multipartFile);
        final String originalFilename = (resourceType == IMAGE ? multipartFile.getOriginalFilename() : "stylesheet.css");

        final ResourceEntity resourceEntity = resourceService
                .create(name, resourceType, data, originalFilename, principal.getName());
        return new ResponseEntity<>(resourceMapper.map(resourceEntity), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<ResourceDTO> update(final String name, @Valid final ResourceUpdateRequestDTO updateRequestDTO,
            final Principal principal) {
        final ResourceEntity entity = resourceService
                .update(decodeBase64(name), resourceMapper.map(updateRequestDTO), principal.getName());
        final ResourceDTO dto = resourceMapper.map(entity);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResourceDTO> applyRole(final String name, final String type,
            @Valid final ApplyRoleRequestDTO applyRoleRequestDTO) {
        final ResourceEntity resourceEntity = resourceService
                .applyRole(decodeBase64(name), parseResourceTypeFromPath(type),
                        applyRoleRequestDTO.getRoleName(),
                        applyRoleRequestDTO.getPermissions());
        return new ResponseEntity<>(resourceMapper.map(resourceEntity), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Page<ResourcePermissionDTO>> getRoles(final Pageable pageable, final String name,
            final String type, final ResourcePermissionFilter filter, final String search) {
        final Page<ResourcePermissionModel> resourcePermissions = resourcePermissionService
                .getRoles(pageable, decodeBase64(name), parseResourceTypeFromPath(type), filter, search);
        return new ResponseEntity<>(permissionMapper.mapResourcePermissions(resourcePermissions), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResourceDTO> deleteRole(final String name, final String type,
            final String roleName) {
        final ResourceEntity resourceEntity = resourceService
                .detachRole(decodeBase64(name), parseResourceTypeFromPath(type), decodeBase64(roleName));
        return new ResponseEntity<>(resourceMapper.map(resourceEntity), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> delete(final String name, final String type) {
        resourceService.delete(decodeBase64(name), parseResourceTypeFromPath(type));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void checkFileSizeIsNotExceededLimit(final ResourceTypeEnum resourceType, final Long fileSize) {
        if (this.sizeLimit.containsKey(resourceType) && fileSize > this.sizeLimit.get(resourceType)) {
            throw new ResourceFileSizeExceedLimitException(fileSize);
        }
    }

    private void checkFileExtensionIsSupported(final ResourceTypeEnum resourceType, final MultipartFile resource) {
        final String resourceExtension = FilenameUtils.getExtension(resource.getOriginalFilename()).toLowerCase();
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

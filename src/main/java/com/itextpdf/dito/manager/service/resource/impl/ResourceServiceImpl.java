package com.itextpdf.dito.manager.service.resource.impl;

import com.itextpdf.dito.manager.component.validator.resource.ContentValidator;
import com.itextpdf.dito.manager.exception.resource.ResourceFontCannotBeRenamedException;
import com.itextpdf.dito.manager.model.resource.MetaInfoModel;
import com.itextpdf.dito.manager.model.resource.ResourceModel;
import com.itextpdf.dito.manager.model.resource.ResourceRoleModel;
import com.itextpdf.dito.manager.model.resource.ResourceModelWithRoles;
import com.itextpdf.dito.manager.dto.permission.PermissionDTO;
import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.dto.role.RoleDTO;
import com.itextpdf.dito.manager.entity.PermissionEntity;
import com.itextpdf.dito.manager.entity.RoleEntity;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.entity.resource.FontTypeEnum;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceFileEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceLogEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.exception.date.InvalidDateRangeException;
import com.itextpdf.dito.manager.exception.resource.InvalidResourceContentException;
import com.itextpdf.dito.manager.exception.resource.ResourceAlreadyExistsException;
import com.itextpdf.dito.manager.exception.resource.ResourceHasDependenciesException;
import com.itextpdf.dito.manager.exception.resource.ResourceNotFoundException;
import com.itextpdf.dito.manager.exception.role.RoleNotFoundException;
import com.itextpdf.dito.manager.filter.resource.ResourceFilter;
import com.itextpdf.dito.manager.filter.role.RoleFilter;
import com.itextpdf.dito.manager.repository.resource.ResourceFileRepository;
import com.itextpdf.dito.manager.repository.resource.ResourceRepository;
import com.itextpdf.dito.manager.repository.template.TemplateRepository;
import com.itextpdf.dito.manager.service.AbstractService;
import com.itextpdf.dito.manager.service.permission.PermissionService;
import com.itextpdf.dito.manager.service.resource.ResourceService;
import com.itextpdf.dito.manager.service.role.RoleService;
import com.itextpdf.dito.manager.service.template.TemplateRefreshLinksService;
import com.itextpdf.dito.manager.service.template.TemplateService;
import com.itextpdf.dito.manager.service.user.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.itextpdf.dito.manager.filter.FilterUtils.getEndDateFromRange;
import static com.itextpdf.dito.manager.filter.FilterUtils.getStartDateFromRange;
import static com.itextpdf.dito.manager.filter.FilterUtils.getStringFromFilter;
import static com.itextpdf.dito.manager.util.FilesUtils.getFileBytes;

@Service
public class ResourceServiceImpl extends AbstractService implements ResourceService {
    private static final Logger log = LogManager.getLogger(ResourceServiceImpl.class);
    private final ResourceRepository resourceRepository;
    private final ResourceFileRepository resourceFileRepository;
    private final TemplateRepository templateRepository;
    private final TemplateService templateService;
    private final UserService userService;
    private final RoleService roleService;
    private final PermissionService permissionService;
    private final TemplateRefreshLinksService templateRefreshLinksService;
    private final Map<ResourceTypeEnum, ContentValidator> contentValidators = new EnumMap<>(ResourceTypeEnum.class);

    public ResourceServiceImpl(
            final ResourceRepository resourceRepository,
            final ResourceFileRepository resourceFileRepository,
            final TemplateRepository templateRepository,
            final UserService userService,
            final RoleService roleService,
            final PermissionService permissionService,
            final TemplateService templateService,
            final List<ContentValidator> contentValidators,
            final TemplateRefreshLinksService templateRefreshLinksService) {
        this.resourceRepository = resourceRepository;
        this.resourceFileRepository = resourceFileRepository;
        this.templateRepository = templateRepository;
        this.userService = userService;
        this.roleService = roleService;
        this.permissionService = permissionService;
        this.templateService = templateService;
        this.templateRefreshLinksService = templateRefreshLinksService;
        this.contentValidators.putAll(contentValidators.stream().collect(Collectors.toMap(ContentValidator::getType, Function.identity())));
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ResourceEntity createNewFont(final String email, final String resourceName,
                                        final ResourceTypeEnum type, final Map<FontTypeEnum, MultipartFile> fonts) {
        log.info("Create resource(Font) by email: {} and resourceName: {} and type: {} and fonts: {} was started", email, resourceName, type, fonts);
        throwExceptionIfResourceExists(resourceName, type);
        final UserEntity userEntity = userService.findActiveUserByEmail(email);

        final ContentValidator contentValidator = contentValidators.get(type);
        if (contentValidators.containsKey(type) && !fonts.values().stream().allMatch(f -> contentValidator.isValid(getFileBytes(f)))) {
            throw new InvalidResourceContentException();
        }

        final ResourceEntity resourceEntity = new ResourceEntity();
        resourceEntity.setName(resourceName);
        resourceEntity.setType(type);
        resourceEntity.setCreatedOn(new Date());
        resourceEntity.setCreatedBy(userEntity);

        final ResourceLogEntity logEntity = createResourceLogEntry(resourceEntity, userEntity);
        final List<ResourceFileEntity> files = new ArrayList<>();
        fonts.forEach((key, value) -> files.add(createFileEntry(userEntity, resourceEntity, getFileBytes(value),
                value.getOriginalFilename(), key.name())));
        resourceEntity.setResourceFiles(files);
        resourceEntity.setLatestFile(files);
        resourceEntity.setResourceLogs(Collections.singletonList(logEntity));
        final ResourceEntity savedResource = resourceRepository.save(resourceEntity);
        log.info("Create resource(Font) by email: {} and resourceName: {} and type: {} and fonts: {} was finished successfully", email, resourceName, type, fonts);
        return savedResource;
    }

    @Override
    public ResourceEntity create(final String name, final ResourceTypeEnum type, final byte[] data,
                                 final String fileName, final String email) {
        log.info("Create resource with name: {}, type: {}, data: {}, fileName: {} and email: {} was started", name, type, data, fileName, email);
        final boolean resourceExists = resourceRepository.existsByNameEqualsAndTypeEquals(name, type);
        if (resourceExists) {
            throw new ResourceAlreadyExistsException(name);
        }

        if (contentValidators.containsKey(type) && !contentValidators.get(type).isValid(data)) {
            throw new InvalidResourceContentException();
        }

        final UserEntity userEntity = userService.findActiveUserByEmail(email);

        final ResourceEntity resourceEntity = new ResourceEntity();
        resourceEntity.setName(name);
        resourceEntity.setType(type);
        resourceEntity.setCreatedOn(new Date());
        resourceEntity.setCreatedBy(userEntity);

        final ResourceLogEntity logEntity = createResourceLogEntry(resourceEntity, userEntity);

        final ResourceFileEntity fileEntity = createFileEntry(userEntity, resourceEntity, data, fileName, null);
        resourceEntity.setLatestFile(Collections.singletonList(fileEntity));
        resourceEntity.setResourceFiles(Collections.singletonList(fileEntity));
        resourceEntity.setResourceLogs(Collections.singletonList(logEntity));
        final ResourceEntity savedResource = resourceRepository.save(resourceEntity);
        log.info("Create resource with name: {}, type: {}, data: {}, fileName: {} and email: {} was finished successfully", name, type, data, fileName, email);
        return savedResource;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ResourceEntity createNewVersion(final String name, final ResourceTypeEnum type, final byte[] data,
                                           final String fileName, final String email, final String comment) {
        log.info("Create resource version with resourceName: {} and type: {} and fileName: {}, email: {}, comment: {} was started", name, type, fileName, email, comment);
        final ResourceEntity existingResourceEntity = getResource(name, type);
        final UserEntity userEntity = userService.findActiveUserByEmail(email);

        if (contentValidators.containsKey(type) && !contentValidators.get(type).isValid(data)) {
            throw new InvalidResourceContentException();
        }

        final Long oldVersion = resourceFileRepository
                .findFirstByResource_IdOrderByVersionDesc(existingResourceEntity.getId()).getVersion();
        final ResourceLogEntity logEntity = createResourceLogEntry(existingResourceEntity, userEntity);

        final ResourceFileEntity fileEntity = new ResourceFileEntity();
        fileEntity.setResource(existingResourceEntity);
        fileEntity.setVersion(oldVersion + 1);
        fileEntity.setFile(data);
        fileEntity.setFileName(fileName);
        fileEntity.setDeployed(false);
        fileEntity.setComment(comment);
        fileEntity.setAuthor(userEntity);
        fileEntity.setCreatedOn(new Date());
        fileEntity.setModifiedOn(new Date());

        resourceFileRepository.save(fileEntity);

        existingResourceEntity.getResourceFiles().add(fileEntity);
        existingResourceEntity.getResourceLogs().add(logEntity);
        final List<ResourceFileEntity> latestFile = existingResourceEntity.getLatestFile();
        latestFile.clear();
        latestFile.addAll(Collections.singletonList(fileEntity));
        final ResourceEntity updatedResourceEntity = resourceRepository.save(existingResourceEntity);

        final List<TemplateEntity> templateEntities = templateRepository
                .findTemplatesByResourceId(existingResourceEntity.getId());
        if (Objects.nonNull(templateEntities)) {
            final String updatedDependenciesComment = new StringBuilder(updatedResourceEntity.getName()).append(" was updated to version ").append(fileEntity.getVersion().toString()).toString();
            templateEntities.forEach(t -> {
                final TemplateEntity extendedTemplateEntity = templateService
                        .createNewVersionAsCopy(t.getLatestFile(), userEntity, updatedDependenciesComment);
                final Set<ResourceFileEntity> resourceFiles = extendedTemplateEntity.getLatestFile().getResourceFiles();
                resourceFiles.removeAll(existingResourceEntity.getResourceFiles());
                resourceFiles.add(fileEntity);
            });
            templateRepository.saveAll(templateEntities);
        }
        log.info("Create resource version with resourceName: {} and type: {} and fileName: {}, email: {}, comment: {} was finished successfully", name, type, fileName, email, comment);
        return updatedResourceEntity;
    }

    @Override
    public ResourceEntity get(final String name, final ResourceTypeEnum type) {
        return getResource(name, type);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ResourceEntity update(final String name, final ResourceEntity entity, final String mail) {
        log.info("Update resource by name: {} and new resource: {} and email: {} was started", name, entity, mail);
        final ResourceEntity existingResource = getResource(name, entity.getType());
        final UserEntity userEntity = userService.findActiveUserByEmail(mail);

        if (!existingResource.getName().equals(entity.getName())) {
            if(entity.getType() == ResourceTypeEnum.FONT){
                throw new ResourceFontCannotBeRenamedException();
            }
            throwExceptionIfResourceExists(entity.getName(), entity.getType());
        }
        existingResource.setDescription(entity.getDescription());
        templateRefreshLinksService.updateResourceLinksInTemplates(existingResource, entity.getName());
        existingResource.setName(entity.getName());

        final ResourceLogEntity logEntity = createResourceLogEntry(existingResource, userEntity);
        existingResource.getResourceLogs().add(logEntity);
        log.info("Update resource by name: {} and new resource: {} and email: {} was finished successfully", name, entity, mail);
        return resourceRepository.save(existingResource);
    }

    @Override
    public ResourceEntity applyRole(final String resourceName, final ResourceTypeEnum resourceType,
                                    final String roleName,
                                    final List<String> permissions) {
        log.info("Apply resource roles by name: {} and type: {} and roleName: {} and permissions: {} was started", resourceName, resourceType, roleName, permissions);
        final ResourceEntity resourceEntity = getResource(resourceName, resourceType);
        RoleEntity slaveRoleEntity = roleService.getSlaveRole(roleName, resourceEntity);
        if (slaveRoleEntity == null) {
            // line below will throw not found exception in case if user tries to create slave role which doesn't have master role.
            final RoleEntity masterRoleEntity = roleService.getMasterRole(roleName);
            checkNotAdminRole(masterRoleEntity);

            slaveRoleEntity = new RoleEntity();
            slaveRoleEntity.setName(masterRoleEntity.getName());
            slaveRoleEntity.setType(masterRoleEntity.getType());
            slaveRoleEntity.setMaster(Boolean.FALSE);
        } else {
            slaveRoleEntity.getPermissions().clear();
            resourceEntity.getAppliedRoles().remove(slaveRoleEntity);
        }

        for (final String permission : permissions) {
            final PermissionEntity permissionEntity = permissionService.get(permission);
            slaveRoleEntity.getPermissions().add(permissionEntity);
        }
        slaveRoleEntity.getResources().add(resourceEntity);

        resourceEntity.getAppliedRoles().add(slaveRoleEntity);
        final ResourceEntity savedResource = resourceRepository.save(resourceEntity);
        log.info("Apply resource roles by name: {} and type: {} and roleName: {} and permissions: {} was finished successfully", resourceName, resourceType, roleName, permissions);
        return savedResource;
    }

    @Override
    public ResourceEntity detachRole(final String name, final ResourceTypeEnum type, final String roleName) {
        log.info("Detach role by resourceName: {} and type: {} and roleName: {} was started", name, type, roleName);
        final ResourceEntity resourceEntity = getResource(name, type);
        final RoleEntity roleEntity = roleService.getSlaveRole(roleName, resourceEntity);

        if (roleEntity == null) {
            throw new RoleNotFoundException(roleName);
        }

        resourceEntity.getAppliedRoles().remove(roleEntity);
        roleService.delete(roleEntity);
        final ResourceEntity savedResourceEntity = resourceRepository.save(resourceEntity);
        log.info("Detach role by resourceName: {} and type: {} and roleName: {} was finished successfully", name, type, roleName);
        return savedResourceEntity;
    }

    @Override
    public Page<RoleEntity> getRoles(final Pageable pageable, final String resourceName, final ResourceTypeEnum type,
                                     final RoleFilter roleFilter) {
        log.info("Ger resource roles by resourceName: {} and type: {} and filter: {} was started", resourceName, type, roleFilter);
        final ResourceEntity resourceEntity = getResource(resourceName, type);
        final Page<RoleEntity> roleEntities = roleService.getSlaveRolesByResource(pageable, roleFilter, resourceEntity);
        log.info("Ger resource roles by resourceName: {} and type: {} and filter: {} was finished successfully", resourceName, type, roleFilter);
        return roleEntities;
    }

    @Override
    public Page<ResourceModelWithRoles> list(final Pageable pageable, final ResourceFilter filter, final String searchParam) {
        log.info("Get list resources by filter: {} and searchParam: {} was started", filter, searchParam);
        throwExceptionIfSortedFieldIsNotSupported(pageable.getSort());

        final Pageable pageWithSort = updateSort(pageable);
        final String name = getStringFromFilter(filter.getName());
        final List<ResourceTypeEnum> resourceTypes = filter.getType();
        final String modifiedBy = getStringFromFilter(filter.getModifiedBy());
        final String comment = getStringFromFilter(filter.getComment());

        Date modifiedOnStartDate = null;
        Date modifiedOnEndDate = null;
        final List<String> modifiedOnDateRange = filter.getModifiedOn();
        if (modifiedOnDateRange != null) {
            if (modifiedOnDateRange.size() != 2) {
                throw new InvalidDateRangeException();
            }
            modifiedOnStartDate = getStartDateFromRange(modifiedOnDateRange);
            modifiedOnEndDate = getEndDateFromRange(modifiedOnDateRange);
        }
        final Page<ResourceModel> resources = StringUtils.isEmpty(searchParam)
                ? resourceRepository.getResourceModelFilter(pageWithSort, name, resourceTypes,
                comment, modifiedBy, modifiedOnStartDate, modifiedOnEndDate)
                : resourceRepository.getResourceModelSearch(pageWithSort, name, resourceTypes, comment, modifiedBy, modifiedOnStartDate, modifiedOnEndDate, searchParam.toLowerCase());
        final List<Long> listId = resources.stream().map(resource -> resource.getId()).collect(Collectors.toList());
        final List<ResourceRoleModel> roles = resourceRepository.getRoles(listId);
        final List<MetaInfoModel> listMetaInfo = resourceRepository.getMetaInfo(listId);
        final Map<Long, List<MetaInfoModel>> map = listMetaInfo.stream().collect(Collectors.groupingBy(MetaInfoModel::getResourceId));
        final Page<ResourceModelWithRoles> page = addMetaInfo(getListResourceWithRoles(roles, resources), map);
        return page;
    }

    private Page<ResourceModelWithRoles> addMetaInfo(final Page<ResourceModelWithRoles> page, final Map<Long, List<MetaInfoModel>> map) {
        for (final ResourceModelWithRoles roles : page) {
            roles.setMetadataUrls(map.get(roles.getResourceId()));
        }
        return page;
    }

    private Page<ResourceModelWithRoles> getListResourceWithRoles(final List<ResourceRoleModel> listRoles, final Page<ResourceModel> models) {
        final Map<Long, List<ResourceRoleModel>> map = listRoles.stream().collect(Collectors.groupingBy(ResourceRoleModel::getResourceId));
        return models.map(resource -> createResourceModelWithRoles(resource, map));
    }

    private ResourceModelWithRoles createResourceModelWithRoles(final ResourceModel resource, final Map<Long, List<ResourceRoleModel>> listRoles) {
        final ResourceModelWithRoles model = new ResourceModelWithRoles();
        model.setName(resource.getResourceName());
        model.setCreatedOn(resource.getCreatedOn());
        model.setModifiedBy(resource.getModifiedBy());
        model.setModifiedOn(resource.getModifiedOn());
        model.setAuthorFirstName(resource.getAuthorFirstName());
        model.setAuthorLastName(resource.getAuthorLastName());
        model.setDescription(resource.getDescription());
        model.setType(resource.getType());
        model.setVersion(resource.getVersion());
        model.setAppliedRoles(addListRolesToModel(listRoles, resource.getId()));
        model.setComment(resource.getComment());
        model.setResourceId(resource.getId());
        return model;
    }

    private Set<RoleDTO> addListRolesToModel(final Map<Long, List<ResourceRoleModel>> map, final Long resourceId) {
        final List<ResourceRoleModel> roles = new ArrayList<>();
        if (map.get(resourceId) != null) {
            roles.addAll(map.get(resourceId));
        }
        final Set<RoleDTO> roleDTOS = new HashSet<>();
        for (final ResourceRoleModel role : roles) {
            final RoleDTO roleDTO = new RoleDTO();
            roleDTO.setId(role.getId());
            roleDTO.setName(role.getRoleName());
            roleDTO.setType(role.getType());
            roleDTO.setMaster(false);
            roleDTO.setPermissions(createListPermissions(role));
            roleDTOS.add(roleDTO);
        }
        return roleDTOS;
    }

    private List<PermissionDTO> createListPermissions(final ResourceRoleModel role) {
        final List<PermissionDTO> list = new ArrayList<>();
        if (role.getE8_US55_EDIT_RESOURCE_METADATA_IMAGE()) {
            final PermissionDTO permissionDTO = createPermission("E8_US55_EDIT_RESOURCE_METADATA_IMAGE");
            list.add(permissionDTO);
        }
        if (role.getE8_US62_CREATE_NEW_VERSION_OF_RESOURCE_IMAGE()) {
            final PermissionDTO permissionDTO = createPermission("E8_US62_CREATE_NEW_VERSION_OF_RESOURCE_IMAGE");
            list.add(permissionDTO);
        }
        if (role.getE8_US65_ROLL_BACK_OF_THE_RESOURCE_IMAGE()) {
            final PermissionDTO permissionDTO = createPermission("E8_US65_ROLL_BACK_OF_THE_RESOURCE_IMAGE");
            list.add(permissionDTO);
        }
        if (role.getE8_US66_DELETE_RESOURCE_IMAGE()) {
            final PermissionDTO permissionDTO = createPermission("E8_US66_DELETE_RESOURCE_IMAGE");
            list.add(permissionDTO);
        }
        if (role.getE8_US66_1_DELETE_RESOURCE_FONT()) {
            final PermissionDTO permissionDTO = createPermission("E8_US66_1_DELETE_RESOURCE_FONT");
            list.add(permissionDTO);
        }
        if (role.getE8_US62_1_CREATE_NEW_VERSION_OF_RESOURCE_FONT()) {
            final PermissionDTO permissionDTO = createPermission("E8_US62_1_CREATE_NEW_VERSION_OF_RESOURCE_FONT");
            list.add(permissionDTO);
        }
        if (role.getE8_US58_EDIT_RESOURCE_METADATA_FONT()) {
            final PermissionDTO permissionDTO = createPermission("E8_US58_EDIT_RESOURCE_METADATA_FONT");
            list.add(permissionDTO);
        }
        if (role.getE8_US65_1_ROLL_BACK_OF_THE_RESOURCE_FONT()) {
            final PermissionDTO permissionDTO = createPermission("E8_US65_1_ROLL_BACK_OF_THE_RESOURCE_FONT");
            list.add(permissionDTO);
        }
        if (role.getE8_US66_2_DELETE_RESOURCE_STYLESHEET()) {
            final PermissionDTO permissionDTO = createPermission("E8_US66_2_DELETE_RESOURCE_STYLESHEET");
            list.add(permissionDTO);
        }
        if (role.getE8_US63_CREATE_NEW_VERSION_OF_RESOURCE_STYLESHEET()) {
            final PermissionDTO permissionDTO = createPermission("E8_US63_CREATE_NEW_VERSION_OF_RESOURCE_STYLESHEET");
            list.add(permissionDTO);
        }
        if (role.getE8_US61_EDIT_RESOURCE_METADATA_STYLESHEET()) {
            final PermissionDTO permissionDTO = createPermission("E8_US61_EDIT_RESOURCE_METADATA_STYLESHEET");
            list.add(permissionDTO);
        }
        if (role.getE8_US65_2_ROLL_BACK_OF_THE_RESOURCE_STYLESHEET()) {
            final PermissionDTO permissionDTO = createPermission("E8_US65_2_ROLL_BACK_OF_THE_RESOURCE_STYLESHEET");
            list.add(permissionDTO);
        }
        return list;
    }

    private PermissionDTO createPermission(final String name) {
        final PermissionDTO permissionDTO = new PermissionDTO();
        permissionDTO.setOptionalForCustomRole(true);
        permissionDTO.setName(name);
        return permissionDTO;
    }


    @Override
    public List<ResourceEntity> list() {
        return resourceRepository.findAll();
    }

    @Override
    public ResourceEntity delete(final String name, final ResourceTypeEnum type, final String mail) {
        log.info("Delete resource by name: {} and type: {} and email: {} was started", name, type, mail);
        final ResourceEntity deletingResourceEntity = getResource(name, type);

        if (hasOutboundDependencies(deletingResourceEntity.getId())) {
            throw new ResourceHasDependenciesException();
        }

        resourceRepository.delete(deletingResourceEntity);
        log.info("Delete resource by name: {} and type: {} and email: {} was finished successfully", name, type, mail);
        return deletingResourceEntity;
    }

    private Pageable updateSort(Pageable pageable) {
        Sort newSort = Sort.by(pageable.getSort().stream()
                .map(sortParam -> {
                    if (sortParam.getProperty().equals("modifiedBy")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "log.author.firstName");
                    }
                    if (sortParam.getProperty().equals("comment")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "file.comment");
                    }
                    return sortParam.getProperty().equals("latestLogRecord.date") ? sortParam : sortParam.ignoreCase();
                })
                .collect(Collectors.toList()));
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), newSort);
    }

    private void throwExceptionIfResourceExists(final String resourceName, final ResourceTypeEnum type) {
        final boolean resourceExists = resourceRepository.existsByNameEqualsAndTypeEquals(resourceName, type);
        if (resourceExists) {
            throw new ResourceAlreadyExistsException(resourceName);
        }
    }

    @Override
    public ResourceEntity getResource(final String name, final ResourceTypeEnum type) {
        return resourceRepository.findByNameAndType(name, type).orElseThrow(() -> new ResourceNotFoundException(name));
    }



    @Override
    public byte[] getFile(final String uuid) {
        final ResourceFileEntity file = resourceFileRepository.findFirstByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException(uuid));
        return file.getFile();
    }

    @Override
    public List<String> getSupportedSortFields() {
        return ResourceRepository.SUPPORTED_SORT_FIELDS;
    }

    private ResourceLogEntity createResourceLogEntry(final ResourceEntity resourceEntity, final UserEntity userEntity) {
        final ResourceLogEntity logEntity = new ResourceLogEntity();
        logEntity.setResource(resourceEntity);
        logEntity.setDate(new Date());
        logEntity.setAuthor(userEntity);
        return logEntity;
    }

    private ResourceFileEntity createFileEntry(final UserEntity userEntity, final ResourceEntity resourceEntity,
                                               final byte[] file, final String fileName, final String fontName) {
        final ResourceFileEntity fileEntity = new ResourceFileEntity();
        fileEntity.setResource(resourceEntity);
        fileEntity.setVersion(1L);
        fileEntity.setFile(file);
        fileEntity.setFileName(fileName);
        fileEntity.setDeployed(false);
        fileEntity.setAuthor(userEntity);
        fileEntity.setCreatedOn(new Date());
        fileEntity.setModifiedOn(new Date());
        fileEntity.setFontName(fontName);
        return fileEntity;
    }

    private boolean hasOutboundDependencies(final Long id) {
        return !resourceFileRepository.searchDependencies(id).isEmpty();
    }

}

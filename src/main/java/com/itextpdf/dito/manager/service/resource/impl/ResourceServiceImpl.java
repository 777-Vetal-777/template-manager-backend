package com.itextpdf.dito.manager.service.resource.impl;

import com.itextpdf.dito.manager.component.validator.resource.ContentValidator;
import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
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
import com.itextpdf.dito.manager.service.template.TemplateService;
import com.itextpdf.dito.manager.service.user.UserService;

import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.Iterator;
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
    private final ResourceRepository resourceRepository;
    private final ResourceFileRepository resourceFileRepository;
    private final TemplateRepository templateRepository;
    private final TemplateService templateService;
    private final UserService userService;
    private final RoleService roleService;
    private final PermissionService permissionService;
    private final Map<ResourceTypeEnum, ContentValidator> contentValidators = new EnumMap<>(ResourceTypeEnum.class);

    public ResourceServiceImpl(
            final ResourceRepository resourceRepository,
            final ResourceFileRepository resourceFileRepository,
            final TemplateRepository templateRepository,
            final UserService userService,
            final RoleService roleService,
            final PermissionService permissionService,
            final TemplateService templateService,
            final List<ContentValidator> contentValidators) {
        this.resourceRepository = resourceRepository;
        this.resourceFileRepository = resourceFileRepository;
        this.templateRepository = templateRepository;
        this.userService = userService;
        this.roleService = roleService;
        this.permissionService = permissionService;
        this.templateService = templateService;
        this.contentValidators.putAll(contentValidators.stream().collect(Collectors.toMap(ContentValidator::getType, Function.identity())));
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ResourceEntity createNewFont(final String email, final String resourceName,
                                        final ResourceTypeEnum type, final Map<FontTypeEnum, MultipartFile> fonts) {
        throwExceptionIfResourceExists(resourceName, type);
        final UserEntity userEntity = userService.findByEmail(email);

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
        return resourceRepository.save(resourceEntity);
    }

    @Override
    public ResourceEntity create(final String name, final ResourceTypeEnum type, final byte[] data,
                                 final String fileName, final String email) {
        final boolean resourceExists = resourceRepository.existsByNameEqualsAndTypeEquals(name, type);
        if (resourceExists) {
            throw new ResourceAlreadyExistsException(name);
        }

        if (contentValidators.containsKey(type) && !contentValidators.get(type).isValid(data)) {
            throw new InvalidResourceContentException();
        }

        final UserEntity userEntity = userService.findByEmail(email);

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
        return resourceRepository.save(resourceEntity);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ResourceEntity createNewVersion(final String name, final ResourceTypeEnum type, final byte[] data,
                                           final String fileName, final String email, final String comment) {
        final ResourceEntity existingResourceEntity = getResource(name, type);
        final UserEntity userEntity = userService.findByEmail(email);

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
            templateEntities.forEach(t -> {
                final TemplateEntity extendedTemplateEntity = templateService
                        .createNewVersionAsCopy(t.getLatestFile(), userEntity, "");
                final Set<ResourceFileEntity> resourceFiles = extendedTemplateEntity.getLatestFile().getResourceFiles();
                resourceFiles.removeAll(existingResourceEntity.getResourceFiles());
                resourceFiles.add(fileEntity);
            });
            templateRepository.saveAll(templateEntities);
        }
        return updatedResourceEntity;
    }

    @Override
    public ResourceEntity get(final String name, final ResourceTypeEnum type) {
        return getResource(name, type);
    }

    @Override
    public ResourceEntity update(final String name, final ResourceEntity entity, final String mail) {
        final ResourceEntity existingResource = getResource(name, entity.getType());
        final UserEntity userEntity = userService.findByEmail(mail);

        if (!existingResource.getName().equals(entity.getName())) {
            throwExceptionIfResourceExists(entity.getName(), entity.getType());
        }
        existingResource.setName(entity.getName());
        existingResource.setDescription(entity.getDescription());

        final ResourceLogEntity log = createResourceLogEntry(existingResource, userEntity);
        existingResource.getResourceLogs().add(log);

        return resourceRepository.save(existingResource);
    }

    @Override
    public ResourceEntity applyRole(final String resourceName, final ResourceTypeEnum resourceType,
                                    final String roleName,
                                    final List<String> permissions) {
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
        return resourceRepository.save(resourceEntity);
    }

    @Override
    public ResourceEntity detachRole(final String name, final ResourceTypeEnum type, final String roleName) {
        final ResourceEntity resourceEntity = getResource(name, type);
        final RoleEntity roleEntity = roleService.getSlaveRole(roleName, resourceEntity);

        if (roleEntity == null) {
            throw new RoleNotFoundException(roleName);
        }

        resourceEntity.getAppliedRoles().remove(roleEntity);
        roleService.delete(roleEntity);
        return resourceRepository.save(resourceEntity);
    }

    @Override
    public Page<RoleEntity> getRoles(final Pageable pageable, final String resourceName, final ResourceTypeEnum type,
                                     final RoleFilter roleFilter) {
        final ResourceEntity resourceEntity = getResource(resourceName, type);
        return roleService.getSlaveRolesByResource(pageable, roleFilter, resourceEntity);
    }

    @Override
    public Page<ResourceEntity> list(final Pageable pageable, final ResourceFilter filter, final String searchParam) {
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
        return StringUtils.isEmpty(searchParam)
                ? resourceRepository
                .filter(pageWithSort, name, resourceTypes, comment, modifiedBy, modifiedOnStartDate, modifiedOnEndDate)
                : resourceRepository.search(pageWithSort, name, resourceTypes, comment, modifiedBy, modifiedOnStartDate,
                modifiedOnEndDate, searchParam.toLowerCase());
    }

    @Override
    @ReadOnlyProperty
    public List<ResourceEntity> list() {
		final List<ResourceEntity> resourceList = resourceRepository.findAll();
		final List<ResourceEntity> resultResourceList = new ArrayList<>(resourceList);
		final Iterator<ResourceEntity> it = resultResourceList.iterator();
		while (it.hasNext()) {			
			final ResourceEntity resourceEntity = it.next();
			if (Objects.equals(resourceEntity.getType(), ResourceTypeEnum.FONT)) {
				for (final ResourceFileEntity resourceFileEntity : resourceEntity.getLatestFile()) {
					final ResourceEntity newEntity = new ResourceEntity();
					final StringBuilder sb = new StringBuilder();
					sb.append(resourceEntity.getName());
					sb.append("_");
					sb.append(resourceFileEntity.getFontName());
					newEntity.setName(sb.toString());
					newEntity.setId(resourceEntity.getId());
					newEntity.setLatestFile(Arrays.asList(new ResourceFileEntity[] { resourceFileEntity }));
					newEntity.setType(resourceEntity.getType());
					resourceList.add(newEntity);
				}
				it.remove();
			}
		}
		return resourceList;
    }

    @Override
    public ResourceEntity delete(final String name, final ResourceTypeEnum type, final String mail) {
        final ResourceEntity deletingResourceEntity = getResource(name, type);

        if (hasOutboundDependencies(deletingResourceEntity.getId())) {
            throw new ResourceHasDependenciesException();
        }

        resourceRepository.delete(deletingResourceEntity);

        return deletingResourceEntity;
    }

    private Pageable updateSort(Pageable pageable) {
        Sort newSort = Sort.by(pageable.getSort().stream()
                .map(sortParam -> {
                    if (sortParam.getProperty().equals("modifiedBy")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "latestLogRecord.author.firstName");
                    }
                    if (sortParam.getProperty().equals("modifiedOn")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "latestLogRecord.date");
                    }
                    if (sortParam.getProperty().equals("comment")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "latestFile.comment");
                    }
                    return sortParam;
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
	@ReadOnlyProperty
	public ResourceEntity get(final String name, final ResourceTypeEnum type, final String fontName) {
		//changing new font name back to db name
		final StringBuilder sb = new StringBuilder();
		sb.append("_");
		sb.append(fontName);
		sb.append("$");
		return resourceRepository.getByNameTypeAndFontName(name.replaceFirst(sb.toString(), ""), type, fontName)
				.orElseThrow(() -> new ResourceNotFoundException(name));
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
        final ResourceLogEntity log = new ResourceLogEntity();
        log.setResource(resourceEntity);
        log.setDate(new Date());
        log.setAuthor(userEntity);
        return log;
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

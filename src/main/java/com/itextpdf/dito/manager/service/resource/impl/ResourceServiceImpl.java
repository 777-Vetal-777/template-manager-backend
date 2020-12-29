package com.itextpdf.dito.manager.service.resource.impl;

import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.entity.PermissionEntity;
import com.itextpdf.dito.manager.entity.RoleEntity;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceFileEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceLogEntity;
import com.itextpdf.dito.manager.exception.resource.ForbiddenOperationException;
import com.itextpdf.dito.manager.exception.resource.ResourceAlreadyExistsException;
import com.itextpdf.dito.manager.exception.resource.ResourceHasDependenciesException;
import com.itextpdf.dito.manager.exception.resource.ResourceNotFoundException;
import com.itextpdf.dito.manager.exception.role.RoleNotFoundException;
import com.itextpdf.dito.manager.filter.resource.ResourceFilter;
import com.itextpdf.dito.manager.repository.resource.ResourceFileRepository;
import com.itextpdf.dito.manager.repository.resource.ResourceLogRepository;
import com.itextpdf.dito.manager.repository.resource.ResourceRepository;
import com.itextpdf.dito.manager.service.AbstractService;
import com.itextpdf.dito.manager.service.permission.PermissionService;
import com.itextpdf.dito.manager.service.resource.ResourceService;
import com.itextpdf.dito.manager.service.role.RoleService;
import com.itextpdf.dito.manager.service.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.itextpdf.dito.manager.filter.FilterUtils.getEndDateFromRange;
import static com.itextpdf.dito.manager.filter.FilterUtils.getStartDateFromRange;
import static com.itextpdf.dito.manager.filter.FilterUtils.getStringFromFilter;

@Service
public class ResourceServiceImpl extends AbstractService implements ResourceService {
    // Image
    private static final String PERMISSION_NAME_FOR_EDIT_METADATA_IMAGE = "E8_US55_EDIT_RESOURCE_METADATA_IMAGE";
    private static final String PERMISSION_NAME_FOR_EDIT_RESOURCE_IMAGE = "E8_US62_CREATE_NEW_VERSION_OF_RESOURCE_IMAGE";
    private static final String PERMISSION_NAME_FOR_ROLLBACK_IMAGE = "E8_US65_ROLL_BACK_OF_THE_RESOURCE_IMAGE";
    private static final String PERMISSION_NAME_FOR_DELETE_IMAGE = "E8_US66_DELETE_RESOURCE_IMAGE";

    private final ResourceRepository resourceRepository;
    private final ResourceLogRepository resourceLogRepository;
    private final ResourceFileRepository resourceFileRepository;
    private final UserService userService;
    private final RoleService roleService;
    private final PermissionService permissionService;

    public ResourceServiceImpl(
            final ResourceRepository resourceRepository,
            final ResourceLogRepository resourceLogRepository,
            final ResourceFileRepository resourceFileRepository,
            final UserService userService,
            final RoleService roleService,
            final PermissionService permissionService) {
        this.resourceRepository = resourceRepository;
        this.resourceLogRepository = resourceLogRepository;
        this.resourceFileRepository = resourceFileRepository;
        this.userService = userService;
        this.roleService = roleService;
        this.permissionService = permissionService;
    }

    @Override
    public ResourceEntity create(final String name, final ResourceTypeEnum type, final byte[] data, final String fileName, final String email) {
        if (resourceRepository.existsByNameEqualsAndTypeEquals(name, type)) {
            throw new ResourceAlreadyExistsException(name);
        }

        final UserEntity userEntity = userService.findByEmail(email);

        final ResourceEntity resourceEntity = new ResourceEntity();
        resourceEntity.setName(name);
        resourceEntity.setType(type);
        resourceEntity.setCreatedOn(new Date());
        resourceEntity.setCreatedBy(userEntity);

        final ResourceLogEntity logEntity = createResourceLogEntry(resourceEntity, userEntity);

        final ResourceFileEntity fileEntity = new ResourceFileEntity();
        fileEntity.setResource(resourceEntity);
        fileEntity.setVersion(1L);
        fileEntity.setFile(data);
        fileEntity.setFileName(fileName);
        fileEntity.setDeployed(false);
        fileEntity.setAuthor(userEntity);
        fileEntity.setCreatedOn(new Date());
        fileEntity.setModifiedOn(new Date());

        resourceEntity.setResourceFiles(Collections.singletonList(fileEntity));
        resourceEntity.setResourceLogs(Collections.singletonList(logEntity));
        return resourceRepository.save(resourceEntity);
    }

    @Override
    public ResourceEntity createNewVersion(final String name, final ResourceTypeEnum type, final byte[] data, final String fileName, final String email, final String comment) {
        final ResourceEntity existingResourceEntity = getResource(name, type);
        final UserEntity userEntity = userService.findByEmail(email);

        switch (type) {
            case IMAGE:
                checkUserPermissions(
                        retrieveSetOfRoleNames(userEntity.getRoles()),
                        existingResourceEntity.getAppliedRoles(), PERMISSION_NAME_FOR_EDIT_RESOURCE_IMAGE);
                break;
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

        existingResourceEntity.getResourceFiles().add(fileEntity);
        existingResourceEntity.getResourceLogs().add(logEntity);
        return resourceRepository.save(existingResourceEntity);
    }

    @Override
    public ResourceEntity get(final String name, final ResourceTypeEnum type) {
        final ResourceEntity resourceEntity = getResource(name, type);
        //specially made to reduce the load of files
        final ResourceFileEntity file = resourceFileRepository.findFirstByResource_IdOrderByVersionDesc(resourceEntity.getId());
        final ResourceLogEntity log = resourceLogRepository.findFirstByResource_IdOrderByDateDesc(resourceEntity.getId());
        resourceEntity.setResourceFiles(Collections.singletonList(file));
        resourceEntity.setResourceLogs(log != null ? Collections.singletonList(log) : null);
        return resourceEntity;
    }

    @Override
    public ResourceEntity update(final String name, final ResourceEntity entity, final String mail) {
        final ResourceEntity existingResource = getResource(name, entity.getType());
        final UserEntity userEntity = userService.findByEmail(mail);

        switch (entity.getType()) {
            case IMAGE:
                checkUserPermissions(retrieveSetOfRoleNames(userEntity.getRoles()),
                        existingResource.getAppliedRoles(), PERMISSION_NAME_FOR_EDIT_METADATA_IMAGE);
                break;
        }

        if (!existingResource.getName().equals(entity.getName())) {
            throwExceptionIfResourceExist(entity);
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
    public Page<RoleEntity> getRoles(final Pageable pageable, final String name, final ResourceTypeEnum type) {
        final ResourceEntity resourceEntity = getResource(name, type);
        return roleService.getSlaveRolesByResource(pageable, resourceEntity);
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
                throw new IllegalArgumentException("Date range should contain two elements: start date and end date");
            }
            modifiedOnStartDate = getStartDateFromRange(modifiedOnDateRange);
            modifiedOnEndDate = getEndDateFromRange(modifiedOnDateRange);
        }
        return StringUtils.isEmpty(searchParam)
                ? resourceRepository.filter(pageWithSort, name, resourceTypes, comment, modifiedBy, modifiedOnStartDate, modifiedOnEndDate)
                : resourceRepository.search(pageWithSort, name, resourceTypes, comment, modifiedBy, modifiedOnStartDate,
                modifiedOnEndDate, searchParam.toLowerCase());
    }
    
    @Override
	public ResourceEntity delete(final String name, final ResourceTypeEnum type, final String mail) {
    	ResourceEntity deletingResourceEntity = getResource(name, type);
		
    	if (hasOutboundDependencies(deletingResourceEntity)) {
    		throw new ResourceHasDependenciesException();
    	}
    	
    	resourceRepository.delete(deletingResourceEntity);
    	
    	final ResourceLogEntity log = createResourceLogEntry(mail, deletingResourceEntity);
        deletingResourceEntity.getResourceLogs().add(log);

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

    private void throwExceptionIfResourceExist(final ResourceEntity entity) {
        if (resourceRepository.existsByNameEqualsAndTypeEquals(entity.getName(), entity.getType())) {
            throw new ResourceAlreadyExistsException(entity.getName());
        }
    }

    @Override
    public ResourceEntity getResource(final String name, final ResourceTypeEnum type) {
        return resourceRepository.findByNameAndType(name, type).orElseThrow(() -> new ResourceNotFoundException(name));
    }

    @Override
    public List<String> getSupportedSortFields() {
        return ResourceRepository.SUPPORTED_SORT_FIELDS;
    }

    private void checkUserPermissions(final Set<String> userRoleNames,
                                      final Set<RoleEntity> resourceAppliedRoles,
                                      final String requiredPermission) {
        if (!isUserAdmin(userRoleNames) && !resourceAppliedRoles.isEmpty()) {

            boolean isPermissionRolePresented = false;
            final Set<String> resourceAppliedRolesWithRequiredPermission = retrieveSetOfRoleNamesFilteredByPermission(
                    resourceAppliedRoles, requiredPermission);
            for (final String role : resourceAppliedRolesWithRequiredPermission) {
                if (userRoleNames.contains(role)) {
                    isPermissionRolePresented = true;
                    break;
                }
            }

            if (!isPermissionRolePresented) {
                throw new ForbiddenOperationException();
            }
        }
    }

    private boolean isUserAdmin(final Set<String> userRoleNames) {
        return userRoleNames.contains("GLOBAL_ADMINISTRATOR") || userRoleNames.contains("ADMINISTRATOR");
    }

    private Set<String> retrieveSetOfRoleNames(final Set<RoleEntity> roleEntities) {
        return roleEntities.stream().map(RoleEntity::getName).collect(Collectors.toSet());
    }

    private Set<String> retrieveSetOfRoleNamesFilteredByPermission(final Set<RoleEntity> roleEntities, final String permission) {
        return roleEntities.stream().filter(roleEntity -> roleEntity.getPermissions().stream()
                .anyMatch(permissionEntity -> permissionEntity.getName().equals(permission)))
                .map(RoleEntity::getName).collect(
                        Collectors.toSet());
    }

	private ResourceLogEntity createResourceLogEntry(final String mail, ResourceEntity resourceEntity) {
		final UserEntity userEntity = userService.findByEmail(mail);
        return createResourceLogEntry(resourceEntity, userEntity);
	}

	private ResourceLogEntity createResourceLogEntry(final ResourceEntity resourceEntity, final UserEntity userEntity) {
		final ResourceLogEntity log = new ResourceLogEntity();
        log.setResource(resourceEntity);
        log.setDate(new Date());
        log.setAuthor(userEntity);
		return log;
	}
	
	private boolean hasOutboundDependencies(final ResourceEntity resourceEntity) {
    	//TODO: DTM-710: add a check that resource has no outbound dependencies
		return false;
	}
	
}

package com.itextpdf.dito.manager.service.datacollection.impl;

import com.itextpdf.dito.manager.component.validator.json.JsonValidator;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionType;
import com.itextpdf.dito.manager.entity.PermissionEntity;
import com.itextpdf.dito.manager.entity.RoleEntity;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionLogEntity;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.exception.date.InvalidDateRangeException;
import com.itextpdf.dito.manager.exception.datacollection.DataCollectionAlreadyExistsException;
import com.itextpdf.dito.manager.exception.datacollection.DataCollectionNotFoundException;
import com.itextpdf.dito.manager.exception.datacollection.InvalidDataCollectionException;
import com.itextpdf.dito.manager.exception.role.RoleNotFoundException;
import com.itextpdf.dito.manager.filter.datacollection.DataCollectionFilter;
import com.itextpdf.dito.manager.filter.role.RoleFilter;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionLogRepository;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionRepository;
import com.itextpdf.dito.manager.service.AbstractService;
import com.itextpdf.dito.manager.service.datacollection.DataCollectionService;
import com.itextpdf.dito.manager.service.permission.PermissionService;
import com.itextpdf.dito.manager.service.role.RoleService;
import com.itextpdf.dito.manager.service.template.TemplateService;
import com.itextpdf.dito.manager.service.user.UserService;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import static com.itextpdf.dito.manager.filter.FilterUtils.getEndDateFromRange;
import static com.itextpdf.dito.manager.filter.FilterUtils.getStartDateFromRange;
import static com.itextpdf.dito.manager.filter.FilterUtils.getStringFromFilter;

@Service
public class DataCollectionServiceImpl extends AbstractService implements DataCollectionService {

    private final DataCollectionRepository dataCollectionRepository;
    private final DataCollectionLogRepository dataCollectionLogRepository;
    private final UserService userService;
    private final TemplateService templateService;
    private final JsonValidator jsonValidator;
    private final RoleService roleService;
    private final PermissionService permissionService;

    public DataCollectionServiceImpl(final DataCollectionRepository dataCollectionRepository,
                                     final UserService userService,
                                     final TemplateService templateService,
                                     final DataCollectionLogRepository dataCollectionLogRepository,
                                     final JsonValidator jsonValidator,
                                     final RoleService roleService,
                                     final PermissionService permissionService) {
        this.dataCollectionRepository = dataCollectionRepository;
        this.userService = userService;
        this.templateService = templateService;
        this.dataCollectionLogRepository = dataCollectionLogRepository;
        this.jsonValidator = jsonValidator;
        this.roleService = roleService;
        this.permissionService = permissionService;
    }

    @Override
    public DataCollectionEntity create(final String name, final DataCollectionType type, final byte[] data,
                                       final String fileName,
                                       final String email) {
        if (dataCollectionRepository.existsByName(name)) {
            throw new DataCollectionAlreadyExistsException(name);
        }

        if (!jsonValidator.isValid(data)) {
            throw new InvalidDataCollectionException();
        }

        final DataCollectionEntity dataCollectionEntity = new DataCollectionEntity();
        dataCollectionEntity.setName(name);
        dataCollectionEntity.setType(type);
        dataCollectionEntity.setData(data);
        dataCollectionEntity.setModifiedOn(new Date());
        dataCollectionEntity.setCreatedOn(new Date());
        dataCollectionEntity.setFileName(fileName);
        final UserEntity userEntity = userService.findByEmail(email);
        dataCollectionEntity.setAuthor(userEntity);
        DataCollectionEntity savedCollection = dataCollectionRepository.save(dataCollectionEntity);
        logDataCollectionUpdate(savedCollection, userEntity);

        return savedCollection;
    }

    @Override
    public Page<DataCollectionEntity> list(final Pageable pageable, final DataCollectionFilter dataCollectionFilter,
                                           final String searchParam) {
        throwExceptionIfSortedFieldIsNotSupported(pageable.getSort());

        final Pageable pageWithSort = updateSort(pageable);
        final String name = getStringFromFilter(dataCollectionFilter.getName());
        final String modifiedBy = getStringFromFilter(dataCollectionFilter.getModifiedBy());

        Date modifiedOnStartDate = null;
        Date modifiedOnEndDate = null;
        final List<String> modifiedOnDateRange = dataCollectionFilter.getModifiedOn();
        if (modifiedOnDateRange != null) {
            if (modifiedOnDateRange.size() != 2) {
                throw new InvalidDateRangeException();
            }
            modifiedOnStartDate = getStartDateFromRange(modifiedOnDateRange);
            modifiedOnEndDate = getEndDateFromRange(modifiedOnDateRange);
        }

        final List<DataCollectionType> types = dataCollectionFilter.getType();

        return StringUtils.isEmpty(searchParam)
                ? dataCollectionRepository.filter(pageWithSort, name, modifiedBy, modifiedOnStartDate, modifiedOnEndDate, types)
                : dataCollectionRepository.search(pageWithSort, name, modifiedBy, modifiedOnStartDate, modifiedOnEndDate, types, searchParam.toLowerCase());
    }

    @Override
    public DataCollectionEntity get(final String name) {
        return findByName(name);
    }

    @Override
    public DataCollectionEntity getByTemplateName(final String templateName) {
        return templateService.get(templateName).getDataCollection();
    }

    @Override
    public void delete(final String name) {
        dataCollectionRepository.delete(findByName(name));
    }

    @Override
    @Transactional
    public DataCollectionEntity update(final String name,
                                       final DataCollectionEntity updatedEntity,
                                       final String userEmail) {
        final DataCollectionEntity existingEntity = findByName(name);
        existingEntity.setType(updatedEntity.getType());
        if (updatedEntity.getData() != null) {
            existingEntity.setData(updatedEntity.getData());
        }
        final String newName = updatedEntity.getName();
        if (!name.equals(newName) && dataCollectionRepository.existsByName(newName)) {
            throw new DataCollectionAlreadyExistsException(newName);
        }
        existingEntity.setName(newName);
        existingEntity.setModifiedOn(new Date());
        existingEntity.setDescription(updatedEntity.getDescription());
        final DataCollectionEntity savedCollection = dataCollectionRepository.save(existingEntity);
        logDataCollectionUpdate(savedCollection, userService.findByEmail(userEmail));
        return savedCollection;
    }

    @Override
    public Page<RoleEntity> getRoles(final Pageable pageable, final String name, final RoleFilter filter) {
        final DataCollectionEntity dataCollectionEntity = findByName(name);
        return roleService.getSlaveRolesByDataCollection(pageable, filter, dataCollectionEntity);
    }

    @Override
    public DataCollectionEntity applyRole(final String dataCollectionName, final String roleName, final List<String> permissions) {
        final DataCollectionEntity dataCollectionEntity = findByName(dataCollectionName);

        RoleEntity slaveRoleEntity = roleService.getSlaveRole(roleName, dataCollectionEntity);
        if (slaveRoleEntity == null) {
            // line below will throw not found exception in case if user tries to create slave role which doesn't have master role.
            final RoleEntity masterRoleEntity = roleService.getMasterRole(roleName);

            slaveRoleEntity = new RoleEntity();
            slaveRoleEntity.setName(masterRoleEntity.getName());
            slaveRoleEntity.setType(masterRoleEntity.getType());
            slaveRoleEntity.setMaster(Boolean.FALSE);
        } else {
            slaveRoleEntity.getPermissions().clear();
            dataCollectionEntity.getAppliedRoles().remove(slaveRoleEntity);
        }

        for (final String permission : permissions) {
            final PermissionEntity permissionEntity = permissionService.get(permission);
            slaveRoleEntity.getPermissions().add(permissionEntity);
        }
        slaveRoleEntity.getDataCollections().add(dataCollectionEntity);

        dataCollectionEntity.getAppliedRoles().add(slaveRoleEntity);
        return dataCollectionRepository.save(dataCollectionEntity);

    }

    @Override
    public DataCollectionEntity detachRole(final String name, final String roleName) {
        final DataCollectionEntity dataCollectionEntity = findByName(name);
        final RoleEntity roleEntity = roleService.getSlaveRole(roleName, dataCollectionEntity);

        if (roleEntity == null) {
            throw new RoleNotFoundException(roleName);
        }

        dataCollectionEntity.getAppliedRoles().remove(roleEntity);
        roleService.delete(roleEntity);
        return dataCollectionRepository.save(dataCollectionEntity);

    }

    private DataCollectionEntity findByName(final String name) {
        return dataCollectionRepository.findByName(name).orElseThrow(() -> new DataCollectionNotFoundException(name));
    }

    private void logDataCollectionUpdate(final DataCollectionEntity collectionEntity, final UserEntity userEntity) {
        final DataCollectionLogEntity logDataCollection = new DataCollectionLogEntity();
        logDataCollection.setAuthor(userEntity);
        logDataCollection.setDataCollection(collectionEntity);
        logDataCollection.setDate(new Date());
        collectionEntity.setLastDataCollectionLog(logDataCollection);
        dataCollectionLogRepository.save(logDataCollection);
    }

    private Pageable updateSort(final Pageable pageable) {
        Sort newSort = Sort.by(pageable.getSort().stream()
                .map(sortParam -> {
                    if (sortParam.getProperty().equals("modifiedBy")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "author.firstName");
                    }
                    return sortParam;
                })
                .collect(Collectors.toList()));
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), newSort);
    }

    @Override
    protected List<String> getSupportedSortFields() {
        return DataCollectionRepository.SUPPORTED_SORT_FIELDS;
    }
}

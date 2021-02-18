package com.itextpdf.dito.manager.service.datacollection.impl;

import com.itextpdf.dito.manager.component.validator.json.JsonValidator;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionType;
import com.itextpdf.dito.manager.entity.PermissionEntity;
import com.itextpdf.dito.manager.entity.RoleEntity;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionFileEntity;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionLogEntity;
import com.itextpdf.dito.manager.entity.datasample.DataSampleEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;
import com.itextpdf.dito.manager.exception.datacollection.DataCollectionAlreadyExistsException;
import com.itextpdf.dito.manager.exception.datacollection.DataCollectionHasDependenciesException;
import com.itextpdf.dito.manager.exception.datacollection.DataCollectionNotFoundException;
import com.itextpdf.dito.manager.exception.datacollection.DataCollectionVersionNotFoundException;
import com.itextpdf.dito.manager.exception.datacollection.InvalidDataCollectionException;
import com.itextpdf.dito.manager.exception.date.InvalidDateRangeException;
import com.itextpdf.dito.manager.exception.role.RoleNotFoundException;
import com.itextpdf.dito.manager.filter.datacollection.DataCollectionFilter;
import com.itextpdf.dito.manager.filter.datacollection.DataCollectionPermissionFilter;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionFileRepository;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionLogRepository;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionRepository;
import com.itextpdf.dito.manager.repository.template.TemplateFileRepository;
import com.itextpdf.dito.manager.repository.template.TemplateRepository;
import com.itextpdf.dito.manager.service.AbstractService;
import com.itextpdf.dito.manager.service.datacollection.DataCollectionService;
import com.itextpdf.dito.manager.service.datasample.DataSampleService;
import com.itextpdf.dito.manager.service.permission.PermissionService;
import com.itextpdf.dito.manager.service.role.RoleService;
import com.itextpdf.dito.manager.service.template.TemplateService;
import com.itextpdf.dito.manager.service.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.itextpdf.dito.manager.filter.FilterUtils.getEndDateFromRange;
import static com.itextpdf.dito.manager.filter.FilterUtils.getStartDateFromRange;
import static com.itextpdf.dito.manager.filter.FilterUtils.getStringFromFilter;
import static java.util.Collections.singleton;

@Service
public class DataCollectionServiceImpl extends AbstractService implements DataCollectionService {
    private final DataCollectionRepository dataCollectionRepository;
    private final DataCollectionLogRepository dataCollectionLogRepository;
    private final DataCollectionFileRepository dataCollectionFileRepository;
    private final TemplateFileRepository templateFileRepository;
    private final TemplateRepository templateRepository;
    private final UserService userService;
    private final TemplateService templateService;
    private final JsonValidator jsonValidator;
    private final RoleService roleService;
    private final PermissionService permissionService;
    private final DataSampleService dataSampleService;

    public DataCollectionServiceImpl(final DataCollectionRepository dataCollectionRepository,
                                     final DataCollectionFileRepository dataCollectionFileRepository,
                                     final TemplateFileRepository templateFileRepository,
                                     final TemplateRepository templateRepository,
                                     final UserService userService,
                                     final TemplateService templateService,
                                     final DataCollectionLogRepository dataCollectionLogRepository,
                                     final JsonValidator jsonValidator,
                                     final RoleService roleService,
                                     final DataSampleService dataSampleService,
                                     final PermissionService permissionService) {
        this.dataCollectionRepository = dataCollectionRepository;
        this.dataCollectionFileRepository = dataCollectionFileRepository;
        this.templateRepository = templateRepository;
        this.templateFileRepository = templateFileRepository;
        this.userService = userService;
        this.templateService = templateService;
        this.dataCollectionLogRepository = dataCollectionLogRepository;
        this.jsonValidator = jsonValidator;
        this.roleService = roleService;
        this.permissionService = permissionService;
        this.dataSampleService = dataSampleService;
    }

    @Override
    public DataCollectionEntity create(final String name, final DataCollectionType type, final byte[] data, final String fileName, final String email) {
        if (dataCollectionRepository.existsByName(name)) {
            throw new DataCollectionAlreadyExistsException(name);
        }
        checkJsonIsValid(data);

        final UserEntity userEntity = userService.findActiveUserByEmail(email);

        final DataCollectionEntity dataCollectionEntity = new DataCollectionEntity();
        dataCollectionEntity.setName(name);
        dataCollectionEntity.setType(type);
        dataCollectionEntity.setModifiedOn(new Date());
        dataCollectionEntity.setCreatedOn(new Date());
        dataCollectionEntity.setAuthor(userEntity);

        final DataCollectionFileEntity dataCollectionFileEntity = new DataCollectionFileEntity();
        dataCollectionFileEntity.setData(data);
        dataCollectionFileEntity.setFileName(fileName);
        dataCollectionFileEntity.setAuthor(userEntity);
        dataCollectionFileEntity.setCreatedOn(new Date());
        dataCollectionFileEntity.setVersion(1L);
        dataCollectionFileEntity.setDataCollection(dataCollectionEntity);
        dataCollectionEntity.setVersions(singleton(dataCollectionFileEntity));
        dataCollectionEntity.setLatestVersion(dataCollectionFileEntity);

        DataCollectionEntity savedCollection = dataCollectionRepository.save(dataCollectionEntity);
        logDataCollectionUpdate(savedCollection, userEntity);

        return savedCollection;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public DataCollectionEntity createNewVersion(final String name, final DataCollectionType type, final byte[] data,
                                                 final String fileName, final String email, final String comment) {
        checkJsonIsValid(data);
        final DataCollectionEntity existingDataCollectionEntity = findByName(name);
        final UserEntity userEntity = userService.findActiveUserByEmail(email);

        final Long oldVersion = dataCollectionFileRepository.findFirstByDataCollection_IdOrderByVersionDesc(existingDataCollectionEntity.getId()).getVersion();
        final DataCollectionLogEntity logEntity = createDataCollectionLogEntry(existingDataCollectionEntity, userEntity);
        final DataCollectionFileEntity fileEntity = new DataCollectionFileEntity();
        fileEntity.setDataCollection(existingDataCollectionEntity);
        fileEntity.setVersion(oldVersion + 1);
        fileEntity.setData(data);
        fileEntity.setFileName(fileName);
        fileEntity.setCreatedOn(new Date());
        fileEntity.setAuthor(userEntity);
        fileEntity.setComment(comment);

        existingDataCollectionEntity.setModifiedOn(new Date());
        existingDataCollectionEntity.getVersions().add(fileEntity);
        existingDataCollectionEntity.setLatestVersion(fileEntity);
        existingDataCollectionEntity.getDataCollectionLog().add(logEntity);

        final DataCollectionEntity dataCollectionEntity = dataCollectionRepository.save(existingDataCollectionEntity);
        final List<TemplateFileEntity> templateEntities = templateRepository.findTemplatesFilesByDataCollectionId(existingDataCollectionEntity.getId());
        if (Objects.nonNull(templateEntities)) {
            final List<TemplateFileEntity> newFiles = new LinkedList<>();
            final DataCollectionFileEntity dataCollectionEntityLatestVersion = dataCollectionEntity.getLatestVersion();
            final String updatedDependenciesComment = new StringBuilder(dataCollectionEntity.getName()).append(" was updated to version ").append(dataCollectionEntityLatestVersion.getVersion().toString()).toString();
            templateEntities.forEach(t -> {
                final TemplateEntity extendedTemplateEntity = templateService.createNewVersionAsCopy(t, userEntity, updatedDependenciesComment);
                extendedTemplateEntity.getLatestFile().setDataCollectionFile(dataCollectionEntityLatestVersion);
                newFiles.add(extendedTemplateEntity.getLatestFile());
            });
            templateFileRepository.saveAll(newFiles);
        }
        return dataCollectionEntity;
    }

    private void checkJsonIsValid(final byte[] data) {
        if (!jsonValidator.isValid(data)) {
            throw new InvalidDataCollectionException();
        }
    }

    @Override
    public Page<DataCollectionEntity> list(final Pageable pageable, final DataCollectionFilter dataCollectionFilter, final String searchParam) {
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
    public List<DataCollectionEntity> list(final DataCollectionFilter dataCollectionFilter, final String searchParam) {
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
                ? dataCollectionRepository.filter(name, modifiedBy, modifiedOnStartDate, modifiedOnEndDate, types)
                : dataCollectionRepository.search(name, modifiedBy, modifiedOnStartDate, modifiedOnEndDate, types, searchParam.toLowerCase());
    }

    @Override
    public DataCollectionEntity get(final String name) {
        return findByName(name);
    }

    @Override
    public DataCollectionEntity getByTemplateName(final String templateName) {
        final TemplateEntity existingTemplate = templateService.get(templateName);
        return existingTemplate.getLatestFile().getDataCollectionFile().getDataCollection();
    }

    @Override
    public void delete(final String name, final String userEmail) {
        final DataCollectionEntity deletingDataCollection = findByName(name);

        if (hasOutboundDependencies(deletingDataCollection.getId())) {
            throw new DataCollectionHasDependenciesException();
        }

        dataCollectionRepository.delete(findByName(name));
    }

    @Override
    @Transactional
    public DataCollectionEntity update(final String name,
                                       final DataCollectionEntity updatedEntity,
                                       final String userEmail) {
        final DataCollectionEntity existingEntity = findByName(name);
        final UserEntity currentUser = userService.findActiveUserByEmail(userEmail);

        existingEntity.setType(updatedEntity.getType());
        final String newName = updatedEntity.getName();
        if (!name.equals(newName) && dataCollectionRepository.existsByName(newName)) {
            throw new DataCollectionAlreadyExistsException(newName);
        }
        existingEntity.setName(newName);
        existingEntity.setModifiedOn(new Date());
        existingEntity.setDescription(updatedEntity.getDescription());
        final DataCollectionEntity savedCollection = dataCollectionRepository.save(existingEntity);
        logDataCollectionUpdate(savedCollection, currentUser);
        return savedCollection;
    }

    @Override
    public Page<RoleEntity> getRoles(final Pageable pageable, final String name, final DataCollectionPermissionFilter filter) {
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
            checkNotAdminRole(masterRoleEntity);

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

    private DataCollectionLogEntity createDataCollectionLogEntry(final DataCollectionEntity collectionEntity, final UserEntity userEntity) {
        final DataCollectionLogEntity logDataCollection = new DataCollectionLogEntity();
        logDataCollection.setAuthor(userEntity);
        logDataCollection.setDataCollection(collectionEntity);
        logDataCollection.setDate(new Date());
        collectionEntity.setLastDataCollectionLog(logDataCollection);
        return logDataCollection;
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
                        sortParam = new Sort.Order(sortParam.getDirection(), "lastLog.author.firstName");
                    }
                    if (sortParam.getProperty().equals("modifiedOn")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "lastLog.date");
                    }
                    return sortParam.getProperty().equals("lastLog.date") ? sortParam : sortParam.ignoreCase();
                })
                .collect(Collectors.toList()));
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), newSort);
    }

    private boolean hasOutboundDependencies(final Long dataCollectionId) {
        return !dataCollectionFileRepository.searchDependencyOfDataCollection(dataCollectionId).isEmpty();
    }

    @Override
    protected List<String> getSupportedSortFields() {
        return DataCollectionRepository.SUPPORTED_SORT_FIELDS;
    }

    @Override
    public DataSampleEntity create(final String dataCollectionName, final String name, final String fileName, final String sample, final String comment, final String email) {
        final DataCollectionEntity dataCollectionEntity = get(dataCollectionName);
        return dataSampleService.create(dataCollectionEntity, name, fileName, sample, comment, email);
    }

    @Override
    public DataCollectionEntity rollbackVersion(final String name, final Long version, final String email) {

        final DataCollectionEntity existingDataCollectionEntity = get(name);
        final DataCollectionFileEntity versionForRollback = dataCollectionFileRepository.findByVersionAndDataCollection(version, existingDataCollectionEntity)
                .orElseThrow(() -> new DataCollectionVersionNotFoundException(String.valueOf(version)));
        final String comment = new StringBuilder().append("Rollback to version: ").append(versionForRollback.getVersion()).toString();

        final UserEntity userEntity = userService.findActiveUserByEmail(email);

        final Long latestVersion = dataCollectionFileRepository.findFirstByDataCollection_IdOrderByVersionDesc(existingDataCollectionEntity.getId()).getVersion();
        final DataCollectionLogEntity logEntity = createDataCollectionLogEntry(existingDataCollectionEntity, userEntity);
        final DataCollectionFileEntity newVersion = new DataCollectionFileEntity();
        newVersion.setDataCollection(existingDataCollectionEntity);
        newVersion.setVersion(latestVersion + 1);
        newVersion.setData(versionForRollback.getData());
        newVersion.setFileName(versionForRollback.getFileName());
        newVersion.setCreatedOn(new Date());
        newVersion.setAuthor(userEntity);
        newVersion.setComment(comment);

        existingDataCollectionEntity.setModifiedOn(new Date());
        existingDataCollectionEntity.getVersions().add(newVersion);
        existingDataCollectionEntity.setLatestVersion(newVersion);
        existingDataCollectionEntity.getDataCollectionLog().add(logEntity);

        final DataCollectionEntity dataCollectionEntity = dataCollectionRepository.save(existingDataCollectionEntity);
        final List<TemplateFileEntity> templateEntities = templateRepository.findTemplatesFilesByDataCollectionId(existingDataCollectionEntity.getId());
        if (Objects.nonNull(templateEntities)) {
            final List<TemplateFileEntity> newFiles = new LinkedList<>();
            templateEntities.forEach(t -> {
                final TemplateEntity extendedTemplateEntity = templateService.createNewVersionAsCopy(t, userEntity, "");
                extendedTemplateEntity.getLatestFile().setDataCollectionFile(dataCollectionEntity.getLatestVersion());
                newFiles.add(extendedTemplateEntity.getLatestFile());
            });
            templateFileRepository.saveAll(newFiles);
        }
        return dataCollectionEntity;
    }
}

package com.itextpdf.dito.manager.service.datasample.impl;

import com.itextpdf.dito.manager.component.datasample.jsoncomparator.JsonKeyComparator;
import com.itextpdf.dito.manager.component.validator.json.JsonValidator;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionFileEntity;
import com.itextpdf.dito.manager.entity.datasample.DataSampleEntity;
import com.itextpdf.dito.manager.entity.datasample.DataSampleFileEntity;
import com.itextpdf.dito.manager.entity.datasample.DataSampleLogEntity;
import com.itextpdf.dito.manager.exception.AliasConstants;
import com.itextpdf.dito.manager.exception.datacollection.DataCollectionNotFoundException;
import com.itextpdf.dito.manager.exception.datasample.DataSampleAlreadyExistsException;
import com.itextpdf.dito.manager.exception.datasample.DataSampleNotFoundException;
import com.itextpdf.dito.manager.exception.datasample.DataSampleUuidNotFoundException;
import com.itextpdf.dito.manager.exception.datasample.InvalidDataSampleException;
import com.itextpdf.dito.manager.exception.datasample.InvalidDataSampleStructureException;
import com.itextpdf.dito.manager.exception.date.InvalidDateRangeException;
import com.itextpdf.dito.manager.filter.datasample.DataSampleFilter;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionRepository;
import com.itextpdf.dito.manager.repository.datasample.DataSampleLogRepository;
import com.itextpdf.dito.manager.repository.datasample.DataSampleRepository;
import com.itextpdf.dito.manager.service.AbstractService;
import com.itextpdf.dito.manager.service.datasample.DataSampleService;
import com.itextpdf.dito.manager.service.user.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.itextpdf.dito.manager.filter.FilterUtils.getBooleanMultiselectFromFilter;
import static com.itextpdf.dito.manager.filter.FilterUtils.getEndDateFromRange;
import static com.itextpdf.dito.manager.filter.FilterUtils.getStartDateFromRange;
import static com.itextpdf.dito.manager.filter.FilterUtils.getStringFromFilter;
import static java.util.Collections.singleton;

@Service
public class DataSampleServiceImpl extends AbstractService implements DataSampleService {
    private static final Logger log = LogManager.getLogger(DataSampleServiceImpl.class);
    private static final String IS_DEFAULT = "isDefault";

    private final DataSampleRepository dataSampleRepository;
    private final DataCollectionRepository dataCollectionRepository;
    private final UserService userService;
    private final JsonValidator jsonValidator;
    private final JsonKeyComparator jsonKeyComparator;
    private final DataSampleLogRepository dataSampleLogRepository;

    public DataSampleServiceImpl(final DataSampleRepository dataSampleRepository,
                                 final DataSampleLogRepository dataSampleLogRepository,
                                 final UserService userService,
                                 final DataCollectionRepository dataCollectionRepository,
                                 final JsonValidator jsonValidator,
                                 final JsonKeyComparator jsonKeyComparator) {
        this.dataSampleRepository = dataSampleRepository;
        this.dataCollectionRepository = dataCollectionRepository;
        this.userService = userService;
        this.jsonValidator = jsonValidator;
        this.jsonKeyComparator = jsonKeyComparator;
        this.dataSampleLogRepository = dataSampleLogRepository;
    }

    @Override
    public DataSampleEntity create(final DataCollectionEntity dataCollectionEntity, final String name, final String fileName,
                                   final String sample, final String comment, final String email) {
        log.info("Create dataSample by dataCollectionEntity: {} and dataSampleName: {} and fileName: {} and json: {} and comment: {} and email: {} was started",
                dataCollectionEntity, name, fileName, sample, comment, email);
        throwExceptionIfNameNotMatchesPattern(name, AliasConstants.DATA_SAMPLE);
        if (Boolean.TRUE.equals(dataSampleRepository.existsByNameAndDataCollection(name, dataCollectionEntity))) {
            throw new DataSampleAlreadyExistsException(name);
        }

        if (!jsonValidator.isValid(sample.getBytes())) {
            throw new InvalidDataSampleException();
        }

        final DataCollectionFileEntity lastEntity = dataCollectionEntity.getLatestVersion();
        final String jsonFromCollection = new String(lastEntity.getData(), StandardCharsets.UTF_8);
        if (!jsonKeyComparator.checkJsonKeysEquals(jsonFromCollection, sample)) {
            throw new InvalidDataSampleStructureException();
        }

        final UserEntity userEntity = userService.findActiveUserByEmail(email);

        final DataSampleEntity dataSampleEntity = new DataSampleEntity();
        dataSampleEntity.setDataCollection(dataCollectionEntity);
        dataSampleEntity.setName(name);
        dataSampleEntity.setModifiedOn(new Date());
        dataSampleEntity.setCreatedOn(new Date());
        dataSampleEntity.setAuthor(userEntity);
        dataSampleEntity.setIsDefault(!dataSampleRepository.existsByDataCollection(dataCollectionEntity));

        final DataSampleFileEntity dataSampleFileEntity = new DataSampleFileEntity();
        dataSampleFileEntity.setData(sample.getBytes());
        dataSampleFileEntity.setFileName(fileName);
        dataSampleFileEntity.setAuthor(userEntity);
        dataSampleFileEntity.setCreatedOn(new Date());
        dataSampleFileEntity.setComment(comment);
        dataSampleFileEntity.setVersion(1L);
        dataSampleFileEntity.setDataSample(dataSampleEntity);
        dataSampleEntity.setVersions(singleton(dataSampleFileEntity));
        dataSampleEntity.setLatestVersion(dataSampleFileEntity);
        final DataSampleLogEntity logEntity = createDataSampleLogEntry(dataSampleEntity, userEntity);
        dataSampleEntity.setDataSampleLog(Collections.singletonList(logEntity));

        log.info("Create dataSample by dataCollectionEntity: {} and dataSampleName: {} and fileName: {} and json: {} and comment: {} and email: {} was finished successfully",
                dataCollectionEntity, name, fileName, sample, comment, email);
        return dataSampleRepository.save(dataSampleEntity);
    }

    @Override
    protected List<String> getSupportedSortFields() {
        return DataSampleRepository.SUPPORTED_SORT_FIELDS;
    }

    @Override
    public Page<DataSampleEntity> list(final Pageable pageable, final Long dataCollectionId, final DataSampleFilter filter, final String searchParam) {
        log.info("Get list dataSamples by dataCollectionId: {} and filter: {} and searchParam: {} was started", dataCollectionId, filter, searchParam);
        throwExceptionIfSortedFieldIsNotSupported(pageable.getSort());

        final Pageable pageWithSort = updateSort(pageable);
        final String name = getStringFromFilter(filter.getName());
        final String modifiedBy = getStringFromFilter(filter.getModifiedBy());
        final String comment = getStringFromFilter(filter.getComment());
        final Boolean isDefault = getBooleanMultiselectFromFilter(filter.getIsDefault());

        Date editedOnStartDate = null;
        Date editedOnEndDate = null;
        final List<String> editedOnDateRange = filter.getModifiedOn();
        if (editedOnDateRange != null) {
            if (editedOnDateRange.size() != 2) {
                throw new InvalidDateRangeException();
            }
            editedOnStartDate = getStartDateFromRange(editedOnDateRange);
            editedOnEndDate = getEndDateFromRange(editedOnDateRange);
        }

        final Page<DataSampleEntity> dataSampleEntities = StringUtils.isEmpty(searchParam)
                ? dataSampleRepository
                .filter(pageWithSort, dataCollectionId, name, modifiedBy, editedOnStartDate, editedOnEndDate, isDefault, comment)
                : dataSampleRepository
                .search(pageWithSort, dataCollectionId, name, modifiedBy, editedOnStartDate, editedOnEndDate, comment, isDefault, searchParam.toLowerCase());
        log.info("Get list dataSamples by dataCollectionId: {} and filter: {} and searchParam: {} was finished successfully", dataCollectionId, filter, searchParam);
        return dataSampleEntities;
    }

    @Override
    public DataSampleEntity get(final String dataCollectionName, final String dataSampleName) {
        final DataCollectionEntity dataCollectionEntity = dataCollectionRepository.findByName(dataCollectionName).orElseThrow(() -> new DataCollectionNotFoundException(dataCollectionName));
        return dataSampleRepository.findByNameAndDataCollection(dataSampleName, dataCollectionEntity).orElseThrow(() -> new DataSampleNotFoundException(dataSampleName));
    }

    @Override
    public DataSampleEntity getByUuid(final String uuid) {
        return dataSampleRepository.findByUuid(uuid).orElseThrow(() -> new DataSampleUuidNotFoundException(uuid));
    }

    @Override
    public DataSampleEntity getByNameAndTemplateName(final String dataSampleName, final String templateName) {
        return dataSampleRepository.findDataSamplesByTemplateNameAndSampleName(templateName, dataSampleName).orElseThrow(() -> new DataSampleNotFoundException(dataSampleName));
    }

    @Override
    public List<DataSampleEntity> list(final Long dataCollectionId) {
        return dataSampleRepository.findDataSampleEntitiesByDataCollectionId(dataCollectionId);
    }

    private Pageable updateSort(final Pageable pageable) {
        Sort newSort = Sort.by(pageable.getSort().stream()
                .map(sortParam -> {
                    if ("modifiedBy".equals(sortParam.getProperty())) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "lastLog.author.firstName");
                    }
                    if ("comment".equals(sortParam.getProperty())) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "latestFile.comment");
                    }
                    if (IS_DEFAULT.equals(sortParam.getProperty())) {
                        sortParam = new Sort.Order(sortParam.getDirection() == Sort.Direction.ASC ? Sort.Direction.DESC : Sort.Direction.ASC, IS_DEFAULT);
                    }
                    return "modifiedOn".equals(sortParam.getProperty()) || IS_DEFAULT.equals(sortParam.getProperty()) ? sortParam : sortParam.ignoreCase();
                })
                .collect(Collectors.toList()));
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), newSort);
    }

    @Override
    public DataSampleEntity setAsDefault(String dataCollectionName, final String dataSampleName) {
        log.info("Set as default dataSample with name: {} was started", dataSampleName);
        final DataSampleEntity dataSampleEntity = get(dataCollectionName, dataSampleName);
        final DataCollectionEntity dataCollectionEntity = dataSampleEntity.getDataCollection();
        final List<DataSampleEntity> list = dataSampleRepository.findByDataCollection(dataCollectionEntity)
                .orElseThrow(() -> new DataCollectionNotFoundException(dataCollectionEntity.getName()));
        list.forEach(e -> {
            e.setIsDefault(false);
            e.setModifiedOn(new Date());
        });
        dataSampleEntity.setIsDefault(true);
        dataSampleRepository.saveAll(list);
        log.info("Set as default dataSample with name: {} was finished successfully", dataSampleName);
        return dataSampleEntity;
    }

    @Override
    public List<DataSampleEntity> delete(final String dataCollectionName, final List<String> dataSamplesList) {
        log.info("Delete list dataSample: {} was started", dataSamplesList);
        final List<DataSampleEntity> dataSampleListToDelete = dataSamplesList.stream().map(dataSampleName -> get(dataCollectionName, dataSampleName)).collect(Collectors.toList());
        dataSampleRepository.deleteAll(dataSampleListToDelete);
        log.info("Delete list dataSample: {} was finished successfully", dataSamplesList);
        return dataSampleListToDelete;
    }

    @Override
    public DataSampleEntity deleteByUuid(final String uuid) {
        log.info("Delete data sample by uuid: {} was started", uuid);
        final DataSampleEntity dataSampleToDelete = getByUuid(uuid);
        dataSampleRepository.delete(dataSampleToDelete);
        log.info("Delete data sample by uuid: {} was finished successfully", uuid);
        return dataSampleToDelete;
    }

    @Override
    public void delete(final DataCollectionEntity dataCollectionEntity) {
        log.info("Delete dataSamples by dataCollection: {} was started", dataCollectionEntity);
        final List<DataSampleEntity> dataSampleListToDelete = dataSampleRepository
                .findByDataCollection(dataCollectionEntity)
                .orElseThrow(() -> new DataCollectionNotFoundException(dataCollectionEntity.getName()));
        dataSampleRepository.deleteAll(dataSampleListToDelete);
        log.info("Delete dataSamples by dataCollection: {} was finished successfully", dataCollectionEntity);
    }

    @Override
    public DataSampleEntity createNewVersion(final String dataCollectionName, final String name, final String sample, final String fileName, final String email, final String comment) {
        log.info("Create new version with data collection: {}, name: {}, sample: {} and fileName: {}, email: {}, comment: {} was started",
                dataCollectionName, name, sample, fileName, email, comment);
        if (!jsonValidator.isValid(sample.getBytes())) {
            throw new InvalidDataSampleException();
        }
        final DataSampleEntity existingDataSampleEntity = get(dataCollectionName, name);
        final DataCollectionEntity dataCollectionEntity = existingDataSampleEntity.getDataCollection();
        final DataSampleFileEntity lastEntity = existingDataSampleEntity.getLatestVersion();
        final String jsonFromCollection = new String(dataCollectionEntity.getLatestVersion().getData(), StandardCharsets.UTF_8);
        if (!jsonKeyComparator.checkJsonKeysEquals(jsonFromCollection, sample)) {
            throw new InvalidDataSampleStructureException();
        }

        final UserEntity userEntity = userService.findActiveUserByEmail(email);

        final DataSampleLogEntity logEntity = createDataSampleLogEntry(existingDataSampleEntity, userEntity);
        final DataSampleFileEntity fileEntity = new DataSampleFileEntity();
        fileEntity.setDataSample(existingDataSampleEntity);
        fileEntity.setVersion(lastEntity.getVersion() + 1);
        fileEntity.setData(sample.getBytes());
        fileEntity.setFileName(fileName == null ? lastEntity.getFileName() : fileName);
        fileEntity.setCreatedOn(new Date());
        fileEntity.setAuthor(userEntity);
        fileEntity.setComment(comment);

        existingDataSampleEntity.setModifiedOn(new Date());
        existingDataSampleEntity.getVersions().add(fileEntity);
        existingDataSampleEntity.setLatestVersion(fileEntity);
        existingDataSampleEntity.getDataSampleLog().add(logEntity);

        final DataSampleEntity savedDataSampleEntity = dataSampleRepository.save(existingDataSampleEntity);
        log.info("Create new version with data collection: {}, name: {}, sample: {} and fileName: {}, email: {}, comment: {} was finished successfully",
                dataCollectionName, name, sample, fileName, email, comment);
        return savedDataSampleEntity;
    }

    private DataSampleLogEntity createDataSampleLogEntry(final DataSampleEntity dataSampleEntity, final UserEntity userEntity) {
        final DataSampleLogEntity logDataSample = new DataSampleLogEntity();
        logDataSample.setAuthor(userEntity);
        logDataSample.setDataSample(dataSampleEntity);
        logDataSample.setDate(new Date());
        dataSampleEntity.setLastDataSampleLog(logDataSample);
        return logDataSample;
    }

    @Override
    @Transactional
    public DataSampleEntity update(final String dataCollectionName, final String name, final DataSampleEntity updatedEntity, final String userEmail) {
        final String newName = updatedEntity.getName();
        throwExceptionIfNameNotMatchesPattern(newName, AliasConstants.DATA_SAMPLE);
        final DataSampleEntity existingEntity = get(dataCollectionName, name);
        final UserEntity currentUser = userService.findActiveUserByEmail(userEmail);

        if (!name.equals(newName) && Boolean.TRUE.equals(dataSampleRepository.existsByNameAndDataCollection(newName, existingEntity.getDataCollection()))) {
            throw new DataSampleAlreadyExistsException(newName);
        }
        existingEntity.setName(newName);
        existingEntity.setModifiedOn(new Date());
        existingEntity.setDescription(updatedEntity.getDescription());
        final DataSampleEntity savedCollection = dataSampleRepository.save(existingEntity);
        logDataSampleUpdate(savedCollection, currentUser);
        return savedCollection;
    }

    private void logDataSampleUpdate(final DataSampleEntity sampleEntity, final UserEntity userEntity) {
        final DataSampleLogEntity logDataSample = new DataSampleLogEntity();
        logDataSample.setAuthor(userEntity);
        logDataSample.setDataSample(sampleEntity);
        logDataSample.setDate(new Date());
        sampleEntity.setLastDataSampleLog(logDataSample);
        dataSampleLogRepository.save(logDataSample);
    }

    @Override
    public Optional<DataSampleEntity> findDataSampleByTemplateId(final Long templateId) {
        return dataSampleRepository.findDataSampleByTemplateId(templateId);
    }

    @Override
    public Optional<DataSampleEntity> findDataSampleByCollectionId(Long dataCollectionId) {
        return dataSampleRepository.findDataSampleByCollectionId(dataCollectionId);
    }

    @Override
    public List<DataSampleEntity> getListByTemplateName(final String templateName) {
        return dataSampleRepository.findDataSamplesByTemplateName(templateName);
    }
}

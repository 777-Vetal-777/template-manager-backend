package com.itextpdf.dito.manager.service.datacollection.impl;

import com.itextpdf.dito.manager.component.validator.json.JsonValidator;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionType;
import com.itextpdf.dito.manager.entity.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.DataCollectionLogEntity;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.exception.datacollection.DataCollectionAlreadyExistsException;
import com.itextpdf.dito.manager.exception.datacollection.DataCollectionNotFoundException;
import com.itextpdf.dito.manager.exception.datacollection.InvalidDataCollectionException;
import com.itextpdf.dito.manager.filter.FilterUtils;
import com.itextpdf.dito.manager.filter.datacollection.DataCollectionFilter;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionLogRepository;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionRepository;
import com.itextpdf.dito.manager.service.AbstractService;
import com.itextpdf.dito.manager.service.datacollection.DataCollectionService;
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

import static com.itextpdf.dito.manager.filter.FilterUtils.getDateRangeFromFilter;
import static com.itextpdf.dito.manager.filter.FilterUtils.getStringFromFilter;

@Service
public class DataCollectionServiceImpl extends AbstractService implements DataCollectionService {

    private final DataCollectionRepository dataCollectionRepository;
    private final DataCollectionLogRepository dataCollectionLogRepository;
    private final UserService userService;
    private final JsonValidator jsonValidator;

    public DataCollectionServiceImpl(final DataCollectionRepository dataCollectionRepository,
                                     final UserService userService,
                                     final DataCollectionLogRepository dataCollectionLogRepository,
                                     final JsonValidator jsonValidator) {
        this.dataCollectionRepository = dataCollectionRepository;
        this.dataCollectionLogRepository = dataCollectionLogRepository;
        this.userService = userService;
        this.jsonValidator = jsonValidator;
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
        logDataCollectionUpdate(savedCollection);

        return savedCollection;
    }

    @Override
    public Page<DataCollectionEntity> list(final Pageable pageable, final DataCollectionFilter dataCollectionFilter,
                                           final String searchParam) {
        throwExceptionIfSortedFieldIsNotSupported(pageable.getSort());

        final Pageable pageWithSort = updateSort(pageable);
        final String name = getStringFromFilter(dataCollectionFilter.getName());
        final String modifiedBy = getStringFromFilter(dataCollectionFilter.getModifiedBy());
        final List<Date> dateRange = getDateRangeFromFilter(dataCollectionFilter.getModifiedOn());
        final List<DataCollectionType> types = dataCollectionFilter.getTypes();

        return StringUtils.isEmpty(searchParam)
                ? dataCollectionRepository.filter(pageWithSort, name, modifiedBy, dateRange, types)
                : dataCollectionRepository.search(pageWithSort, name, modifiedBy, dateRange, types, searchParam.toLowerCase());
    }

    @Override
    public DataCollectionEntity get(final String name) {
        return findByName(name);
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
        if (!name.equals(updatedEntity.getName()) && dataCollectionRepository.existsByName(updatedEntity.getName())) {
            throw new DataCollectionAlreadyExistsException(name);
        }
        existingEntity.setName(updatedEntity.getName());
        existingEntity.setModifiedOn(new Date());
        existingEntity.setAuthor(userService.findByEmail(userEmail));
        existingEntity.setDescription(updatedEntity.getDescription());
        final DataCollectionEntity savedCollection = dataCollectionRepository.save(existingEntity);
        logDataCollectionUpdate(savedCollection);
        return savedCollection;
    }

    private DataCollectionEntity findByName(final String name) {
        return dataCollectionRepository.findByName(name).orElseThrow(() -> new DataCollectionNotFoundException(name));
    }

    private void logDataCollectionUpdate(DataCollectionEntity collectionEntity) {
        final DataCollectionLogEntity logDataCollection = new DataCollectionLogEntity();
        logDataCollection.setAuthor(collectionEntity.getAuthor());
        logDataCollection.setDataCollection(collectionEntity);
        logDataCollection.setDate(new Date());
        dataCollectionLogRepository.save(logDataCollection);
    }

    private Pageable updateSort(Pageable pageable) {
        Sort newSort = Sort.by(pageable.getSort().stream()
                .map(sortParam -> {
                    if (sortParam.getProperty().equals("template")) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "template.name");
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

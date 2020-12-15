package com.itextpdf.dito.manager.service.datacollection.impl;

import com.itextpdf.dito.manager.component.validator.json.JsonValidator;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionType;
import com.itextpdf.dito.manager.entity.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.DataCollectionLogEntity;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.exception.datacollection.DataCollectionAlreadyExistsException;
import com.itextpdf.dito.manager.exception.datacollection.DataCollectionNotFoundException;
import com.itextpdf.dito.manager.exception.datacollection.InvalidDataCollectionException;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionLogRepository;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionRepository;
import com.itextpdf.dito.manager.service.datacollection.DataCollectionService;
import com.itextpdf.dito.manager.service.user.UserService;

import java.util.Date;
import javax.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class DataCollectionServiceImpl implements DataCollectionService {

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
    public Page<DataCollectionEntity> list(final Pageable pageable, final String searchParam) {
        return StringUtils.isEmpty(searchParam)
                ? dataCollectionRepository.findAll(pageable)
                : dataCollectionRepository.search(pageable, searchParam);
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
}

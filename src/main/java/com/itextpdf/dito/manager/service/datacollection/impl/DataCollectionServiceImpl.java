package com.itextpdf.dito.manager.service.datacollection.impl;

import com.itextpdf.dito.manager.component.mapper.datacollection.DataCollectionMapper;
import com.itextpdf.dito.manager.entity.DataCollectionEntity;
import com.itextpdf.dito.manager.exception.CollectionAlreadyExistsException;
import com.itextpdf.dito.manager.exception.EntityNotFoundException;
import com.itextpdf.dito.manager.exception.FileCannotBeReadException;
import com.itextpdf.dito.manager.exception.FileTypeNotSupportedException;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionRepository;
import com.itextpdf.dito.manager.service.datacollection.DataCollectionService;
import com.itextpdf.dito.manager.service.user.UserService;
import liquibase.util.file.FilenameUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;

@Service
public class DataCollectionServiceImpl implements DataCollectionService {

    private final DataCollectionRepository dataCollectionRepository;
    private final DataCollectionMapper dataCollectionMapper;
    private final UserService userService;

    public DataCollectionServiceImpl(final DataCollectionRepository dataCollectionRepository,
                                     final DataCollectionMapper dataCollectionMapper,
                                     final UserService userService) {
        this.dataCollectionRepository = dataCollectionRepository;
        this.dataCollectionMapper = dataCollectionMapper;
        this.userService = userService;
    }

    @Override
    public DataCollectionEntity create(final DataCollectionEntity collectionEntity, final MultipartFile attachment, final String email) {
        if (dataCollectionRepository.existsByName(collectionEntity.getName())) {
            throw new CollectionAlreadyExistsException(collectionEntity.getName());
        }
        final String fileExtension = FilenameUtils.getExtension(attachment.getOriginalFilename()).toLowerCase();
        if (StringUtils.isEmpty(fileExtension) || !fileExtension.equals("json")) {
            throw new FileTypeNotSupportedException(fileExtension);
        }
        try {
            collectionEntity.setData(attachment.getBytes());
        } catch (IOException e) {
            throw new FileCannotBeReadException(attachment.getOriginalFilename());
        }
        collectionEntity.setModifiedOn(new Date());
        collectionEntity.setAuthor(userService.findByEmail(email));
        return dataCollectionRepository.save(collectionEntity);
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
    public DataCollectionEntity update(final String name,
                                       final DataCollectionEntity entity,
                                       final String userEmail) {
        final DataCollectionEntity existingEntity = findByName(name);
        existingEntity.setType(entity.getType());
        if (entity.getData() != null) {
            existingEntity.setData(entity.getData());
        }
        existingEntity.setTemplate(entity.getTemplate());
        existingEntity.setName(entity.getName());
        existingEntity.setAuthor(userService.findByEmail(userEmail));
        return dataCollectionRepository.save(existingEntity);
    }

    private DataCollectionEntity findByName(final String name) {
        return dataCollectionRepository.findByName(name).orElseThrow(() -> new EntityNotFoundException(new StringBuilder()
                .append("Data collection with name ")
                .append(name)
                .append(" not found")
                .toString()));
    }
}

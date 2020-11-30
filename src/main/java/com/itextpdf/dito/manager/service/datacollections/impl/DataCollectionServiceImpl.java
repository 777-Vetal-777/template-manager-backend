package com.itextpdf.dito.manager.service.datacollections.impl;

import com.itextpdf.dito.manager.component.mapper.datacollection.DataCollectionMapper;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionCreateRequestDTO;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionDTO;
import com.itextpdf.dito.manager.entity.DataCollectionEntity;
import com.itextpdf.dito.manager.exception.CollectionAlreadyExistsException;
import com.itextpdf.dito.manager.exception.FileCannotBeReadException;
import com.itextpdf.dito.manager.exception.FileTypeNotSupportedException;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionRepository;
import com.itextpdf.dito.manager.service.datacollections.DataCollectionService;
import com.itextpdf.dito.manager.service.user.UserService;
import liquibase.util.file.FilenameUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
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
    public DataCollectionDTO create(final DataCollectionCreateRequestDTO requestDTO, final MultipartFile attachment, final Principal principal) {
        if (dataCollectionRepository.existsByName(requestDTO.getName())) {
            throw new CollectionAlreadyExistsException(requestDTO.getName());
        }
        final DataCollectionEntity entity = dataCollectionMapper.map(requestDTO);
        final String fileExtension = FilenameUtils.getExtension(attachment.getOriginalFilename()).toLowerCase();
        if (org.apache.commons.lang3.StringUtils.isEmpty(fileExtension) || !fileExtension.equals("json")) {
            throw new FileTypeNotSupportedException(fileExtension);
        }
        try {
            entity.setData(attachment.getBytes());
        } catch (IOException e) {
            throw new FileCannotBeReadException(attachment.getOriginalFilename());
        }
        entity.setModifiedOn(new Date());
        entity.setAuthor(userService.findByEmail(principal.getName()));
        return dataCollectionMapper.map(dataCollectionRepository.save(entity));
    }

    @Override
    public Page<DataCollectionEntity> list(final Pageable pageable, final String searchParam) {
        return StringUtils.isEmpty(searchParam)
                ? dataCollectionRepository.findAll(pageable)
                : dataCollectionRepository.search(pageable, searchParam);
    }
}

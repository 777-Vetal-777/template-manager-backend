package com.itextpdf.dito.manager.controller.datacollection.impl;

import com.itextpdf.dito.manager.component.mapper.datacollection.DataCollectionMapper;
import com.itextpdf.dito.manager.controller.AbstractController;
import com.itextpdf.dito.manager.controller.datacollection.DataCollectionController;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionDTO;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionType;
import com.itextpdf.dito.manager.dto.datacollection.update.DataCollectionUpdateRequestDTO;
import com.itextpdf.dito.manager.entity.DataCollectionEntity;
import com.itextpdf.dito.manager.exception.datacollection.UnreadableDataCollectionException;
import com.itextpdf.dito.manager.service.datacollection.DataCollectionService;

import java.io.IOException;
import java.security.Principal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class DataCollectionControllerImpl extends AbstractController implements DataCollectionController {
    private final DataCollectionService dataCollectionService;
    private final DataCollectionMapper dataCollectionMapper;

    public DataCollectionControllerImpl(final DataCollectionService dataCollectionService,
            final DataCollectionMapper dataCollectionMapper) {
        this.dataCollectionService = dataCollectionService;
        this.dataCollectionMapper = dataCollectionMapper;
    }

    @Override
    public ResponseEntity<DataCollectionDTO> create(final String name,
            final DataCollectionType dataCollectionType,
            final MultipartFile multipartFile, final Principal principal) {
        byte[] data;

        try {
            data = multipartFile.getBytes();
        } catch (IOException e) {
            throw new UnreadableDataCollectionException(multipartFile.getOriginalFilename());
        }

        final DataCollectionEntity dataCollectionEntity = dataCollectionService
                .create(name, dataCollectionType, data, multipartFile.getOriginalFilename(), principal.getName());
        return new ResponseEntity<>(dataCollectionMapper.map(dataCollectionEntity), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Page<DataCollectionDTO>> list(final Pageable pageable, final String searchParam) {
        final Page<DataCollectionEntity> dataCollectionEntities = dataCollectionService.list(pageable, searchParam);
        return new ResponseEntity<>(dataCollectionMapper.map(dataCollectionEntities), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<DataCollectionDTO> get(final String name) {
        return new ResponseEntity<>(dataCollectionMapper.map(dataCollectionService.get(decodeBase64(name))), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<DataCollectionDTO> update(final String name,
            final DataCollectionUpdateRequestDTO dataCollectionUpdateRequestDTO,
            final Principal principal) {
        final DataCollectionEntity entity = dataCollectionService
                .update(decodeBase64(name), dataCollectionMapper.map(dataCollectionUpdateRequestDTO),
                        principal.getName());

        return new ResponseEntity<>(dataCollectionMapper.map(entity), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> delete(final String name) {
        dataCollectionService.delete(decodeBase64(name));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

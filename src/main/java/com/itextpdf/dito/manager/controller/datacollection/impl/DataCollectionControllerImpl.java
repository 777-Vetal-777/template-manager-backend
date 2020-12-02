package com.itextpdf.dito.manager.controller.datacollection.impl;

import com.itextpdf.dito.manager.component.mapper.datacollection.DataCollectionMapper;
import com.itextpdf.dito.manager.controller.datacollection.DataCollectionController;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionCreateRequestDTO;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionDTO;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionType;
import com.itextpdf.dito.manager.entity.DataCollectionEntity;
import com.itextpdf.dito.manager.service.datacollection.DataCollectionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
public class DataCollectionControllerImpl implements DataCollectionController {

    private final DataCollectionService dataCollectionService;
    private final DataCollectionMapper dataCollectionMapper;

    public DataCollectionControllerImpl(final DataCollectionService dataCollectionService,
                                        final DataCollectionMapper dataCollectionMapper) {
        this.dataCollectionService = dataCollectionService;
        this.dataCollectionMapper = dataCollectionMapper;
    }

    @Override
    public ResponseEntity<DataCollectionDTO> create(final String name, DataCollectionType type, final MultipartFile attachment, final Principal principal) {
        final DataCollectionEntity collectionEntity = new DataCollectionEntity();
        collectionEntity.setName(name);
        collectionEntity.setType(type);
        final DataCollectionDTO dataCollectionDTO = dataCollectionMapper.map(dataCollectionService.create(collectionEntity, attachment, principal.getName()));
        return new ResponseEntity<>(dataCollectionDTO, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Page<DataCollectionDTO>> list(final Pageable pageable, final String searchParam) {
        final Page<DataCollectionEntity> result = dataCollectionService.list(pageable, searchParam);
        return new ResponseEntity<>(dataCollectionMapper.map(result), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<DataCollectionDTO> get(final String name) {
        return new ResponseEntity<>(dataCollectionMapper.map(dataCollectionService.get(name)), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<DataCollectionDTO> update(final String name,
                                                    final DataCollectionCreateRequestDTO requestDTO,
                                                    final Principal principal) {
        final DataCollectionEntity entity = dataCollectionMapper.map(requestDTO);
        return new ResponseEntity<>(dataCollectionMapper.map(dataCollectionService.update(name, entity, principal.getName())), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> delete(final String name) {
        dataCollectionService.delete(name);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

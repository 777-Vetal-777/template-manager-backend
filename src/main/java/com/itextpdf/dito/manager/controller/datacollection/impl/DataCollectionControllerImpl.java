package com.itextpdf.dito.manager.controller.datacollection.impl;

import com.itextpdf.dito.manager.controller.datacollection.DataCollectionController;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionCreateRequestDTO;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionDTO;
import com.itextpdf.dito.manager.service.datacollections.DataCollectionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class DataCollectionControllerImpl implements DataCollectionController {

    private final DataCollectionService dataCollectionService;

    public DataCollectionControllerImpl(final DataCollectionService dataCollectionService) {
        this.dataCollectionService = dataCollectionService;
    }

    @Override
    public ResponseEntity<DataCollectionDTO> create(final DataCollectionCreateRequestDTO requestDTO, final Principal principal) {
        return new ResponseEntity<>(dataCollectionService.create(requestDTO, principal), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Page<DataCollectionDTO>> list(final Pageable pageable, final String searchParam) {
        return new ResponseEntity<>(dataCollectionService.list(pageable, searchParam), HttpStatus.OK);
    }
}

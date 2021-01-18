package com.itextpdf.dito.manager.controller.datasample.impl;

import com.itextpdf.dito.manager.component.mapper.datasample.DataSampleMapper;
import com.itextpdf.dito.manager.controller.AbstractController;
import com.itextpdf.dito.manager.controller.datasample.DataSampleController;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionDTO;
import com.itextpdf.dito.manager.dto.datasample.DataSampleDTO;
import com.itextpdf.dito.manager.dto.datasample.create.DataSampleCreateRequestDTO;

import java.security.Principal;

import com.itextpdf.dito.manager.filter.datasample.DataSampleFilter;
import com.itextpdf.dito.manager.service.datasample.DataSampleService;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DataSampleControllerImpl extends AbstractController implements DataSampleController {

    private final DataSampleService dataSampleService;
    private final DataSampleMapper dataSampleMapper;

    public DataSampleControllerImpl(final DataSampleService dataSampleService,
                                    final DataSampleMapper dataSampleMapper) {
        this.dataSampleService = dataSampleService;
        this.dataSampleMapper = dataSampleMapper;
    }


    @Override
    public ResponseEntity<DataSampleDTO> create(final DataSampleCreateRequestDTO templateCreateRequestDTO,
                                                final Principal principal) {
        throw new NotImplementedException("Not realized yet");
    }


}
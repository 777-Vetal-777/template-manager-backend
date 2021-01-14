package com.itextpdf.dito.manager.controller.datasample.impl;

import com.itextpdf.dito.manager.controller.AbstractController;
import com.itextpdf.dito.manager.controller.datasample.DataSampleController;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionDTO;
import com.itextpdf.dito.manager.dto.datasample.create.DataSampleCreateRequestDTO;

import java.security.Principal;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DataSampleControllerImpl extends AbstractController implements DataSampleController {

    @Override
    public ResponseEntity<DataCollectionDTO> create(final DataSampleCreateRequestDTO templateCreateRequestDTO,
                                                    final Principal principal) {
        throw new NotImplementedException("Not realized yet");
    }

}
package com.itextpdf.dito.manager.controller.instance.impl;

import com.itextpdf.dito.manager.controller.AbstractController;
import com.itextpdf.dito.manager.controller.instance.InstanceController;
import com.itextpdf.dito.manager.dto.instance.create.InstanceCreateRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class InstanceControllerImpl extends AbstractController implements InstanceController {

    @Override
    public ResponseEntity<Void> getInstanceStatus(String socket) {
        return null;
    }

    @Override
    public ResponseEntity<Void> saveInstances(@Valid InstanceCreateRequestDTO createRequestDTO) {
        return null;
    }
}

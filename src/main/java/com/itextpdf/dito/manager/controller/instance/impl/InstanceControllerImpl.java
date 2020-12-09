package com.itextpdf.dito.manager.controller.instance.impl;

import com.itextpdf.dito.manager.controller.AbstractController;
import com.itextpdf.dito.manager.controller.instance.InstanceController;
import com.itextpdf.dito.manager.dto.instance.InstanceDTO;
import com.itextpdf.dito.manager.dto.instance.create.InstanceCreateRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
public class InstanceControllerImpl extends AbstractController implements InstanceController {
    @Override
    public ResponseEntity<Void> getInstanceStatus(String socket) {
        return null;
    }

    @Override
    public ResponseEntity<List<InstanceDTO>> saveInstances(@Valid InstanceCreateRequestDTO createRequestDTO) {
        return null;
    }

    @Override
    public ResponseEntity<Void> deleteInstance(@NotNull String name) {
        return null;
    }
}

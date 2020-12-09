package com.itextpdf.dito.manager.controller.instance.impl;

import com.itextpdf.dito.manager.controller.AbstractController;
import com.itextpdf.dito.manager.controller.instance.InstanceController;
import com.itextpdf.dito.manager.dto.instance.InstanceDTO;
import com.itextpdf.dito.manager.dto.instance.create.InstanceCreateRequestDTO;
import com.itextpdf.dito.manager.dto.instance.filter.InstanceFilterDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
public class InstanceControllerImpl extends AbstractController implements InstanceController {
    @Override
    public ResponseEntity<Void> list(String socket) {
        return null;
    }

    @Override
    public ResponseEntity<List<InstanceDTO>> saveInstances(@Valid InstanceCreateRequestDTO createRequestDTO) {
        return null;
    }

    @Override
    public ResponseEntity<Void> deleteInstance(final String name) {
        return null;
    }

    @Override
    public ResponseEntity<Page<InstanceDTO>> list(Pageable pageable, InstanceFilterDTO instanceFilterDTO, String searchParam) {
        return null;
    }
}

package com.itextpdf.dito.manager.controller.instance.impl;

import com.itextpdf.dito.manager.component.mapper.instance.InstanceMapper;
import com.itextpdf.dito.manager.controller.AbstractController;
import com.itextpdf.dito.manager.controller.instance.InstanceController;
import com.itextpdf.dito.manager.dto.instance.InstanceDTO;
import com.itextpdf.dito.manager.dto.instance.create.InstanceRememberRequestDTO;
import com.itextpdf.dito.manager.entity.InstanceEntity;
import com.itextpdf.dito.manager.filter.instance.InstanceFilter;
import com.itextpdf.dito.manager.service.instance.InstanceService;

import java.security.Principal;
import java.util.List;
import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InstanceControllerImpl extends AbstractController implements InstanceController {
    private InstanceService instanceService;
    private InstanceMapper instanceMapper;

    public InstanceControllerImpl(final InstanceService instanceService, final InstanceMapper instanceMapper) {
        this.instanceService = instanceService;
        this.instanceMapper = instanceMapper;
    }

    @Override
    public ResponseEntity<List<InstanceDTO>> remember(
            @Valid final InstanceRememberRequestDTO instanceRememberRequestDTO,
            final Principal principal) {
        final List<InstanceEntity> entities = instanceMapper.mapDTOs(instanceRememberRequestDTO.getInstances());
        return new ResponseEntity<>(instanceMapper.mapEntities(instanceService.save(entities, principal.getName())),
                HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> ping(String socket) {
        return null;
    }

    @Override
    public ResponseEntity<Void> forget(final String name) {
        return null;
    }

    @Override
    public ResponseEntity<Page<InstanceDTO>> getInstances(final Pageable pageable, final InstanceFilter instanceFilter,
            String searchParam) {
        return null;
    }
}

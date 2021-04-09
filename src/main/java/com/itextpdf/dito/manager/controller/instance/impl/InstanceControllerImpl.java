package com.itextpdf.dito.manager.controller.instance.impl;

import com.itextpdf.dito.manager.component.client.instance.InstanceClient;
import com.itextpdf.dito.manager.component.encoder.Encoder;
import com.itextpdf.dito.manager.component.mapper.instance.InstanceMapper;
import com.itextpdf.dito.manager.controller.AbstractController;
import com.itextpdf.dito.manager.controller.instance.InstanceController;
import com.itextpdf.dito.manager.dto.instance.InstanceDTO;
import com.itextpdf.dito.manager.dto.instance.create.InstanceHeaderRequestDTO;
import com.itextpdf.dito.manager.dto.instance.update.InstanceUpdateRequestDTO;
import com.itextpdf.dito.manager.dto.instance.create.InstancesRememberRequestDTO;
import com.itextpdf.dito.manager.entity.InstanceEntity;
import com.itextpdf.dito.manager.filter.instance.InstanceFilter;
import com.itextpdf.dito.manager.service.instance.InstanceService;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InstanceControllerImpl extends AbstractController implements InstanceController {
    private static final Logger log = LogManager.getLogger(InstanceControllerImpl.class);
    private final InstanceService instanceService;
    private final InstanceMapper instanceMapper;
    private final InstanceClient instanceClient;
    private final Encoder encoder;

    public InstanceControllerImpl(final InstanceService instanceService, final InstanceClient instanceClient,
                                  final InstanceMapper instanceMapper, final Encoder encoder) {
        this.instanceService = instanceService;
        this.instanceMapper = instanceMapper;
        this.instanceClient = instanceClient;
        this.encoder = encoder;
    }

    @Override
    public ResponseEntity<List<InstanceDTO>> remember(
            @Valid final InstancesRememberRequestDTO instancesRememberRequestDTO,
            final Principal principal) {
        log.info("Save instances: {} was started ", instancesRememberRequestDTO);
        final List<InstanceEntity> entities = instanceMapper.map(instancesRememberRequestDTO.getInstances());
        final List<InstanceEntity> savedInstances = instanceService.save(entities, principal.getName());
        log.info("Save instances: {} was finished successfully", instancesRememberRequestDTO);
        return new ResponseEntity<>(instanceMapper.mapEntities(savedInstances),
                HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> ping(final String socket, final InstanceHeaderRequestDTO headers) {
        log.info("Get instance status by socket: {} and headers: {} was started", socket, headers);
        final String customHeaderName = Optional.ofNullable(headers).map(InstanceHeaderRequestDTO::getHeaderName).orElse(null);
        final String customHeaderValue = Optional.ofNullable(headers).map(InstanceHeaderRequestDTO::getHeaderValue).orElse(null);
        instanceClient.ping(encoder.decode(socket), customHeaderName, customHeaderValue);
        log.info("Get instance status by socket: {} and headers: {} was finished successfully", socket, headers);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> forget(final String name) {
        log.info("Disconnect instance by name: {} was started", name);
        instanceService.forget(encoder.decode(name));
        log.info("Disconnect instance by name: {} was finished successfully", name);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Page<InstanceDTO>> getInstances(final Pageable pageable,
                                                          final InstanceFilter instanceFilter,
                                                          final String searchParam) {
        log.info("Get information about instances by filter: {} and searchParam: {} was started", instanceFilter, searchParam);
        final Page<InstanceEntity> instanceEntities = instanceService.getAll(instanceFilter, pageable, searchParam);
        log.info("Get information about instances by filter: {} and searchParam: {} was finished successfully", instanceFilter, searchParam);
        return new ResponseEntity<>(instanceMapper.map(instanceEntities), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<InstanceDTO>> getInstances() {
        log.info("Get information about instances was started");
        final List<InstanceEntity> instanceEntities = instanceService.getAll();
        log.info("Get information about instances was finished successfully");
        return new ResponseEntity<>(instanceMapper.mapEntities(instanceEntities), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<InstanceDTO> update(final String name, @Valid final InstanceUpdateRequestDTO instanceUpdateRequestDTO) {
        log.info("Update instance by name: {} and instanceUpdateRequestDTO: {} was started", name, instanceUpdateRequestDTO);
        final InstanceEntity instanceEntity = instanceService
                .update(encoder.decode(name), instanceMapper.map(instanceUpdateRequestDTO));
        log.info("Update instance by name: {} and instanceUpdateRequestDTO: {} was finished successfully", name, instanceUpdateRequestDTO);
        return new ResponseEntity<>(instanceMapper.map(instanceEntity), HttpStatus.OK);
    }
}

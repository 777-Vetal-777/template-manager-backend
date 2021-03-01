package com.itextpdf.dito.manager.controller.permission.impl;

import com.itextpdf.dito.manager.component.mapper.permission.PermissionMapper;
import com.itextpdf.dito.manager.controller.AbstractController;
import com.itextpdf.dito.manager.controller.permission.PermissionController;
import com.itextpdf.dito.manager.dto.permission.PermissionDTO;
import com.itextpdf.dito.manager.entity.PermissionEntity;
import com.itextpdf.dito.manager.service.permission.PermissionService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PermissionControllerImpl extends AbstractController implements PermissionController {
    private static final Logger log = LogManager.getLogger(PermissionControllerImpl.class);
    private final PermissionService permissionService;
    private final PermissionMapper permissionMapper;

    public PermissionControllerImpl(final PermissionService permissionService,
            final PermissionMapper permissionMapper) {
        this.permissionService = permissionService;
        this.permissionMapper = permissionMapper;
    }

    @Override
    public ResponseEntity<Page<PermissionDTO>> list(final Pageable pageable, final String searchParam) {
        log.info("Get permission list with Pageable and searchParam: {} was started", searchParam);
        final Page<PermissionDTO> result = permissionMapper.map(permissionService.list(pageable, searchParam));
        log.info("Get permission list with Pageable and searchParam: {} was finished successfully", searchParam);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<PermissionDTO>> list() {
        log.info("Get permission list was started");
        final List<PermissionEntity> permissionEntities = permissionService.list();
        log.info("Get permission list was finished successfully");
        return new ResponseEntity<>(permissionMapper.map(permissionEntities), HttpStatus.OK);
    }
}

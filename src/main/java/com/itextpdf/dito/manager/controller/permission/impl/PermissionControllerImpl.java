package com.itextpdf.dito.manager.controller.permission.impl;

import com.itextpdf.dito.manager.component.mapper.permission.PermissionMapper;
import com.itextpdf.dito.manager.controller.permission.PermissionController;
import com.itextpdf.dito.manager.dto.permission.PermissionDTO;
import com.itextpdf.dito.manager.service.permission.PermissionService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PermissionControllerImpl implements PermissionController {
    private final PermissionService permissionService;
    private final PermissionMapper permissionMapper;

    public PermissionControllerImpl(final PermissionService permissionService,
            final PermissionMapper permissionMapper) {
        this.permissionService = permissionService;
        this.permissionMapper = permissionMapper;
    }

    @Override
    public ResponseEntity<Page<PermissionDTO>> list(final Pageable pageable, final String searchParam) {
        final Page<PermissionDTO> result = permissionMapper.map(permissionService.list(pageable, searchParam));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}

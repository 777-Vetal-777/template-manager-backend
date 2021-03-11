package com.itextpdf.dito.manager.controller.role.impl;

import com.itextpdf.dito.manager.component.mapper.role.RoleMapper;
import com.itextpdf.dito.manager.controller.AbstractController;
import com.itextpdf.dito.manager.controller.role.RoleController;
import com.itextpdf.dito.manager.dto.role.RoleDTO;
import com.itextpdf.dito.manager.dto.role.create.RoleCreateRequestDTO;
import com.itextpdf.dito.manager.dto.role.update.RoleUpdateRequestDTO;
import com.itextpdf.dito.manager.entity.RoleEntity;
import com.itextpdf.dito.manager.filter.role.RoleFilter;
import com.itextpdf.dito.manager.model.role.RoleWithUsersModel;
import com.itextpdf.dito.manager.service.role.RoleService;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RoleControllerImpl extends AbstractController implements RoleController {
    private static final Logger log = LogManager.getLogger(RoleControllerImpl.class);
    private final RoleService roleService;
    private final RoleMapper roleMapper;

    public RoleControllerImpl(final RoleService roleService,
                              final RoleMapper roleMapper) {
        this.roleService = roleService;
        this.roleMapper = roleMapper;
    }

    @Override
    public ResponseEntity<RoleDTO> create(@Valid final RoleCreateRequestDTO roleCreateRequestDTO) {
        log.info("Create role with params: {} was started", roleCreateRequestDTO);
        final RoleEntity result = roleService
                .create(roleCreateRequestDTO.getName(), roleCreateRequestDTO.getPermissions(), Boolean.TRUE);
        log.info("Create role with params: {} was finished successfully", roleCreateRequestDTO);
        return new ResponseEntity<>(roleMapper.map(result), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Page<RoleDTO>> list(final Pageable pageable,
                                              final RoleFilter roleFilter,
                                              final String searchParam) {
        log.info("Get role list by filter: {} and searchParam: {} was started", roleFilter, searchParam);
        final Page<RoleWithUsersModel> roleWithUsersModels = roleService.list(pageable, roleFilter, searchParam);
        log.info("Get role list by filter: {} and searchParam: {} was finished successfully", roleFilter, searchParam);
        return new ResponseEntity<>(roleMapper.mapModels(roleWithUsersModels), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<RoleDTO> update(final String name,
                                          @Valid final RoleUpdateRequestDTO roleUpdateRequestDTO) {
        log.info("Update role by name: {} and params: {} was started", name, roleUpdateRequestDTO);
        final RoleEntity roleEntity = roleMapper.map(roleUpdateRequestDTO);
        final RoleEntity updatedRole = roleService.update(decodeBase64(name), roleEntity, roleUpdateRequestDTO.getPermissions());
        log.info("Update role by name: {} and params: {} was finished successfully", name, roleUpdateRequestDTO);
        return new ResponseEntity<>(
                roleMapper.map(updatedRole),
                HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> delete(final String name) {
        log.info("Delete role by name: {} was started", name);
        roleService.deleteMasterRole(decodeBase64(name));
        log.info("Delete role by name: {} was finished successfully", name);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

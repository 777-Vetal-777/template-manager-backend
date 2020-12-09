package com.itextpdf.dito.manager.controller.role.impl;

import com.itextpdf.dito.manager.component.mapper.role.RoleMapper;
import com.itextpdf.dito.manager.controller.AbstractController;
import com.itextpdf.dito.manager.controller.role.RoleController;
import com.itextpdf.dito.manager.dto.role.RoleDTO;
import com.itextpdf.dito.manager.dto.role.create.RoleCreateRequestDTO;
import com.itextpdf.dito.manager.dto.role.update.RoleUpdateRequestDTO;
import com.itextpdf.dito.manager.entity.RoleEntity;
import com.itextpdf.dito.manager.service.role.RoleService;

import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RoleControllerImpl extends AbstractController implements RoleController {

    private final RoleService roleService;
    private final RoleMapper roleMapper;

    public RoleControllerImpl(final RoleService roleService,
            final RoleMapper roleMapper) {
        this.roleService = roleService;
        this.roleMapper = roleMapper;
    }

    @Override
    public ResponseEntity<RoleDTO> create(final @Valid RoleCreateRequestDTO roleCreateRequestDTO) {
        final RoleEntity result = roleService
                .create(roleMapper.map(roleCreateRequestDTO), roleCreateRequestDTO.getPermissions());
        return new ResponseEntity<>(roleMapper.map(result), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Page<RoleDTO>> list(final Pageable pageable, final String searchParam) {
        return new ResponseEntity<>(roleMapper.map(roleService.list(pageable, searchParam)), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<RoleDTO> update(final String name,
            final RoleUpdateRequestDTO roleUpdateRequestDTO) {
        final RoleEntity updatedRole = roleMapper.map(roleUpdateRequestDTO);
        return new ResponseEntity<>(
                roleMapper.map(roleService.update(decodeBase64(name), updatedRole, roleUpdateRequestDTO.getPermissions())),
                HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> delete(final String name) {
        roleService.delete(decodeBase64(name));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

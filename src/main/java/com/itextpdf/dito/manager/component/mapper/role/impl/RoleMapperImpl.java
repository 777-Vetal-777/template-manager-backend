package com.itextpdf.dito.manager.component.mapper.role.impl;

import com.itextpdf.dito.manager.component.mapper.permission.PermissionMapper;
import com.itextpdf.dito.manager.component.mapper.resource.impl.ResourceMapperImpl;
import com.itextpdf.dito.manager.component.mapper.role.RoleMapper;
import com.itextpdf.dito.manager.dto.role.RoleDTO;
import com.itextpdf.dito.manager.dto.role.create.RoleCreateRequestDTO;
import com.itextpdf.dito.manager.dto.role.update.RoleUpdateRequestDTO;
import com.itextpdf.dito.manager.entity.RoleEntity;
import com.itextpdf.dito.manager.entity.UserEntity;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class RoleMapperImpl implements RoleMapper {
    private static final Logger log = LogManager.getLogger(RoleMapperImpl.class);
    private final PermissionMapper permissionMapper;

    public RoleMapperImpl(final PermissionMapper permissionMapper) {
        this.permissionMapper = permissionMapper;
    }

    @Override
    public RoleEntity map(final RoleCreateRequestDTO dto) {
        final RoleEntity entity = new RoleEntity();
        entity.setName(dto.getName());
        return entity;
    }

    @Override
    public RoleEntity map(final RoleUpdateRequestDTO dto) {
        log.info("Convert {} to entity was started", dto);
        final RoleEntity entity = new RoleEntity();
        entity.setName(dto.getName());
        log.info("Convert {} to entity was finished successfully", dto);
        return entity;
    }

    @Override
    public RoleDTO map(final RoleEntity entity) {
        log.info("Convert role: {} to roleDto was started", entity.getId());
        final RoleDTO dto = new RoleDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setType(entity.getType().toString());
        dto.setMaster(entity.getMaster());
        dto.setUsersEmails(entity.getUsers() != null
                ? entity.getUsers().stream().map(UserEntity::getEmail).collect(Collectors.toList())
                : Collections.emptyList());
        dto.setPermissions(permissionMapper.map(entity.getPermissions()));
        log.info("Convert role: {} to roleDto was finished successfully", entity.getId());
        return dto;
    }

    @Override
    public RoleDTO mapWithoutUsers(RoleEntity entity) {
        log.info("Convert role: {} to roleDto was started", entity.getId());
        final RoleDTO dto = new RoleDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setType(entity.getType().toString());
        dto.setMaster(entity.getMaster());
        dto.setPermissions(permissionMapper.map(entity.getPermissions()));
        log.info("Convert role: {} to roleDto was finished successfully", entity.getId());
        return dto;
    }

    @Override
    public Page<RoleDTO> map(final Page<RoleEntity> entities) {
        return entities.map(this::map);
    }

    @Override
    public Set<RoleDTO> map(Set<RoleEntity> entities) {
        return entities.stream().map(this::map).collect(Collectors.toSet());
    }
}

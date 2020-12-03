package com.itextpdf.dito.manager.component.mapper.role.impl;

import com.itextpdf.dito.manager.component.mapper.permission.PermissionMapper;
import com.itextpdf.dito.manager.component.mapper.role.RoleMapper;
import com.itextpdf.dito.manager.component.mapper.user.UserMapper;
import com.itextpdf.dito.manager.dto.role.RoleCreateRequestDTO;
import com.itextpdf.dito.manager.dto.role.RoleDTO;
import com.itextpdf.dito.manager.entity.RoleEntity;

import com.itextpdf.dito.manager.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class RoleMapperImpl implements RoleMapper {
    private final UserMapper userMapper;
    private final PermissionMapper permissionMapper;

    public RoleMapperImpl(final UserMapper userMapper, final PermissionMapper permissionMapper) {
        this.userMapper = userMapper;
        this.permissionMapper = permissionMapper;
    }

    @Override
    public RoleEntity map(final RoleCreateRequestDTO dto) {
        final RoleEntity entity = new RoleEntity();
        entity.setName(dto.getName());
        return entity;
    }

    @Override
    public RoleDTO map(final RoleEntity entity) {
        final RoleDTO dto = new RoleDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setType(entity.getType().getName().toString());
        dto.setUsersEmails(entity.getUsers() != null
                ? entity.getUsers().stream().map(UserEntity::getEmail).collect(Collectors.toList())
                : Collections.emptyList());
        dto.setPermissions(permissionMapper.map(entity.getPermissions()));
        return dto;
    }

    @Override
    public Page<RoleDTO> map(final Page<RoleEntity> entities) {
        return entities.map(this::map);
    }
}

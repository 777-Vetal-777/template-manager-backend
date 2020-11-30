package com.itextpdf.dito.manager.component.mapper.role.impl;

import com.itextpdf.dito.manager.component.mapper.role.RoleMapper;
import com.itextpdf.dito.manager.dto.role.RoleCreateRequestDTO;
import com.itextpdf.dito.manager.dto.role.RoleDTO;
import com.itextpdf.dito.manager.entity.RoleEntity;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class RoleMapperImpl implements RoleMapper {
    @Override
    public RoleEntity map(final RoleCreateRequestDTO dto) {
        final RoleEntity entity = new RoleEntity();
        entity.setName(dto.getName());
        return entity;
    }

    @Override
    public RoleDTO map(final RoleEntity entity) {
        final RoleDTO dto = new RoleDTO();
        dto.setName(entity.getName());
        dto.setType(entity.getType().getName().toString());
        return dto;
    }

    @Override
    public Page<RoleDTO> map(final Page<RoleEntity> entities) {
        return entities.map(this::map);
    }
}

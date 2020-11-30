package com.itextpdf.dito.manager.component.mapper.permission.impl;

import com.itextpdf.dito.manager.component.mapper.permission.PermissionMapper;
import com.itextpdf.dito.manager.dto.permission.PermissionDTO;
import com.itextpdf.dito.manager.entity.PermissionEntity;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class PermissionMapperImpl implements PermissionMapper {
    @Override
    public PermissionDTO map(PermissionEntity entity) {
        final PermissionDTO result = new PermissionDTO();

        result.setName(entity.getName());
        result.setAvailableForCustomRole(entity.getAvailableForCustomRole());

        return result;
    }

    @Override
    public PermissionEntity map(PermissionDTO dto) {
        final PermissionEntity result = new PermissionEntity();

        result.setName(dto.getName());
        result.setAvailableForCustomRole(dto.getAvailableForCustomRole());

        return result;
    }

    @Override
    public Page<PermissionDTO> map(Page<PermissionEntity> entities) {
        return entities.map(this::map);
    }
}

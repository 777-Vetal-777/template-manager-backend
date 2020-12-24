package com.itextpdf.dito.manager.component.mapper.permission.impl;

import com.itextpdf.dito.manager.component.mapper.permission.PermissionMapper;
import com.itextpdf.dito.manager.dto.permission.PermissionDTO;
import com.itextpdf.dito.manager.entity.PermissionEntity;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class PermissionMapperImpl implements PermissionMapper {
    @Override
    public PermissionDTO map(final PermissionEntity entity) {
        final PermissionDTO result = new PermissionDTO();

        result.setName(entity.getName());
        result.setOptionalForCustomRole(entity.getOptionalForCustomRole());

        return result;
    }

    @Override
    public PermissionEntity map(final PermissionDTO dto) {
        final PermissionEntity result = new PermissionEntity();

        result.setName(dto.getName());
        result.setOptionalForCustomRole(dto.getOptionalForCustomRole());

        return result;
    }

    @Override
    public List<PermissionDTO> map(Collection<PermissionEntity> entities) {
        return entities != null
                ? entities.stream().map(this::map).collect(Collectors.toList())
                : Collections.emptyList();
    }

    @Override
    public Page<PermissionDTO> map(final Page<PermissionEntity> entities) {
        return entities.map(this::map);
    }
}

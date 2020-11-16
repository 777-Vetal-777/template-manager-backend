package com.itextpdf.dito.manager.component.mapper.workspace.impl;

import com.itextpdf.dito.manager.component.mapper.workspace.WorkspaceMapper;
import com.itextpdf.dito.manager.dto.workspace.create.WorkspaceCreateRequestDTO;
import com.itextpdf.dito.manager.dto.workspace.create.WorkspaceCreateResponseDTO;
import com.itextpdf.dito.manager.entity.WorkspaceEntity;
import org.springframework.stereotype.Component;

@Component
public class WorkspaceMapperImpl implements WorkspaceMapper {
    @Override
    public WorkspaceEntity map(final WorkspaceCreateRequestDTO dto) {
        final WorkspaceEntity entity = new WorkspaceEntity();
        entity.setLanguage(dto.getLanguage());
        entity.setName(dto.getName());
        entity.setTimezone(dto.getTimezone());
        return entity;
    }

    @Override
    public WorkspaceCreateResponseDTO map(final WorkspaceEntity entity) {
        final WorkspaceCreateResponseDTO response = new WorkspaceCreateResponseDTO();
        response.setId(entity.getId());
        response.setLanguage(entity.getLanguage());
        response.setTimezone(entity.getTimezone());
        response.setName(entity.getName());
        return response;
    }
}

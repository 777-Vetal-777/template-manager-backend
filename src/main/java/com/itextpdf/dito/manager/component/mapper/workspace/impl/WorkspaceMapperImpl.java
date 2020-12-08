package com.itextpdf.dito.manager.component.mapper.workspace.impl;

import com.itextpdf.dito.manager.component.mapper.workspace.WorkspaceMapper;
import com.itextpdf.dito.manager.dto.workspace.WorkspaceDTO;
import com.itextpdf.dito.manager.dto.workspace.create.WorkspaceCreateRequestDTO;
import com.itextpdf.dito.manager.entity.WorkspaceEntity;

import org.springframework.stereotype.Component;

@Component
public class WorkspaceMapperImpl implements WorkspaceMapper {
    @Override
    public WorkspaceEntity map(final WorkspaceCreateRequestDTO dto) {
        final WorkspaceEntity result = new WorkspaceEntity();

        result.setLanguage(dto.getLanguage());
        result.setName(dto.getName());
        result.setTimezone(dto.getTimezone());

        return result;
    }

    @Override
    public WorkspaceEntity map(final WorkspaceDTO dto) {
        final WorkspaceEntity result = new WorkspaceEntity();

        result.setName(dto.getName());
        result.setLanguage(dto.getLanguage());
        result.setTimezone(dto.getTimezone());

        return result;
    }

    @Override
    public WorkspaceDTO map(final WorkspaceEntity entity) {
        final WorkspaceDTO result = new WorkspaceDTO();

        result.setName(entity.getName());
        result.setLanguage(entity.getLanguage());
        result.setTimezone(entity.getTimezone());

        return result;
    }
}

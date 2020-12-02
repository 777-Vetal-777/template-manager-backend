package com.itextpdf.dito.manager.component.mapper.workspace.impl;

import com.itextpdf.dito.manager.component.mapper.workspace.WorkspaceMapper;
import com.itextpdf.dito.manager.dto.workspace.WorkspaceDTO;
import com.itextpdf.dito.manager.dto.workspace.create.WorkspaceCreateRequestDTO;
import com.itextpdf.dito.manager.dto.workspace.create.WorkspaceCreateResponseDTO;
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
    public WorkspaceCreateResponseDTO map(final WorkspaceEntity entity) {
        final WorkspaceCreateResponseDTO result = new WorkspaceCreateResponseDTO();

        result.setId(entity.getId());
        result.setLanguage(entity.getLanguage());
        result.setTimezone(entity.getTimezone());
        result.setName(entity.getName());

        return result;
    }

    @Override
    public WorkspaceEntity fromDto(final WorkspaceDTO dto) {
        final WorkspaceEntity result = new WorkspaceEntity();

        result.setId(dto.getId());
        result.setName(dto.getName());
        result.setLanguage(dto.getLanguage());
        result.setTimezone(dto.getTimezone());

        return result;
    }

    @Override
    public WorkspaceDTO toDto(final WorkspaceEntity entity) {
        final WorkspaceDTO result = new WorkspaceDTO();

        result.setId(entity.getId());
        result.setName(entity.getName());
        result.setLanguage(entity.getLanguage());
        result.setTimezone(entity.getTimezone());

        return result;
    }
}

package com.itextpdf.dito.manager.component.mapper.workspace;

import com.itextpdf.dito.manager.dto.workspace.create.WorkspaceCreateRequestDTO;
import com.itextpdf.dito.manager.dto.workspace.create.WorkspaceCreateResponseDTO;
import com.itextpdf.dito.manager.entity.WorkspaceEntity;

public interface WorkspaceMapper {
    WorkspaceEntity map(WorkspaceCreateRequestDTO dto);

    WorkspaceCreateResponseDTO map(WorkspaceEntity entity);
}

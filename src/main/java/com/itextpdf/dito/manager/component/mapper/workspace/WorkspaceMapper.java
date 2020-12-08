package com.itextpdf.dito.manager.component.mapper.workspace;

import com.itextpdf.dito.manager.dto.workspace.WorkspaceDTO;
import com.itextpdf.dito.manager.dto.workspace.create.WorkspaceCreateRequestDTO;
import com.itextpdf.dito.manager.entity.WorkspaceEntity;

public interface WorkspaceMapper {
    WorkspaceEntity map(WorkspaceCreateRequestDTO dto);

    WorkspaceEntity map(WorkspaceDTO dto);

    WorkspaceDTO map(WorkspaceEntity entity);
}

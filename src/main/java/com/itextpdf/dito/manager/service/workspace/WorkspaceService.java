package com.itextpdf.dito.manager.service.workspace;

import com.itextpdf.dito.manager.dto.workspace.create.WorkspaceCreateRequestDTO;
import com.itextpdf.dito.manager.dto.workspace.create.WorkspaceCreateResponseDTO;


public interface WorkspaceService {
    WorkspaceCreateResponseDTO create(WorkspaceCreateRequestDTO workspaceCreateRequest);
}

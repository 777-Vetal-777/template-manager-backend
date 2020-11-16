package com.itextpdf.dito.manager.controller.workspace.impl;

import com.itextpdf.dito.manager.controller.workspace.WorkspaceController;
import com.itextpdf.dito.manager.dto.workspace.create.WorkspaceCreateRequestDTO;
import com.itextpdf.dito.manager.dto.workspace.create.WorkspaceCreateResponseDTO;
import org.springframework.http.ResponseEntity;

public class WorkspaceControllerImpl implements WorkspaceController {
    @Override
    public ResponseEntity<WorkspaceCreateResponseDTO> create(WorkspaceCreateRequestDTO workspaceCreateRequest) {
        return null;
    }
}

package com.itextpdf.dito.manager.controller.workspace.impl;

import com.itextpdf.dito.manager.controller.workspace.WorkspaceController;
import com.itextpdf.dito.manager.dto.workspace.create.WorkspaceCreateRequestDTO;
import com.itextpdf.dito.manager.dto.workspace.create.WorkspaceCreateResponseDTO;
import com.itextpdf.dito.manager.service.workspace.WorkspaceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WorkspaceControllerImpl implements WorkspaceController {

    private final WorkspaceService workspaceService;

    public WorkspaceControllerImpl(final WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    @Override
    public ResponseEntity<WorkspaceCreateResponseDTO> create(WorkspaceCreateRequestDTO workspaceCreateRequest) {
        return new ResponseEntity<>(workspaceService.create(workspaceCreateRequest), HttpStatus.CREATED);
    }
}

package com.itextpdf.dito.manager.controller.workspace.impl;

import com.itextpdf.dito.manager.component.mapper.workspace.WorkspaceMapper;
import com.itextpdf.dito.manager.controller.workspace.WorkspaceController;
import com.itextpdf.dito.manager.dto.workspace.WorkspaceDTO;
import com.itextpdf.dito.manager.dto.workspace.create.WorkspaceCreateRequestDTO;
import com.itextpdf.dito.manager.dto.workspace.create.WorkspaceCreateResponseDTO;
import com.itextpdf.dito.manager.entity.WorkspaceEntity;
import com.itextpdf.dito.manager.service.workspace.WorkspaceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WorkspaceControllerImpl implements WorkspaceController {

    private final WorkspaceService workspaceService;
    private final WorkspaceMapper workspaceMapper;

    public WorkspaceControllerImpl(final WorkspaceService workspaceService, WorkspaceMapper workspaceMapper) {
        this.workspaceService = workspaceService;
        this.workspaceMapper = workspaceMapper;
    }

    @Override
    public ResponseEntity<WorkspaceCreateResponseDTO> create(final WorkspaceCreateRequestDTO workspaceCreateRequest) {
        WorkspaceEntity workspace = workspaceService.create(workspaceMapper.map(workspaceCreateRequest));
        return new ResponseEntity<>(workspaceMapper.map(workspace), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<WorkspaceDTO> get() {
        return new ResponseEntity<>(workspaceMapper.toDto(workspaceService.get()), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<WorkspaceDTO> update(final WorkspaceDTO workspaceCreateRequest) {
        WorkspaceEntity workspace = workspaceMapper.fromDto(workspaceCreateRequest);
        return new ResponseEntity<>(workspaceMapper.toDto(workspaceService.update(workspace)), HttpStatus.OK);
    }
}

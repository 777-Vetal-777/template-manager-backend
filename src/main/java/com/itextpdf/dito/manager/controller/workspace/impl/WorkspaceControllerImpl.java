package com.itextpdf.dito.manager.controller.workspace.impl;

import com.itextpdf.dito.manager.component.mapper.workspace.WorkspaceMapper;
import com.itextpdf.dito.manager.controller.AbstractController;
import com.itextpdf.dito.manager.controller.workspace.WorkspaceController;
import com.itextpdf.dito.manager.dto.promotionpath.PromotionPathDTO;
import com.itextpdf.dito.manager.dto.workspace.WorkspaceDTO;
import com.itextpdf.dito.manager.dto.workspace.create.WorkspaceCreateRequestDTO;
import com.itextpdf.dito.manager.entity.WorkspaceEntity;
import com.itextpdf.dito.manager.service.workspace.WorkspaceService;

import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WorkspaceControllerImpl extends AbstractController implements WorkspaceController {

    private final WorkspaceService workspaceService;
    private final WorkspaceMapper workspaceMapper;

    public WorkspaceControllerImpl(final WorkspaceService workspaceService, final WorkspaceMapper workspaceMapper) {
        this.workspaceService = workspaceService;
        this.workspaceMapper = workspaceMapper;
    }

    @Override
    public ResponseEntity<WorkspaceDTO> create(final WorkspaceCreateRequestDTO workspaceCreateRequestDTO) {
        WorkspaceEntity workspaceEntity = workspaceService.create(workspaceMapper.map(workspaceCreateRequestDTO));
        return new ResponseEntity<>(workspaceMapper.map(workspaceEntity), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<WorkspaceDTO> get() {
        return new ResponseEntity<>(workspaceMapper.map(workspaceService.get()), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<WorkspaceDTO> update(final WorkspaceDTO workspaceDTO) {
        WorkspaceEntity workspaceEntity = workspaceMapper.map(workspaceDTO);
        return new ResponseEntity<>(workspaceMapper.map(workspaceService.update(workspaceEntity)), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<PromotionPathDTO> getPromotionPath(final String workspaceName) {
        return null;
    }

    @Override
    public ResponseEntity<PromotionPathDTO> updatePromotionPath(final String workspaceName,
            @Valid final PromotionPathDTO promotionPathDTO) {
        return null;
    }
}

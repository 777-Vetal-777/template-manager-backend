package com.itextpdf.dito.manager.controller.workspace.impl;

import com.itextpdf.dito.manager.component.mapper.workspace.WorkspaceMapper;
import com.itextpdf.dito.manager.controller.AbstractController;
import com.itextpdf.dito.manager.controller.workspace.WorkspaceController;
import com.itextpdf.dito.manager.dto.license.LicenseDTO;
import com.itextpdf.dito.manager.dto.promotionpath.PromotionPathDTO;
import com.itextpdf.dito.manager.dto.workspace.WorkspaceDTO;
import com.itextpdf.dito.manager.dto.workspace.create.WorkspaceCreateRequestDTO;
import com.itextpdf.dito.manager.entity.PromotionPathEntity;
import com.itextpdf.dito.manager.entity.WorkspaceEntity;
import com.itextpdf.dito.manager.service.workspace.WorkspaceService;

import java.security.Principal;
import java.util.List;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Validated
@RestController
public class WorkspaceControllerImpl extends AbstractController implements WorkspaceController {

    private final WorkspaceService workspaceService;
    private final WorkspaceMapper workspaceMapper;

    public WorkspaceControllerImpl(final WorkspaceService workspaceService, final WorkspaceMapper workspaceMapper) {
        this.workspaceService = workspaceService;
        this.workspaceMapper = workspaceMapper;
    }

    @Override
    public ResponseEntity<WorkspaceDTO> create(final WorkspaceCreateRequestDTO workspaceCreateRequestDTO, final Principal principal) {
        WorkspaceEntity workspaceEntity = workspaceService.create(workspaceMapper.map(workspaceCreateRequestDTO),
                workspaceCreateRequestDTO.getMainDevelopmentInstanceSocket(), principal.getName());
        return new ResponseEntity<>(workspaceMapper.map(workspaceEntity), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<List<WorkspaceDTO>> getAll() {
        final List<WorkspaceEntity> workspaceEntities = workspaceService.getAll();
        return new ResponseEntity<>(workspaceMapper.map(workspaceEntities), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<WorkspaceDTO> get(final String name) {
        return new ResponseEntity<>(workspaceMapper.map(workspaceService.get(decodeBase64(name))), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<WorkspaceDTO> update(final String name, final WorkspaceDTO workspaceDTO) {
        WorkspaceEntity workspaceEntity = workspaceMapper.map(workspaceDTO);
        return new ResponseEntity<>(workspaceMapper.map(workspaceService.update(decodeBase64(name), workspaceEntity)),
                HttpStatus.OK);
    }

    @Override
    public ResponseEntity<PromotionPathDTO> getPromotionPath(final String workspaceName) {
        final PromotionPathEntity promotionPathEntity = workspaceService.getPromotionPath(decodeBase64(workspaceName));
        final PromotionPathDTO promotionPathDTO = workspaceMapper.map(promotionPathEntity);
        return new ResponseEntity<>(promotionPathDTO, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<PromotionPathDTO> updatePromotionPath(final String workspaceName,
            final PromotionPathDTO promotionPathDTO) {
        PromotionPathDTO result;

        final PromotionPathEntity promotionPathEntity = workspaceService
                .updatePromotionPath(decodeBase64(workspaceName), workspaceMapper.map(promotionPathDTO));
        result = workspaceMapper.map(promotionPathEntity);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<String>> getStageNames(final String workspaceName) {
        final List<String> stageNames = workspaceService.getStageNames(decodeBase64(workspaceName));
        return new ResponseEntity<>(stageNames, HttpStatus.OK);

    }

	@Override
	public ResponseEntity<Void> uploadLisence(MultipartFile multipartFile, Principal principal) {
		throw new NotImplementedException("Method not implemented yet");
	}

	@Override
	public ResponseEntity<LicenseDTO> getLisence(String workspaceName, Principal principal) {
		throw new NotImplementedException("Method not implemented yet");
	}
}

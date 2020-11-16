package com.itextpdf.dito.manager.controller.workspace;

import com.itextpdf.dito.manager.dto.workspace.create.WorkspaceCreateRequestDTO;
import com.itextpdf.dito.manager.dto.workspace.create.WorkspaceCreateResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(WorkspaceController.BASE_NAME)
public interface WorkspaceController {
    String MAJOR_VERSION = "/v1";
    String BASE_NAME = MAJOR_VERSION + "/workspace";

    @PostMapping
    ResponseEntity<WorkspaceCreateResponseDTO> create(final @RequestBody WorkspaceCreateRequestDTO workspaceCreateRequest);
}

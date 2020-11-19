package com.itextpdf.dito.manager.controller.workspace;

import com.itextpdf.dito.manager.config.OpenApiConfig;
import com.itextpdf.dito.manager.dto.workspace.create.WorkspaceCreateRequestDTO;
import com.itextpdf.dito.manager.dto.workspace.create.WorkspaceCreateResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(WorkspaceController.BASE_NAME)
public interface WorkspaceController {
    String MAJOR_VERSION = "/v1";
    String BASE_NAME = MAJOR_VERSION + "/workspaces";

    @PostMapping
    @Operation(summary = "Create workspace", security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME))
    ResponseEntity<WorkspaceCreateResponseDTO> create(@RequestBody WorkspaceCreateRequestDTO workspaceCreateRequest);
}

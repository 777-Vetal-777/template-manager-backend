package com.itextpdf.dito.manager.controller.workspace;

import com.itextpdf.dito.manager.config.OpenApiConfig;
import com.itextpdf.dito.manager.dto.workspace.WorkspaceDTO;
import com.itextpdf.dito.manager.dto.workspace.create.WorkspaceCreateRequestDTO;
import com.itextpdf.dito.manager.dto.workspace.create.WorkspaceCreateResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(WorkspaceController.BASE_NAME)
@Tag(name = "workspace", description = "workspace API")
public interface WorkspaceController {
    String MAJOR_VERSION = "/v1";
    String BASE_NAME = MAJOR_VERSION + "/workspaces";

    @PostMapping
    @Operation(summary = "Create workspace", description = "Create new workspace",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created new workspace", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = WorkspaceCreateResponseDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
    })
    ResponseEntity<WorkspaceCreateResponseDTO> create(@RequestBody WorkspaceCreateRequestDTO workspaceCreateRequest);

    @GetMapping
    @Operation(summary = "get workspace", description = "get info about specified workspace",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created new workspace", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = WorkspaceCreateResponseDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "404", description = "workspace not found", content = @Content),
    })
    ResponseEntity<WorkspaceDTO> get();

    @PutMapping
    @Operation(summary = "update workspace", description = "update existing workspace",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created new workspace", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = WorkspaceCreateResponseDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "404", description = "workspace not found", content = @Content),
    })
    ResponseEntity<WorkspaceDTO> update(@RequestBody WorkspaceDTO workspaceDTO);
}

package com.itextpdf.dito.manager.controller.workspace;

import com.itextpdf.dito.manager.config.OpenApiConfig;
import com.itextpdf.dito.manager.dto.promotionpath.PromotionPathDTO;
import com.itextpdf.dito.manager.dto.workspace.WorkspaceDTO;
import com.itextpdf.dito.manager.dto.workspace.create.WorkspaceCreateRequestDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(WorkspaceController.BASE_NAME)
@Tag(name = "workspace", description = "workspace API")
public interface WorkspaceController {
    String MAJOR_VERSION = "/v1";
    String BASE_NAME = MAJOR_VERSION + "/workspaces";

    String WORKSPACE_PATH_VARIABLE = "name";
    String WORKSPACE_ENDPOINT_WITH_PATH_VARIABLE = "/{" + WORKSPACE_PATH_VARIABLE + "}";
    String WORKSPACE_PROMOTION_PATH_ENDPOINT = WORKSPACE_ENDPOINT_WITH_PATH_VARIABLE + "/promotion-path";
    String WORKSPACE_STAGES_ENDPOINT = WORKSPACE_ENDPOINT_WITH_PATH_VARIABLE + "/stages";

    @PostMapping
    @Operation(summary = "Create workspace", description = "Create new workspace",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created new workspace", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = WorkspaceDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
    })
    ResponseEntity<WorkspaceDTO> create(@Valid @RequestBody WorkspaceCreateRequestDTO workspaceCreateRequestDTO);

    @GetMapping
    @Operation(summary = "Get all workspaces", description = "Get workspaces",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<List<WorkspaceDTO>> getAll();

    @GetMapping(WORKSPACE_ENDPOINT_WITH_PATH_VARIABLE)
    @Operation(summary = "Get workspace", description = "Get workspace",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<WorkspaceDTO> get(@NotBlank @PathVariable(WORKSPACE_PATH_VARIABLE) String name);

    @PatchMapping(WORKSPACE_ENDPOINT_WITH_PATH_VARIABLE)
    @Operation(summary = "Update workspace", description = "Update workspace",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<WorkspaceDTO> update(@NotBlank @PathVariable(WORKSPACE_PATH_VARIABLE) String name,
            @Valid @RequestBody WorkspaceDTO workspaceDTO);

    @GetMapping(WORKSPACE_PROMOTION_PATH_ENDPOINT)
    @Operation(summary = "Promotion path", description = "Retrieve full promotion path",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<PromotionPathDTO> getPromotionPath(@PathVariable(WORKSPACE_PATH_VARIABLE) String workspaceName);

    @PatchMapping(WORKSPACE_PROMOTION_PATH_ENDPOINT)
    @Operation(summary = "Update promotion path", description = "Update promotion path",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<PromotionPathDTO> updatePromotionPath(@PathVariable(WORKSPACE_PATH_VARIABLE) String workspaceName,
            @Valid @RequestBody PromotionPathDTO promotionPathDTO);

    @GetMapping(WORKSPACE_STAGES_ENDPOINT)
    @Operation(summary = "List of stages for workspace", description = "Retrieve list of stage names for workspace",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<List<String>> getStageNames(@PathVariable(WORKSPACE_PATH_VARIABLE) String workspaceName);
}

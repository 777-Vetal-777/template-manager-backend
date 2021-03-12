package com.itextpdf.dito.manager.integration.editor.controller.workspace;

import com.itextpdf.dito.editor.server.common.core.descriptor.WorkspaceInfoDescriptor;
import com.itextpdf.dito.manager.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "editor", description = "editor integration API")
public interface WorkspaceManagementController {

    String WORKSPACE_INFO_URL = "/workspaces/{workspace-id}";

    @GetMapping(value = WORKSPACE_INFO_URL, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get workspace info", description = "returns descriptor of workspace with the same display name as received",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponse(responseCode = "200", description = "Workspace info", content = @Content(mediaType = "application/json", schema = @Schema(implementation = WorkspaceInfoDescriptor.class)))
    @ApiResponse(responseCode = "404", description = "Workspace not found", content = @Content)
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content)
    WorkspaceInfoDescriptor fetch(@PathVariable("workspace-id") String workspaceId);

}

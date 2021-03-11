package com.itextpdf.dito.manager.integration.editor.controller.workspace;

import com.itextpdf.dito.editor.server.common.core.descriptor.WorkspaceInfoDescriptor;
import com.itextpdf.dito.manager.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface WorkspaceManagementController {

    String WORKSPACE_INFO_URL = "/workspaces/{workspace-id}";

    @GetMapping(value = WORKSPACE_INFO_URL, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    WorkspaceInfoDescriptor fetch(@PathVariable("workspace-id") String workspaceId);

}

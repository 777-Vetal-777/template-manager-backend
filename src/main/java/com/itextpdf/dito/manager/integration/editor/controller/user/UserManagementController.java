package com.itextpdf.dito.manager.integration.editor.controller.user;

import java.security.Principal;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;

import com.itextpdf.dito.editor.server.common.core.descriptor.UserInfoDescriptor;
import com.itextpdf.dito.manager.config.OpenApiConfig;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Tag(name = "editor-user", description = "editor integration API")
public interface UserManagementController {
	String BASE_USERS_URL = "/users";
    String CURRENT = BASE_USERS_URL +"/current";

	@GetMapping(value = CURRENT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Get current user descriptor", description = "UserInfoDescriptor", security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
	@ApiResponse(responseCode = "200", description = "Current user info", content = {
			@Content(mediaType = "application/json", schema = @Schema(implementation = UserInfoDescriptor.class)) })
	UserInfoDescriptor getCurrentUser(Principal principal);
}

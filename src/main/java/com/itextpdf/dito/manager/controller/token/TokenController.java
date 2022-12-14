package com.itextpdf.dito.manager.controller.token;

import com.itextpdf.dito.manager.config.OpenApiConfig;
import com.itextpdf.dito.manager.dto.token.TokenDTO;
import com.itextpdf.dito.manager.dto.token.refresh.AccessTokenRefreshRequestDTO;
import com.itextpdf.dito.manager.exception.token.InvalidRefreshTokenException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(TokenController.BASE_NAME)
@Tag(name = "token", description = "token API")
public interface TokenController {
    String MAJOR_VERSION = "/v1";
    String BASE_NAME = MAJOR_VERSION + "/tokens";
    String REFRESH_ENDPOINT = "/refresh";
    String EDITOR_ENDPOINT = "/editor";

    @Operation(summary = "refresh token", description = "request new access token by refresh token",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created new token", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = TokenDTO.class))}),
            @ApiResponse(responseCode = "401", description = "Invalid refresh token", content = @Content),
    })
    @PostMapping(REFRESH_ENDPOINT)
    ResponseEntity<TokenDTO> refresh(@RequestBody AccessTokenRefreshRequestDTO accessTokenRefreshRequestDTO)
            throws InvalidRefreshTokenException;

    @Operation(summary = "editor token", description = "request token for using it from Editor in order to provide integration",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created new token", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = TokenDTO.class))}),
    })
    @GetMapping(EDITOR_ENDPOINT)
    ResponseEntity<TokenDTO> editor(Principal principal);

}

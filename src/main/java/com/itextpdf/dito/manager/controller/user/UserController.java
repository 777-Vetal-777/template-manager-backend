package com.itextpdf.dito.manager.controller.user;

import com.itextpdf.dito.manager.config.OpenApiConfig;
import com.itextpdf.dito.manager.dto.user.UserDTO;
import com.itextpdf.dito.manager.dto.user.create.UserCreateRequestDTO;
import com.itextpdf.dito.manager.dto.user.create.UserCreateResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequestMapping(UserController.BASE_NAME)
@Tag(name = "user", description = "user API")
public interface UserController {
    String MAJOR_VERSION = "/v1";
    String BASE_NAME = MAJOR_VERSION + "/users";
    String USER_DELETE_PATH_VARIABLE = "email";
    String USER_DELETE_ENDPOINT = "/{" + USER_DELETE_PATH_VARIABLE + "}";

    @PostMapping
    @Operation(summary = "Create user", description = "Create new user",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created new user", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserCreateResponseDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input or user already exists", content = @Content),
    })
    ResponseEntity<UserCreateResponseDTO> create(@RequestBody @Valid UserCreateRequestDTO userCreateRequest);

    @GetMapping
    @Operation(summary = "Get user list", description = "Get available users",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<Page<UserDTO>> list(Pageable pageable, @Parameter(description = "user name or email search string") @RequestParam(name = "search", required = false) String searchParam);

    @DeleteMapping(USER_DELETE_ENDPOINT)
    @Operation(summary = "Deactivate user", description = "Deactivate user", security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = {
                    @Content(schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    ResponseEntity<String> delete(@Parameter(description = "user email", required = true) @PathVariable(USER_DELETE_PATH_VARIABLE) String email);
}

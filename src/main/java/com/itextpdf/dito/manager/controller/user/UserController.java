package com.itextpdf.dito.manager.controller.user;

import com.itextpdf.dito.manager.config.OpenApiConfig;
import com.itextpdf.dito.manager.dto.user.UserDTO;
import com.itextpdf.dito.manager.dto.user.create.UserCreateRequestDTO;
import com.itextpdf.dito.manager.dto.user.create.UserCreateResponseDTO;
import com.itextpdf.dito.manager.dto.user.unblock.UsersUnblockRequestDTO;
import com.itextpdf.dito.manager.dto.user.update.UpdatePasswordRequestDTO;
import com.itextpdf.dito.manager.dto.user.update.UserUpdateRequestDTO;
import com.itextpdf.dito.manager.dto.user.update.UsersActivateRequestDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.security.Principal;
import java.util.List;
import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping(UserController.BASE_NAME)
@Tag(name = "user", description = "user API")
public interface UserController {
    String MAJOR_VERSION = "/v1";
    String BASE_NAME = MAJOR_VERSION + "/users";
    String CURRENT_USER = "/me";

    // Endpoints
    String USERS_UNBLOCK_ENDPOINT = "/unblock";
    String USERS_ACTIVATION_ENDPOINT = "/update-activity";
    String CHANGE_PASSWORD_ENDPOINT = CURRENT_USER + "/change-password";
    String CURRENT_USER_INFO_ENDPOINT = CURRENT_USER + "/info";
    String UPDATE_USER_ENDPOINT = "/{email}"; // in base64 format

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
    @Operation(summary = "Get users list", description = "Get available users",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<Page<UserDTO>> list(Pageable pageable,
                                       @Parameter(description = "user name or email search string") @RequestParam(name = "search", required = false) String searchParam);

    @PatchMapping(USERS_ACTIVATION_ENDPOINT)
    @Operation(summary = "Activate(deactivate) users in batch", description = "Activate(deactivate) users in batch",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    ResponseEntity<Void> updateActivationStatus(@RequestBody @Valid UsersActivateRequestDTO activateRequest);

    @GetMapping(CURRENT_USER_INFO_ENDPOINT)
    @Operation(summary = "Get info about current user", description = "Get info about the user making request",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponse(responseCode = "200", description = "Successfully retrieved user data", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))})
    ResponseEntity<UserDTO> currentUser(Principal principal);

    @PatchMapping(USERS_UNBLOCK_ENDPOINT)
    @Operation(summary = "Unblock users", description = "Unblock users",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = {
                    @Content(schema = @Schema(implementation = UserDTO.class))}),
            @ApiResponse(responseCode = "404", description = "User(s) not found", content = @Content)
    })
    ResponseEntity<List<UserDTO>> unblock(@RequestBody UsersUnblockRequestDTO userUnblockRequestDTO);

    @PatchMapping(CURRENT_USER)
    @Operation(summary = "Update current user", description = "Update first and last name of the user making request",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponse(responseCode = "200", description = "Successfully updated user data", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))})
    ResponseEntity<UserDTO> updateCurrentUser(@RequestBody UserUpdateRequestDTO updateRequest, Principal principal);

    @PatchMapping(CHANGE_PASSWORD_ENDPOINT)
    @Operation(summary = "Change current user password", description = "Change current user password",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated password", content = @Content),
            @ApiResponse(responseCode = "400", description = "New password is same as old password", content = @Content),
    })
    ResponseEntity<Void> updatePassword(@RequestBody UpdatePasswordRequestDTO updatePasswordRequestDTO,
                                        Principal principal);

    @PatchMapping(UPDATE_USER_ENDPOINT)
    @Operation(summary = "Update some user", description = "Update user's related data",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<UserDTO> update(@PathVariable("email") final String email, @RequestBody UserDTO userDTO);
}

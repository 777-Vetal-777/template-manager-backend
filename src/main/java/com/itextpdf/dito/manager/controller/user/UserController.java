package com.itextpdf.dito.manager.controller.user;

import com.itextpdf.dito.manager.config.OpenApiConfig;
import com.itextpdf.dito.manager.dto.user.EmailDTO;
import com.itextpdf.dito.manager.dto.token.reset.ResetPasswordDTO;
import com.itextpdf.dito.manager.dto.user.UserDTO;
import com.itextpdf.dito.manager.dto.user.create.UserCreateRequestDTO;
import com.itextpdf.dito.manager.dto.user.unblock.UsersUnblockRequestDTO;
import com.itextpdf.dito.manager.dto.user.update.PasswordChangeRequestDTO;
import com.itextpdf.dito.manager.dto.user.update.UpdatePasswordRequestDTO;
import com.itextpdf.dito.manager.dto.user.update.UserRolesUpdateRequestDTO;
import com.itextpdf.dito.manager.dto.user.update.UserUpdateRequestDTO;
import com.itextpdf.dito.manager.dto.user.update.UsersActivateRequestDTO;
import com.itextpdf.dito.manager.filter.user.UserFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@RequestMapping(UserController.BASE_NAME)
@Tag(name = "user", description = "user API")
public interface UserController {
    String MAJOR_VERSION = "/v1";
    String BASE_NAME = MAJOR_VERSION + "/users";
    String CURRENT_USER = "/me";
    String USER_NAME_PATH_VARIABLE = "username";
    String CHANGE_PASSWORD = "/change-password";
    String UPDATE_PASSWORD = "/update-password";

    // Endpoints
    String USER_NAME_ENDPOINT_WITH_PATH_VARIABLE = "/{" + USER_NAME_PATH_VARIABLE + "}";
    String USERS_UNBLOCK_ENDPOINT = "/unblock";
    String USERS_ACTIVATION_ENDPOINT = "/update-activity";
    String CURRENT_USER_CHANGE_PASSWORD_ENDPOINT = CURRENT_USER + CHANGE_PASSWORD;
    String USER_CHANGE_PASSWORD_ENDPOINT = USER_NAME_ENDPOINT_WITH_PATH_VARIABLE + CHANGE_PASSWORD;
    String USER_UPDATE_PASSWORD_ENDPOINT = CURRENT_USER + UPDATE_PASSWORD;
    String CURRENT_USER_INFO_ENDPOINT = CURRENT_USER + "/info";
    String UPDATE_USERS_ROLES_ENDPOINT = "/roles";
    String FORGOT_PASSWORD = "/forgot-password";
    String RESET_PASSWORD = "/reset-password";

    @PostMapping
    @PreAuthorize("hasAuthority('E3_US10_CREATE_NEW_USER')")
    @Operation(summary = "Create user", description = "Create new user",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created new user", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input or user already exists", content = @Content),
    })
    ResponseEntity<UserDTO> create(@RequestBody UserCreateRequestDTO userCreateRequestDTO, Principal principal);

    @GetMapping(USER_NAME_ENDPOINT_WITH_PATH_VARIABLE)
    @PreAuthorize("hasAuthority('E3_US128_USER_DETAILS_PAGE')")
    @Operation(summary = "Get info about user by email", description = "Get information about a user using email as a key. Result - First name, last name, user roles. ",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user data", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))}),
            @ApiResponse(responseCode = "400", description = "User with such mail was not found", content = @Content)
    })
    ResponseEntity<UserDTO> get(@Parameter(name = USER_NAME_PATH_VARIABLE, description = "Encoded with base64 username", required = true) @PathVariable(USER_NAME_PATH_VARIABLE) String userName, Principal principal);

    @PatchMapping(USER_NAME_ENDPOINT_WITH_PATH_VARIABLE)
    @PreAuthorize("hasAuthority('E3_US128_USER_DETAILS_PAGE')")
    @Operation(summary = "Update specified user", description = "Update first and last name of the user making request",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponse(responseCode = "200", description = "Successfully updated user data", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))})
    ResponseEntity<UserDTO> update(@Parameter(name = USER_NAME_PATH_VARIABLE, description = "Encoded with base64 username", required = true) @PathVariable(USER_NAME_PATH_VARIABLE) String userName,
            @RequestBody UserUpdateRequestDTO userUpdateRequestDTO);

    @PatchMapping(USER_CHANGE_PASSWORD_ENDPOINT)
    @PreAuthorize("hasAuthority('E3_US128_USER_DETAILS_PAGE')")
    @Operation(summary = "Change specified user password", description = "Change specified user password",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated password", content = @Content),
            @ApiResponse(responseCode = "400", description = "New password is same as old password", content = @Content),
    })
    ResponseEntity<UserDTO> updatePassword(@Parameter(name = USER_NAME_PATH_VARIABLE, description = "Encoded with base64 username", required = true) @PathVariable(USER_NAME_PATH_VARIABLE) String userName,
            @RequestBody UpdatePasswordRequestDTO updatePasswordRequestDTO);

    @GetMapping
    @PreAuthorize("hasAnyAuthority('E3_US_9_USERS_TABLE', 'E2_US6_SETTINGS_PANEL')")
    @Operation(summary = "Get users list", description = "Get available users",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<Page<UserDTO>> list(Pageable pageable,
                                       @ParameterObject UserFilter userFilter,
                                       @Parameter(description = "user name or email search string") @RequestParam(name = "search", required = false) String searchParam);

    @PatchMapping(USERS_ACTIVATION_ENDPOINT)
    @PreAuthorize("hasAuthority('E3_US12_DEACTIVATE_USER')")
    @Operation(summary = "Activate(deactivate) users in batch", description = "Activate(deactivate) users in batch",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    ResponseEntity<Void> updateActivationStatus(@RequestBody UsersActivateRequestDTO usersActivateRequestDTO);

    @GetMapping(CURRENT_USER_INFO_ENDPOINT)
    @PreAuthorize("hasAnyAuthority('E10_US85_USER_PROFILE', 'E2_US5_HEADER_PANEL', 'E2_US6_SETTINGS_PANEL', 'E10_US87_PERSONAL_PREFERENCES')")
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
    ResponseEntity<List<UserDTO>> unblock(@RequestBody UsersUnblockRequestDTO usersUnblockRequestDTO);

    @PatchMapping(CURRENT_USER)
    @Operation(summary = "Update current user", description = "Update first and last name of the user making request",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponse(responseCode = "200", description = "Successfully updated user data", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))})
    ResponseEntity<UserDTO> updateCurrentUser(@RequestBody UserUpdateRequestDTO userUpdateRequestDTO, Principal principal);

    @PatchMapping(CURRENT_USER_CHANGE_PASSWORD_ENDPOINT)
    @PreAuthorize("hasAnyAuthority('E1_US3_FORGOT_PASSWORD', 'E10_US86_CHANGE_PASSWORD', 'E2_US6_SETTINGS_PANEL')")
    @Operation(summary = "Change current user password", description = "Change current user password",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated password", content = @Content),
            @ApiResponse(responseCode = "400", description = "New password is same as old password", content = @Content),
    })
    ResponseEntity<UserDTO> updatePassword(@RequestBody PasswordChangeRequestDTO passwordChangeRequestDTO, Principal principal);

    @PatchMapping(USER_UPDATE_PASSWORD_ENDPOINT)
    @PreAuthorize("hasAnyAuthority('E1_US3_FORGOT_PASSWORD', 'E10_US86_CHANGE_PASSWORD', 'E2_US6_SETTINGS_PANEL')")
    @Operation(summary = "Change the admin password to the custom", description = "Update the password specified by the administrator for the password that the user wants ",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated password", content = @Content),
            @ApiResponse(responseCode = "400", description = "New password is same as old password", content = @Content),
    })
    ResponseEntity<UserDTO> updateAdminPasswordToUser(@RequestBody UpdatePasswordRequestDTO updatePasswordRequestDTO, Principal principal);

    @PatchMapping(UPDATE_USERS_ROLES_ENDPOINT)
    @PreAuthorize("hasAuthority('E3_US11_CHANGE_ROLE_TO_THE_USER')")
    @Operation(summary = "Update users' roles", description = "Update users' roles",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<List<UserDTO>> updateUsersRoles(
            @RequestBody final UserRolesUpdateRequestDTO userRolesUpdateRequestDTO);

    @PatchMapping(FORGOT_PASSWORD)
    @Operation(summary = "Forgot user`s password", description = "Forgot user`s password",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully sent link to email", content = @Content)
    })
    ResponseEntity<Void> forgotPassword(@RequestBody EmailDTO emailDTO);

    @PatchMapping(RESET_PASSWORD)
    @Operation(summary = "Reset user`s password", description = "Reset user`s password",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully updated password", content = @Content),
            @ApiResponse(responseCode = "409", description = "Token is not valid", content = @Content)
    })
    ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO);
}

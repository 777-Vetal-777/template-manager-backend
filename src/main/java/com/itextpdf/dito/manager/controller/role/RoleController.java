package com.itextpdf.dito.manager.controller.role;

import com.itextpdf.dito.manager.config.OpenApiConfig;
import com.itextpdf.dito.manager.dto.role.RoleDTO;
import com.itextpdf.dito.manager.dto.role.create.RoleCreateRequestDTO;
import com.itextpdf.dito.manager.dto.role.update.RoleUpdateRequestDTO;
import com.itextpdf.dito.manager.filter.role.RoleFilter;
import com.itextpdf.dito.manager.filter.role.RoleUserFilter;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping(RoleController.BASE_NAME)
@Tag(name = "role", description = "role API")
public interface RoleController {
    String MAJOR_VERSION = "/v1";
    String BASE_NAME = MAJOR_VERSION + "/roles";
    String ROLE_USER_SEARCH =  "/user-search";

    String ROLE_PATH_VARIABLE = "name";
    String ROLE_ENDPOINT_WITH_PATH_VARIABLE = "/{" + ROLE_PATH_VARIABLE + "}";

    @PostMapping
    @PreAuthorize("hasAuthority('E3_US14_CREATE_SECURITY_ROLE')")
    @Operation(summary = "Create role", description = "Create new role",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created new role", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = RoleCreateRequestDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input or role already exists", content = @Content),
    })
    ResponseEntity<RoleDTO> create(@RequestBody RoleCreateRequestDTO roleCreateRequestDTO);

    @PatchMapping(ROLE_ENDPOINT_WITH_PATH_VARIABLE)
    @PreAuthorize("hasAnyAuthority('E3_US16_EDIT_CUSTOM_ROLES', 'E3_US11_CHANGE_ROLE_TO_THE_USER')")
    @Operation(summary = "Edit custom role", description = "Edit custom security role",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully updated custom role", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = RoleCreateRequestDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input or role already exists", content = @Content),
    })
    ResponseEntity<RoleDTO> update(@Parameter(description = "Encoded with base64 role name, to be updated") @PathVariable String name,
            @RequestBody RoleUpdateRequestDTO roleUpdateRequestDTO);

    @GetMapping
    @PreAuthorize("hasAuthority('E3_US13_SECURITY_ROLES_TABLE')")
    @Operation(summary = "Get role list", description = "Get available roles",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<Page<RoleDTO>> list(Pageable pageable, @ParameterObject RoleFilter roleFilter,
            @Parameter(description = "role name, role type, user name or permission name search string") @RequestParam(name = "search", required = false) String searchParam);

    @GetMapping(ROLE_USER_SEARCH)
    @PreAuthorize("hasAuthority('E3_US13_SECURITY_ROLES_TABLE')")
    @Operation(summary = "Get role list by search of users", description = "Get role list by search of users",
    security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<Page<RoleDTO>> list(Pageable pageable, @ParameterObject RoleUserFilter roleFilter,
                                       @Parameter(description = "role name, role type, user name or permission name search string") @RequestParam(name = "search", required = false) String searchParam);

    @DeleteMapping(ROLE_ENDPOINT_WITH_PATH_VARIABLE)
    @PreAuthorize("hasAuthority('E3_US17_DELETE_SECURITY_ROLE')")
    @Operation(summary = "Delete role", description = "Delete role",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = {
                    @Content(schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "404", description = "Role not found", content = @Content),
            @ApiResponse(responseCode = "422", description = "Role has connected users", content = @Content)
    })
    ResponseEntity<Void> delete(@Parameter(description = "Encoded with base64role name, to be deleted") @PathVariable String name);


}

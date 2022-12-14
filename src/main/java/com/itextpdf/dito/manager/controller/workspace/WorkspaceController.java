package com.itextpdf.dito.manager.controller.workspace;

import com.itextpdf.dito.manager.config.OpenApiConfig;
import com.itextpdf.dito.manager.dto.license.LicenseDTO;
import com.itextpdf.dito.manager.dto.promotionpath.PromotionPathDTO;
import com.itextpdf.dito.manager.dto.workspace.WorkspaceDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.security.Principal;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping(WorkspaceController.BASE_NAME)
@Tag(name = "workspace", description = "workspace API")
public interface WorkspaceController {
    String MAJOR_VERSION = "/v1";
    String BASE_NAME = MAJOR_VERSION + "/workspaces";

    String WORKSPACE_PATH_VARIABLE = "name";
    String WORKSPACE_ENDPOINT_WITH_PATH_VARIABLE = "/{" + WORKSPACE_PATH_VARIABLE + "}";
    String WORKSPACE_PROMOTION_PATH_ENDPOINT = WORKSPACE_ENDPOINT_WITH_PATH_VARIABLE + "/promotion-path";
    String WORKSPACE_STAGES_ENDPOINT = WORKSPACE_ENDPOINT_WITH_PATH_VARIABLE + "/stages";
    String WORKSPACE_LICENSE_ENDPOINT = WORKSPACE_ENDPOINT_WITH_PATH_VARIABLE + "/license";
    String WORKSPACE_CHECK_LICENSE_ENDPOINT = "/check-license";
    String WORKSPACE_NAME_AVAILABLE_ENDPOINTS = WORKSPACE_ENDPOINT_WITH_PATH_VARIABLE + "/exist";

    @GetMapping(WORKSPACE_NAME_AVAILABLE_ENDPOINTS)
    @PreAuthorize("hasAnyAuthority('E4_US18_WORKSPACE_DEFAULT_SETTINGS', 'E4_US23_WORKSPACE_SET_UP_WIZARD')")
    @Operation(summary = "Get all workspaces", description = "Get workspaces", security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponse(responseCode = "200", description = "Answer to a request if workspace can be used ", content = @Content)
    ResponseEntity<Boolean> checkIsWorkspaceWithNameExist(@NotBlank @PathVariable(WORKSPACE_PATH_VARIABLE) String name);

    @PostMapping(path = WORKSPACE_CHECK_LICENSE_ENDPOINT, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('E4_US18_WORKSPACE_DEFAULT_SETTINGS', 'E4_US23_WORKSPACE_SET_UP_WIZARD','E4_US19_UPLOAD_LICENSE')")
    @Operation(summary = "Check the license for validity.", security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponse(responseCode = "200", description = "Successfully uploaded")
    ResponseEntity<Boolean> checkLicenseValidity(@Parameter(description = "license XML file", required = true, style = ParameterStyle.FORM) @RequestPart("license") MultipartFile multipartFile, Principal principal);

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('E4_US18_WORKSPACE_DEFAULT_SETTINGS', 'E4_US23_WORKSPACE_SET_UP_WIZARD')")
    @Operation(summary = "Create workspace", description = "Create new workspace",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created new workspace", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = WorkspaceDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "409", description = "Workspace already exist.", content = @Content),
    })
    ResponseEntity<WorkspaceDTO> create(
            @Parameter(description = "Workspace name", required = true) @RequestPart(name = "name", required = false) String name,
            @Parameter(description = "Workspace timezone", required = true) @RequestPart(name = "timezone", required = false) String timezone,
            @Parameter(description = "Workspace language", required = true) @RequestPart(name = "language", required = false)  String language,
            @Parameter(description = "Adjust for daylight") @RequestPart(name = "adjustForDaylight", required = false) String adjustForDaylight,
            @Parameter(description = "Main develop stage instance", required = true) @RequestPart(name = "mainDevelopInstance", required = false) String mainDevelopInstance,
            @Parameter(description = "license XML file", required = true, style = ParameterStyle.FORM) @RequestPart("license") MultipartFile multipartFile,
            Principal principal);

    @GetMapping
    @Operation(summary = "Get all workspaces", description = "Get workspaces",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<List<WorkspaceDTO>> getAll();

    @GetMapping(WORKSPACE_ENDPOINT_WITH_PATH_VARIABLE)
    @PreAuthorize("hasAuthority('E2_US5_HEADER_PANEL')")
    @Operation(summary = "Get workspace", description = "Get workspace",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<WorkspaceDTO> get(@NotBlank @PathVariable(WORKSPACE_PATH_VARIABLE) String name);

    @PatchMapping(WORKSPACE_ENDPOINT_WITH_PATH_VARIABLE)
    @Operation(summary = "Update workspace", description = "Update workspace",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<WorkspaceDTO> update(@NotBlank @PathVariable(WORKSPACE_PATH_VARIABLE) String name,
            @Valid @RequestBody WorkspaceDTO workspaceDTO);

    @GetMapping(WORKSPACE_PROMOTION_PATH_ENDPOINT)
    @PreAuthorize("hasAuthority('E4_US22_PROMOTION_PATH')")
    @Operation(summary = "Promotion path", description = "Retrieve full promotion path",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<PromotionPathDTO> getPromotionPath(@PathVariable(WORKSPACE_PATH_VARIABLE) String workspaceName);

    @PatchMapping(WORKSPACE_PROMOTION_PATH_ENDPOINT)
    @PreAuthorize("hasAuthority('E4_US22_PROMOTION_PATH')")
    @Operation(summary = "Update promotion path", description = "Update promotion path",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<PromotionPathDTO> updatePromotionPath(@PathVariable(WORKSPACE_PATH_VARIABLE) String workspaceName,
            @Valid @RequestBody PromotionPathDTO promotionPathDTO);

    @GetMapping(WORKSPACE_STAGES_ENDPOINT)
    @Operation(summary = "List of stages for workspace", description = "Retrieve list of stage names for workspace",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<List<String>> getStageNames(@PathVariable(WORKSPACE_PATH_VARIABLE) String workspaceName);
    
	@PostMapping(path = WORKSPACE_LICENSE_ENDPOINT, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasAuthority('E4_US19_UPLOAD_LICENSE')")
	@Operation(summary = "Upload license", security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successfully uploaded", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = LicenseDTO.class)) }),
			@ApiResponse(responseCode = "400", description = "Invalid license", content = @Content),
			@ApiResponse(responseCode = "400", description = "License's file couldn't be empty"),
			@ApiResponse(responseCode = "400", description = "License's file is unreadable") })
	ResponseEntity<LicenseDTO> uploadLicense(
			@Parameter(description = "Workspace name encoded with base64.", required = true) @PathVariable(WORKSPACE_PATH_VARIABLE) String workspaceName,
			@Parameter(description = "license XML file", required = true, style = ParameterStyle.FORM) @RequestPart("license") MultipartFile multipartFile,
			Principal principal);

	@GetMapping(path = WORKSPACE_LICENSE_ENDPOINT)
	@PreAuthorize("hasAuthority('E4_US20_SUBSCRIPTION_PLAN')")
	@Operation(summary = "Get license information", security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Success", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = LicenseDTO.class)) }),
			@ApiResponse(responseCode = "400", description = "Invalid license", content = @Content),
			@ApiResponse(responseCode = "404", description = "No licenses found", content = @Content)})
	ResponseEntity<LicenseDTO> getLicense(
			@Parameter(description = "Workspace name encoded with base64.", required = true) @PathVariable(WORKSPACE_PATH_VARIABLE) String workspaceName,
			Principal principal);

}

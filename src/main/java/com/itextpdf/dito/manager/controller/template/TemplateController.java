package com.itextpdf.dito.manager.controller.template;

import com.itextpdf.dito.manager.config.OpenApiConfig;
import com.itextpdf.dito.manager.dto.dependency.DependencyDTO;
import com.itextpdf.dito.manager.dto.dependency.filter.DependencyFilter;
import com.itextpdf.dito.manager.dto.file.FileVersionDTO;
import com.itextpdf.dito.manager.dto.resource.update.ApplyRoleRequestDTO;
import com.itextpdf.dito.manager.dto.stage.StageDTO;
import com.itextpdf.dito.manager.dto.template.TemplateDTO;
import com.itextpdf.dito.manager.dto.template.TemplateMetadataDTO;
import com.itextpdf.dito.manager.dto.template.TemplatePermissionDTO;
import com.itextpdf.dito.manager.dto.template.TemplateWithSettingsDTO;
import com.itextpdf.dito.manager.dto.template.create.TemplateCreateRequestDTO;
import com.itextpdf.dito.manager.dto.template.create.TemplatePartDTO;
import com.itextpdf.dito.manager.dto.template.setting.TemplateImportSettingDTO;
import com.itextpdf.dito.manager.dto.template.update.TemplateUpdateRequestDTO;
import com.itextpdf.dito.manager.dto.template.version.TemplateDeployedVersionDTO;
import com.itextpdf.dito.manager.entity.TemplateTypeEnum;
import com.itextpdf.dito.manager.filter.template.TemplateFilter;
import com.itextpdf.dito.manager.filter.template.TemplateListFilter;
import com.itextpdf.dito.manager.filter.template.TemplatePermissionFilter;
import com.itextpdf.dito.manager.filter.version.VersionFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RequestMapping(TemplateController.BASE_NAME)
@Tag(name = "template", description = "templates API")
public interface TemplateController {
    String MAJOR_VERSION = "/v1";
    String BASE_NAME = MAJOR_VERSION + "/templates";

    String TEMPLATE_TYPES_ENDPOINT = "/types";
    String TEMPLATE_PATH_VARIABLE = "template-name";
    String TEMPLATE_VERSION_PATH_VARIABLE = "template-version";
    String ROLE_PATH_VARIABLE = "role-name";
    String TEMPLATE_VERSION_ENDPOINT = "/versions";
    String TEMPLATE_PARTS_ENDPOINT = "/parts";
    String TEMPLATE_BLOCK_ENDPOINT = "/block";
    String TEMPLATE_UNBLOCK_ENDPOINT = "/unblock";
    String PAGEABLE_ENDPOINT = "/pageable";
    String PROMOTE_ENDPOINT = "/promote";
    String UNDEPLOY_ENDPOINT = "/undeploy";
    String SEARCH_ENDPOINT = "/search";
    String ROLLBACK_ENDPOINT = "/rollback";
    String NEXT_STAGE_ENDPOINT = "/next-stage";
    String EXPORT_ENDPOINT = "/export";

    String TEMPLATE_ENDPOINT_WITH_PATH_VARIABLE = "/{" + TEMPLATE_PATH_VARIABLE + "}";
    String TEMPLATE_IMPORT_ENDPOINT = "/import";
    String TEMPLATE_EXPORT_ENDPOINT_WITH_PATH_VARIABLE = TEMPLATE_ENDPOINT_WITH_PATH_VARIABLE + EXPORT_ENDPOINT;
    String VERSION_ENDPOINT_WITH_PATH_VARIABLE = "/{" + TEMPLATE_VERSION_PATH_VARIABLE + "}";
    String TEMPLATE_BLOCK_ENDPOINT_WITH_PATH_VARIABLE = TEMPLATE_ENDPOINT_WITH_PATH_VARIABLE + TEMPLATE_BLOCK_ENDPOINT;
    String TEMPLATE_UNBLOCK_ENDPOINT_WITH_PATH_VARIABLE = TEMPLATE_ENDPOINT_WITH_PATH_VARIABLE + TEMPLATE_UNBLOCK_ENDPOINT;
    String TEMPLATE_PROMOTE_ENDPOINT_WITH_PATH_VARIABLE = TEMPLATE_ENDPOINT_WITH_PATH_VARIABLE + VERSION_ENDPOINT_WITH_PATH_VARIABLE + PROMOTE_ENDPOINT;
    String TEMPLATE_UNDEPLOY_ENDPOINT_WITH_PATH_VARIABLE = TEMPLATE_ENDPOINT_WITH_PATH_VARIABLE + VERSION_ENDPOINT_WITH_PATH_VARIABLE + UNDEPLOY_ENDPOINT;
    String TEMPLATE_NEXT_STAGE_ENDPOINT_WITH_PATH_VARIABLE = TEMPLATE_ENDPOINT_WITH_PATH_VARIABLE + VERSION_ENDPOINT_WITH_PATH_VARIABLE + NEXT_STAGE_ENDPOINT;
    String TEMPLATE_ROLLBACK_ENDPOINT_WITH_PATH_VARIABLE = TEMPLATE_ENDPOINT_WITH_PATH_VARIABLE + VERSION_ENDPOINT_WITH_PATH_VARIABLE + ROLLBACK_ENDPOINT;
    //Dependencies
    String TEMPLATE_DEPENDENCIES_ENDPOINT_WITH_PATH_VARIABLE = TEMPLATE_ENDPOINT_WITH_PATH_VARIABLE + "/dependencies";
    String TEMPLATE_DEPENDENCIES_PAGEABLE_ENDPOINT_WITH_PATH_VARIABLE = TEMPLATE_DEPENDENCIES_ENDPOINT_WITH_PATH_VARIABLE + PAGEABLE_ENDPOINT;
    String TEMPLATE_VERSION_ENDPOINT_WITH_PATH_VARIABLE = TEMPLATE_ENDPOINT_WITH_PATH_VARIABLE + TEMPLATE_VERSION_ENDPOINT;
    String TEMPLATE_PARTS_ENDPOINT_WITH_PATH_VARIABLE = TEMPLATE_ENDPOINT_WITH_PATH_VARIABLE + TEMPLATE_PARTS_ENDPOINT;
    String TEMPLATE_PREVIEW_ENDPOINT_WITH_PATH_VARIABLE = TEMPLATE_ENDPOINT_WITH_PATH_VARIABLE + "/preview";
    String TEMPLATE_ROLES_ENDPOINT_WITH_PATH_VARIABLE = TEMPLATE_ENDPOINT_WITH_PATH_VARIABLE + "/roles";
    String TEMPLATE_ROLES_ENDPOINT_WITH_PATH_VARIABLE_AND_ROLE_NAME = TEMPLATE_ROLES_ENDPOINT_WITH_PATH_VARIABLE + "/{" + ROLE_PATH_VARIABLE + "}";

    @PostMapping
    @PreAuthorize("@permissionHandlerImpl.checkTemplateCommonPermissionByType(authentication, #templateCreateRequestDTO)")
    @Operation(summary = "Create template", description = "Create new template",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = TemplateDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input or template already exists", content = @Content)})
    ResponseEntity<TemplateDTO> create(@RequestBody TemplateCreateRequestDTO templateCreateRequestDTO,
                                       Principal principal);

    @GetMapping(value = TEMPLATE_PARTS_ENDPOINT_WITH_PATH_VARIABLE)
    @PreAuthorize("hasAnyAuthority('E9_US70_TEMPLATES_TABLE', 'E9_US74_VIEW_TEMPLATE_METADATA_STANDARD')")
    @Operation(summary = "Get nested templates in template by name.", security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<List<TemplateWithSettingsDTO>> listCompositionTemplates(@Parameter(description = "The name of the template, which will be used to search for parts. ") @PathVariable(TEMPLATE_PATH_VARIABLE) String name, Principal principal);

    @GetMapping
    @PreAuthorize("hasAnyAuthority('E9_US70_TEMPLATES_TABLE', 'E9_US71_TEMPLATE_NAVIGATION_MENU_STANDARD')")
    @Operation(summary = "Get template list", description = "Get templates",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<Page<TemplateDTO>> listTemplates(Pageable pageable,
                                                    @ParameterObject TemplateFilter templateFilter,
                                                    @Parameter(description = "search by template fields") @RequestParam(name = "search", required = false) String searchParam,
                                                    Principal principal);

    @GetMapping(SEARCH_ENDPOINT)
    @PreAuthorize("hasAnyAuthority('E9_US70_TEMPLATES_TABLE', 'E9_US71_TEMPLATE_NAVIGATION_MENU_STANDARD')")
    @Operation(summary = "Get template list", description = "Get templates, return all templates satisfying the filter",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<List<TemplateDTO>> listTemplates(@ParameterObject TemplateListFilter templateListFilter, Principal principal);

    @GetMapping(TEMPLATE_TYPES_ENDPOINT)
    @Operation(summary = "Get template type list", description = "Get all template types",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<TemplateTypeEnum[]> listTemplateTypes();

    @GetMapping(TEMPLATE_DEPENDENCIES_ENDPOINT_WITH_PATH_VARIABLE)
    @PreAuthorize("hasAnyAuthority('E9_US84_TABLE_OF_TEMPLATE_DEPENDENCIES', 'E9_US71_TEMPLATE_NAVIGATION_MENU_STANDARD')")
    @Operation(summary = "Get dependencies list", description = "Retrieving list of information about template dependencies",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponse(responseCode = "200", description = "Information about template dependencies")
    ResponseEntity<List<DependencyDTO>> listDependencies(
            @Parameter(description = "Encoded with base64 template name", required = true) @PathVariable(TEMPLATE_PATH_VARIABLE) String name);

    @GetMapping(TEMPLATE_DEPENDENCIES_PAGEABLE_ENDPOINT_WITH_PATH_VARIABLE)
    @PreAuthorize("hasAnyAuthority('E9_US84_TABLE_OF_TEMPLATE_DEPENDENCIES', 'E9_US71_TEMPLATE_NAVIGATION_MENU_STANDARD')")
    @Operation(summary = "Get dependencies list", description = "Retrieving list of information about dependencies using sorting and filters.",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponse(responseCode = "200", description = "Information about dependencies is prepared according to the specified conditions.")
    ResponseEntity<Page<DependencyDTO>> listDependenciesPageable(
            @Parameter(description = "Encoded with base64 template name", required = true) @PathVariable(TEMPLATE_PATH_VARIABLE) String name,
            Pageable pageable,
            @ParameterObject DependencyFilter dependencyFilter,
            @Parameter(description = "Universal search string which filter dependencies names.")
            @RequestParam(name = "search", required = false) String searchParam);

    @GetMapping(TEMPLATE_ENDPOINT_WITH_PATH_VARIABLE)
    @PreAuthorize("hasAnyAuthority('E9_US74_VIEW_TEMPLATE_METADATA_STANDARD', 'E9_US71_TEMPLATE_NAVIGATION_MENU_STANDARD')")
    @Operation(summary = "Get template metadata", description = "Get template metadata",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<TemplateMetadataDTO> get(
            @Parameter(description = "Template name encoded with base64.") @PathVariable(TEMPLATE_PATH_VARIABLE) String name,
            Principal principal);

    @PatchMapping(TEMPLATE_ENDPOINT_WITH_PATH_VARIABLE)
    @PreAuthorize("@permissionHandlerImpl.checkTemplatePermissions(authentication, @permissionHandlerImpl.decodeBase64(#name), 'E9_US75_EDIT_TEMPLATE_METADATA_STANDARD')")
    @Operation(summary = "Update template metadata", description = "Update template metadata",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<TemplateMetadataDTO> update(
            @Parameter(description = "Template name encoded with base64.") @PathVariable(TEMPLATE_PATH_VARIABLE) String name,
            @RequestBody TemplateUpdateRequestDTO templateUpdateRequestDTO,
            Principal principal);

    @GetMapping(TEMPLATE_VERSION_ENDPOINT_WITH_PATH_VARIABLE)
    @PreAuthorize("hasAnyAuthority('E9_US78_TEMPLATE_VERSIONS_HISTORY_STANDARD', 'E9_US71_TEMPLATE_NAVIGATION_MENU_STANDARD')")
    @Operation(summary = "Get a list of versions of template by name.", description = "Get a list of template versions using the template name and template type, sorting and filters.",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<Page<FileVersionDTO>> getVersions(Pageable pageable,
                                                     @Parameter(description = "Encoded with base64 template name", required = true) @PathVariable(TEMPLATE_PATH_VARIABLE) String name,
                                                     @ParameterObject VersionFilter versionFilter,
                                                     @Parameter(description = "Universal search string.") @RequestParam(name = "search", required = false) String searchParam);

    @GetMapping(TEMPLATE_PREVIEW_ENDPOINT_WITH_PATH_VARIABLE)
    @PreAuthorize("@permissionHandlerImpl.checkTemplatePermissions(authentication, @permissionHandlerImpl.decodeBase64(#templateName), 'E9_US81_PREVIEW_TEMPLATE_STANDARD')")
    @Operation(summary = "Get template preview", description = "Get generated template PDF preview",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponse(responseCode = "200", description = "Generated template PDF preview")
    ResponseEntity<byte[]> preview(@Parameter(description = "Encoded with base64 template name", required = true) @PathVariable(TEMPLATE_PATH_VARIABLE) String templateName,
                                   @Parameter(description = "Optional name of date sample for generating pdf preview. ") @RequestParam(name = "dataSampleName", required = false) String dataSampleName);

    @PostMapping(value = TEMPLATE_VERSION_ENDPOINT, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("@permissionHandlerImpl.checkTemplateCreateVersionPermission(authentication, #name)")
    @Operation(summary = "Create new version of template", description = "Make a new version of a template: upload a new template file and a comment for the new version.",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Template new version saved successfully."),
            @ApiResponse(responseCode = "400", description = "Template file exceeds the file limit."),
            @ApiResponse(responseCode = "400", description = "The file cannot be read."),
            @ApiResponse(responseCode = "400", description = "File extension not supported.")
    })
    ResponseEntity<TemplateDTO> createVersion(Principal principal,
                                              @Parameter(name = "name", description = "Name of an existing template", required = true, style = ParameterStyle.FORM) @RequestPart String name,
                                              @Parameter(name = "comment", description = "Comment on the new version of the template", style = ParameterStyle.FORM) @RequestPart(required = false) String comment,
                                              @Parameter(name = "templateParts", description = "parts of composition templates that should be in new version", style = ParameterStyle.FORM) @RequestPart(required = false) List<TemplatePartDTO> templateParts,
                                              @Parameter(name = "template", description = "Template file", style = ParameterStyle.FORM) @RequestPart(value = "template", required = false) MultipartFile templateFile);

    @GetMapping(TEMPLATE_ROLES_ENDPOINT_WITH_PATH_VARIABLE)
    @PreAuthorize("hasAnyAuthority('E9_US82_TABLE_OF_TEMPLATE_PERMISSIONS_STANDARD', 'E9_US71_TEMPLATE_NAVIGATION_MENU_STANDARD')")
    @Operation(summary = "Get template's roles", description = "Retrieved attached roles.", security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<Page<TemplatePermissionDTO>> getRoles(Pageable pageable,
                                                         @Parameter(description = "Encoded with base64 template name", required = true) @PathVariable(TEMPLATE_PATH_VARIABLE) String name,
                                                         @ParameterObject TemplatePermissionFilter templatePermissionFilter,
                                                         @Parameter(description = "Universal search string.") @RequestParam(name = "search", required = false) String searchParam);

    @PostMapping(TEMPLATE_ROLES_ENDPOINT_WITH_PATH_VARIABLE)
    @PreAuthorize("hasAuthority('E9_US83_MANAGE_TEMPLATE_PERMISSIONS')")
    @Operation(summary = "Add/update role to a template", description = "Apply custom role to a template", security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role applied to template successfully."),
            @ApiResponse(responseCode = "401", description = "Template is not found in repository")
    })
    ResponseEntity<TemplateMetadataDTO> applyRole(Principal principal,
                                                  @Parameter(name = "template-name", description = "Encoded with base64 new name of template", required = true) @PathVariable(TEMPLATE_PATH_VARIABLE) String name,
                                                  @RequestBody ApplyRoleRequestDTO applyRoleRequestDTO);

    @DeleteMapping(TEMPLATE_ROLES_ENDPOINT_WITH_PATH_VARIABLE_AND_ROLE_NAME)
    @PreAuthorize("hasAuthority('E9_US83_MANAGE_TEMPLATE_PERMISSIONS')")
    @Operation(summary = "Remove role from a template", description = "Detach custom role from a template", security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role detached from template successfully."),
            @ApiResponse(responseCode = "401", description = "Role or template are not found in repository")
    })
    ResponseEntity<TemplateMetadataDTO> deleteRole(Principal principal,
                                                   @Parameter(name = "template-name", description = "Encoded with base64 new name of template", required = true) @PathVariable(TEMPLATE_PATH_VARIABLE) String name,
                                                   @Parameter(name = "role-name", description = "Encoded with base64 role name", required = true) @PathVariable(ROLE_PATH_VARIABLE) String roleName);

    @PatchMapping(TEMPLATE_BLOCK_ENDPOINT_WITH_PATH_VARIABLE)
    @Operation(summary = "Block template for editing", description = "Block template for editing by current user", security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Template blocked for editing successfully."),
            @ApiResponse(responseCode = "404", description = "Template not found."),
            @ApiResponse(responseCode = "403", description = "Template already blocked")
    })
    ResponseEntity<TemplateMetadataDTO> block(Principal principal, @Parameter(name = "template-name", description = "Encoded with base64 new name of template", required = true) @PathVariable(TEMPLATE_PATH_VARIABLE) String name);

    @PatchMapping(TEMPLATE_UNBLOCK_ENDPOINT_WITH_PATH_VARIABLE)
    @Operation(summary = "Unblock template for editing", description = "Unblock template for editing by current user", security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Template unblocked for editing successfully."),
            @ApiResponse(responseCode = "404", description = "Template not found."),
            @ApiResponse(responseCode = "403", description = "Template can't be unblocked")
    })
    ResponseEntity<TemplateMetadataDTO> unblock(Principal principal, @Parameter(name = "template-name", description = "Encoded with base64 new name of template", required = true) @PathVariable(TEMPLATE_PATH_VARIABLE) String name);

    @PutMapping(TEMPLATE_PROMOTE_ENDPOINT_WITH_PATH_VARIABLE)
    @Operation(summary = "Promote template version to next stage", description = "Promote template version to next stage",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Template version promoted successfully."),
            @ApiResponse(responseCode = "404", description = "Template version not found."),
            @ApiResponse(responseCode = "403", description = "Template already blocked")
    })
    ResponseEntity<List<TemplateDeployedVersionDTO>> promote(
            @Parameter(description = "Encoded with base64 template name", required = true) @PathVariable(TEMPLATE_PATH_VARIABLE) String name,
            @Parameter(description = "Template version number", required = true) @PathVariable(TEMPLATE_VERSION_PATH_VARIABLE) Long templateVersion);

    @GetMapping(TEMPLATE_NEXT_STAGE_ENDPOINT_WITH_PATH_VARIABLE)
    @Operation(summary = "Get next stage for template version", description = "Get next stage for template version",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Next stage received."),
            @ApiResponse(responseCode = "404", description = "No further stage of promotion path exists.")
    })
    ResponseEntity<StageDTO> getNextStage(
            @Parameter(description = "Encoded with base64 template name", required = true) @PathVariable(TEMPLATE_PATH_VARIABLE) String templateName,
            @Parameter(description = "Template version number", required = true) @PathVariable(TEMPLATE_VERSION_PATH_VARIABLE) Long templateVersion);


    @PutMapping(TEMPLATE_UNDEPLOY_ENDPOINT_WITH_PATH_VARIABLE)
    @Operation(summary = "Undeploy template version from promotion path", description = "Undeploy template version from promotion path",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Template version undeployed successfully."),
            @ApiResponse(responseCode = "404", description = "Template version not found.")
    })
    ResponseEntity<List<TemplateDeployedVersionDTO>> undeploy(
            @Parameter(description = "Encoded with base64 template name", required = true) @PathVariable(TEMPLATE_PATH_VARIABLE) String templateName,
            @Parameter(description = "Template version number", required = true) @PathVariable(TEMPLATE_VERSION_PATH_VARIABLE) Long templateVersion);

    @PostMapping(TEMPLATE_ROLLBACK_ENDPOINT_WITH_PATH_VARIABLE)
    @PreAuthorize("@permissionHandlerImpl.checkTemplateRollbackPermissions(#principal.name, @permissionHandlerImpl.decodeBase64(#templateName))")
    @Operation(summary = "Rollback template to selected version", description = "Rollback template to selected version",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Template rollback was successful."),
            @ApiResponse(responseCode = "404", description = "Template or template version not found.")
    })
    ResponseEntity<TemplateDTO> rollbackVersion(
            Principal principal,
            @Parameter(description = "Encoded with base64 template name", required = true) @PathVariable(TEMPLATE_PATH_VARIABLE) String templateName,
            @Parameter(description = "Template version number", required = true) @PathVariable(TEMPLATE_VERSION_PATH_VARIABLE) Long templateVersion);

    @PreAuthorize("@permissionHandlerImpl.checkTemplateDeletePermissions(authentication, @permissionHandlerImpl.decodeBase64(#templateName))")
    @DeleteMapping(TEMPLATE_ENDPOINT_WITH_PATH_VARIABLE)
    @Operation(summary = "Delete template", description = "Delete template by name - the template cannot be deleted if it has promoted versions or dependencies.",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Template successfully deleted.", content = @Content),
            @ApiResponse(responseCode = "409", description = "The template can't be deleted: it has outbound dependencies or promoted versions.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Template not found", content = @Content),
    })
    ResponseEntity<Void> delete(@Parameter(description = "Encoded with base64 template name", required = true) @PathVariable(TEMPLATE_PATH_VARIABLE) String templateName);

    @GetMapping(value = TEMPLATE_EXPORT_ENDPOINT_WITH_PATH_VARIABLE, produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("@permissionHandlerImpl.checkTemplatePermissions(authentication, @permissionHandlerImpl.decodeBase64(#templateName), 'E9_US24_EXPORT_TEMPLATE_DATA')")
    @Operation(summary = "Export template", description = "Export template as zipped DITO project",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exported template is in response."),
            @ApiResponse(responseCode = "404", description = "Template not found"),
            @ApiResponse(responseCode = "422", description = "Could not proceed with template export.")
    })
    ResponseEntity<byte[]> export(@Parameter(description = "Encoded with base64 template name", required = true) @PathVariable(TEMPLATE_PATH_VARIABLE) String templateName,
                                  @Parameter(description = "Export dependencies flag (default is true)") @RequestParam(value = "exportDependencies", required = false) Boolean dependenciesFlag);

    @PostMapping(value = TEMPLATE_IMPORT_ENDPOINT, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('E9_US25_IMPORT_TEMPLATE_DATA')")
    @Operation(summary = "Import template data", description = "Import template data. Creates new template or new version of existing template",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = TemplateDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)})
    ResponseEntity<TemplateDTO> importData(Principal principal,
                                           @Parameter(name = "template", description = "Template dito file", required = true, style = ParameterStyle.FORM) @RequestPart(value = "template") MultipartFile templateFile,
                                           @Parameter(name = "settings", description = "Name of data collections and renaming files", required = true, style = ParameterStyle.FORM) @RequestPart(value = "settings", required = false) List<TemplateImportSettingDTO> templateImportSettings);

}
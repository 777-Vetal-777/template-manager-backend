package com.itextpdf.dito.manager.controller.resource;

import com.itextpdf.dito.manager.config.OpenApiConfig;
import com.itextpdf.dito.manager.dto.dependency.DependencyDTO;
import com.itextpdf.dito.manager.dto.dependency.filter.DependencyFilter;
import com.itextpdf.dito.manager.dto.file.FileVersionDTO;
import com.itextpdf.dito.manager.dto.permission.ResourcePermissionDTO;
import com.itextpdf.dito.manager.dto.resource.ResourceDTO;
import com.itextpdf.dito.manager.dto.resource.update.ApplyRoleRequestDTO;
import com.itextpdf.dito.manager.dto.resource.update.ResourceUpdateRequestDTO;
import com.itextpdf.dito.manager.filter.resource.ResourceFilter;
import com.itextpdf.dito.manager.filter.resource.ResourcePermissionFilter;
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

@RequestMapping(ResourceController.BASE_NAME)
@Tag(name = "resource", description = "resource API")
public interface ResourceController {
    String MAJOR_VERSION = "/v1";
    String BASE_NAME = MAJOR_VERSION + "/resources";

    String PAGEABLE_ENDPOINT = "/pageable";

    String RESOURCE_PATH_VARIABLE = "resource-name";
    String RESOURCE_TYPE_PATH_VARIABLE = "resource-type";
    String ROLE_PATH_VARIABLE = "role-name";
    String VERSION_PATH_VARIABLE = "resource-version";
    String FILE_UUID_PATH_VARIABLE = "file-uuid";
    String RESOURCE_ENDPOINT_WITH_PATH_VARIABLE = "/{" + RESOURCE_PATH_VARIABLE + "}";
    String RESOURCE_ENDPOINT_WITH_RESOURCE_TYPE_VARIABLE = "/{" + RESOURCE_TYPE_PATH_VARIABLE + "}";
    String VERSION_ENDPOINT_WITH_PATH_VARIABLE = "/{" + VERSION_PATH_VARIABLE + "}";
    String RESOURCE_ENDPOINT_WITH_PATH_VARIABLE_AND_TYPE = RESOURCE_ENDPOINT_WITH_RESOURCE_TYPE_VARIABLE + RESOURCE_ENDPOINT_WITH_PATH_VARIABLE;
    String ROLE_ENDPOINT_WITH_PATH_VARIABLE = "/{" + ROLE_PATH_VARIABLE + "}";
    String RESOURCE_VERSION_ENDPOINT = "/versions";
    String RESOURCE_DEPENDENCIES_ENDPOINT = "/dependencies";
    String RESOURCE_APPLIED_ROLES_ENDPOINT = "/roles";
    String FONTS_ENDPOINT = "/fonts";
    String FILE_ENDPOINT = "/file";
    String RESOURCE_ROLLBACK_ENDPOINT = "/rollback";
    //File
    String RESOURCE_FILE_ENDPOINT_WITH_FILE_PATH_VARIABLE = FILE_ENDPOINT + "/{" + FILE_UUID_PATH_VARIABLE + "}";
    //Versions
    String RESOURCE_VERSION_ENDPOINT_WITH_PATH_VARIABLE = RESOURCE_ENDPOINT_WITH_RESOURCE_TYPE_VARIABLE + RESOURCE_ENDPOINT_WITH_PATH_VARIABLE + RESOURCE_VERSION_ENDPOINT;
    String RESOURCE_ROLLBACK_ENDPOINT_WITH_PATH_VARIABLE = RESOURCE_ENDPOINT_WITH_RESOURCE_TYPE_VARIABLE + RESOURCE_ENDPOINT_WITH_PATH_VARIABLE + VERSION_ENDPOINT_WITH_PATH_VARIABLE + RESOURCE_ROLLBACK_ENDPOINT;
    //Dependencies
    String RESOURCE_DEPENDENCIES_ENDPOINT_WITH_PATH_VARIABLE = RESOURCE_ENDPOINT_WITH_RESOURCE_TYPE_VARIABLE + RESOURCE_ENDPOINT_WITH_PATH_VARIABLE + RESOURCE_DEPENDENCIES_ENDPOINT;
    String RESOURCE_DEPENDENCIES_PAGEABLE_ENDPOINT_WITH_PATH_VARIABLE = RESOURCE_ENDPOINT_WITH_RESOURCE_TYPE_VARIABLE + RESOURCE_ENDPOINT_WITH_PATH_VARIABLE + RESOURCE_DEPENDENCIES_ENDPOINT + PAGEABLE_ENDPOINT;
    //Roles
    String RESOURCE_APPLIED_ROLES_ENDPOINT_WITH_RESOURCE_PATH_VARIABLE = RESOURCE_ENDPOINT_WITH_RESOURCE_TYPE_VARIABLE + RESOURCE_ENDPOINT_WITH_PATH_VARIABLE + RESOURCE_APPLIED_ROLES_ENDPOINT;
    String RESOURCE_APPLIED_ROLES_ENDPOINT_WITH_RESOURCE_AND_ROLE_PATH_VARIABLES = RESOURCE_APPLIED_ROLES_ENDPOINT_WITH_RESOURCE_PATH_VARIABLE + ROLE_ENDPOINT_WITH_PATH_VARIABLE;

    @GetMapping(RESOURCE_FILE_ENDPOINT_WITH_FILE_PATH_VARIABLE)
    @Operation(summary = "Get the file using uuid", description = "A method for getting a file as an array of bytes using the uuid identifier.",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The file was found and sent successfully."),
            @ApiResponse(responseCode = "400", description = "The file for the specified uuid was not found.")
    })
    ResponseEntity<byte[]> getFile(@Parameter(name = FILE_UUID_PATH_VARIABLE, description = "Encoded with base64 file uuid", required = true) @PathVariable(FILE_UUID_PATH_VARIABLE) String name);

    @PostMapping(path = FONTS_ENDPOINT, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("@permissionHandlerImpl.checkResourceCreatePermissionByType(authentication, #type)")
    @Operation(summary = "Save new fonts.", description = "Api for saving new fonts (.ttf)",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fonts saved successfully."),
            @ApiResponse(responseCode = "409", description = "A resource with the same name already exists."),
            @ApiResponse(responseCode = "400", description = "Resource file exceeds the file limit."),
            @ApiResponse(responseCode = "400", description = "The font files cannot be read."),
            @ApiResponse(responseCode = "400", description = "File extension not supported.")
    })
    ResponseEntity<ResourceDTO> createFont(Principal principal,
                                           @Parameter(name = "name", description = "Resource name", style = ParameterStyle.FORM) @RequestPart String name,
                                           @Parameter(name = "type", description = "Resource type, e.g. image, font, style sheet", style = ParameterStyle.FORM, required = true) @RequestPart String type,
                                           @Parameter(name = "regular", description = "File: regular font with .ttf file extension.", style = ParameterStyle.FORM, required = true) @RequestPart("regular") MultipartFile regular,
                                           @Parameter(name = "bold", description = "File: bold font with .ttf file extension.", style = ParameterStyle.FORM, required = true) @RequestPart("bold") MultipartFile bold,
                                           @Parameter(name = "italic", description = "File: italic font with .ttf file extension.", style = ParameterStyle.FORM, required = true) @RequestPart("italic") MultipartFile italic,
                                           @Parameter(name = "bold_italic", description = "File: bold italic font with .ttf file extension.", style = ParameterStyle.FORM, required = true) @RequestPart("bold_italic") MultipartFile boldItalic);

    @GetMapping(RESOURCE_VERSION_ENDPOINT_WITH_PATH_VARIABLE)
    @PreAuthorize("hasAnyAuthority('E8_US_64_RESOURCE_VERSIONS_HISTORY_IMAGE', 'E8_US101_RESOURCE_NAVIGATION_MENU')")
    @Operation(summary = "Get a list of versions of resource by name.", description = "Get a list of resource versions using the resource name and resource type, sorting and filters.",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<Page<FileVersionDTO>> getVersions(Principal principal,
                                                     Pageable pageable,
                                                     @Parameter(name = "resource-name", description = "Encoded with base64 resource name", required = true) @PathVariable(RESOURCE_PATH_VARIABLE) String name,
                                                     @Parameter(name = "resource-type", description = "Resource type, e.g. images, fonts, stylesheets", required = true) @PathVariable(RESOURCE_TYPE_PATH_VARIABLE) String type,
                                                     @ParameterObject VersionFilter versionFilter,
                                                     @Parameter(description = "Universal search string which filter dependencies names.") @RequestParam(name = "search", required = false) String searchParam);

    @PostMapping(path = RESOURCE_VERSION_ENDPOINT, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("@permissionHandlerImpl.checkResourceCreateVersionPermissions(#principal.name, #type, #name)")
    @Operation(summary = "Create new version of resource", description = "Make a new version of a resource: upload a new resource and a comment for the new version.",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resource new version saved successfully."),
            @ApiResponse(responseCode = "400", description = "Resource file exceeds the file limit."),
            @ApiResponse(responseCode = "400", description = "The file cannot be read."),
            @ApiResponse(responseCode = "400", description = "File extension not supported.")
    })
    ResponseEntity<ResourceDTO> createVersion(Principal principal,
                                              @Parameter(name = "name", description = "Name of an existing resource", required = true, style = ParameterStyle.FORM) @RequestPart String name,
                                              @Parameter(name = "comment", description = "Comment on the new version of the resource", style = ParameterStyle.FORM) @RequestPart(required = false) String comment,
                                              @Parameter(name = "type", description = "Resource type, e.g. image, font, style sheet", required = true, style = ParameterStyle.FORM) @RequestPart String type,
                                              @Parameter(name = "resource", description = "File - image with max size 8mb and format (bmp ,ccitt, gif, jpg, jpg2000, png , svg, wmf), font, style sheet.", required = true, style = ParameterStyle.FORM) @RequestPart("resource") MultipartFile resource);

    @GetMapping(RESOURCE_DEPENDENCIES_PAGEABLE_ENDPOINT_WITH_PATH_VARIABLE)
    @PreAuthorize("hasAnyAuthority('E8_US69_TABLE_OF_THE_RESOURCE_DEPENDENCIES_IMAGE', 'E8_US101_RESOURCE_NAVIGATION_MENU')")
    @Operation(summary = "Get a list of dependencies of one resource", description = "Retrieving resource dependencies page using sorting and filters.",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponse(responseCode = "200", description = "Information about one resource dependencies is prepared according to the specified conditions.")
    ResponseEntity<Page<DependencyDTO>> list(Pageable pageable,
                                             @Parameter(name = "resource-name", description = "Encoded with base64 resource name", required = true) @PathVariable(RESOURCE_PATH_VARIABLE) String name,
                                             @Parameter(name = "resource-type", description = "Resource type, e.g. images, fonts, stylesheets", required = true) @PathVariable(RESOURCE_TYPE_PATH_VARIABLE) String type,
                                             @Parameter(description = "Universal search string which filter dependencies names.")
                                             @RequestParam(name = "search", required = false) String searchParam,
                                             @ParameterObject DependencyFilter resourceDependencyFilter);

    @GetMapping(RESOURCE_DEPENDENCIES_ENDPOINT_WITH_PATH_VARIABLE)
    @PreAuthorize("hasAnyAuthority('E8_US69_TABLE_OF_THE_RESOURCE_DEPENDENCIES_IMAGE', 'E8_US101_RESOURCE_NAVIGATION_MENU')")
    @Operation(summary = "Get a list of dependencies of one resource", description = "Retrieving list of information about resource dependencies.",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponse(responseCode = "200", description = "Information about resource dependencies")
    ResponseEntity<List<DependencyDTO>> list(@Parameter(name = "resource-name", description = "Encoded with base64 new name of resource", required = true) @PathVariable(RESOURCE_PATH_VARIABLE) String name,
                                             @Parameter(name = "resource-type", description = "Resource type, e.g. images, fonts, stylesheets", required = true) @PathVariable(RESOURCE_TYPE_PATH_VARIABLE) String type);

    @GetMapping(RESOURCE_ENDPOINT_WITH_PATH_VARIABLE_AND_TYPE)
    @PreAuthorize("@permissionHandlerImpl.checkResourceCommonPermissionByType(authentication, #type)")
    @Operation(summary = "Get resource", description = "Get resource",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<ResourceDTO> get(
            @Parameter(name = "resource-name", description = "Encoded with base64 new name of resource", required = true) @PathVariable(RESOURCE_PATH_VARIABLE) String name,
            @Parameter(name = "resource-type", description = "Resource type, e.g. images, fonts, stylesheets", required = true) @PathVariable(RESOURCE_TYPE_PATH_VARIABLE) String type, Principal principal);

    @GetMapping
    @PreAuthorize("hasAnyAuthority('E8_US52_TABLE_OF_RESOURCES', 'E8_US101_RESOURCE_NAVIGATION_MENU')")
    @Operation(summary = "Get resource list", description = "Get available resources",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<Page<ResourceDTO>> list(Pageable pageable,
                                           @ParameterObject ResourceFilter filter,
                                           @Parameter(description = "Universal filter to find resource name, modified by and version comment ") @RequestParam(name = "search", required = false) String searchParam);


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("@permissionHandlerImpl.checkResourceCreatePermissionByType(authentication, #type)")
    @Operation(summary = "Save new resource.", description = "Api for loading images, fonts, stylesheets.",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resource saved successfully."),
            @ApiResponse(responseCode = "409", description = "A resource with the same name already exists."),
            @ApiResponse(responseCode = "400", description = "Resource file exceeds the file limit."),
            @ApiResponse(responseCode = "400", description = "The file cannot be read."),
            @ApiResponse(responseCode = "400", description = "File extension not supported.")
    })
    ResponseEntity<ResourceDTO> create(Principal principal,
                                       @Parameter(name = "name", description = "resource name", style = ParameterStyle.FORM) @RequestPart String name,
                                       @Parameter(name = "type", description = "Resource type, e.g. image, font, style sheet", style = ParameterStyle.FORM) @RequestPart String type,
                                       @Parameter(name = "resource", description = "File - image with max size 8mb and format (bmp ,ccitt, gif, jpg, jpg2000, png , svg, wmf), font, style sheet.", style = ParameterStyle.FORM) @RequestPart("resource") MultipartFile resource);

    @PutMapping(RESOURCE_ENDPOINT_WITH_PATH_VARIABLE)
    @PreAuthorize("@permissionHandlerImpl.checkResourceEditMetadataPermissions(#principal.name, #updateRequestDTO.type.pluralName, new String(T(java.util.Base64).getUrlDecoder().decode(#name)))")
    @Operation(summary = "Update resource", description = "Update resource metadata (name, description)", security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resource updated successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ResourceDTO.class))}),
            @ApiResponse(responseCode = "409", description = "There is already a resource with the same name")
    })
    ResponseEntity<ResourceDTO> update(
            @Parameter(name = "resource-name", description = "Base64-encoded name of the resource to be updated", required = true) @PathVariable(RESOURCE_PATH_VARIABLE) String name,
            @RequestBody ResourceUpdateRequestDTO updateRequestDTO, Principal principal);

    @PostMapping(RESOURCE_APPLIED_ROLES_ENDPOINT_WITH_RESOURCE_PATH_VARIABLE)
    @PreAuthorize("hasAnyAuthority('E8_US68_MANAGE_RESOURCE_PERMISSIONS_IMAGE')")
    @Operation(summary = "Add role to a resource", description = "Apply custom to a resource", security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<ResourceDTO> applyRole(
            @Parameter(name = "resource-name", description = "Encoded with base64 new name of resource", required = true) @PathVariable(RESOURCE_PATH_VARIABLE) String name,
            @Parameter(name = "resource-type", description = "Resource type, e.g. images, fonts, stylesheets", required = true) @PathVariable(RESOURCE_TYPE_PATH_VARIABLE) String type,
            @RequestBody ApplyRoleRequestDTO applyRoleRequestDTO);


    @GetMapping(RESOURCE_APPLIED_ROLES_ENDPOINT_WITH_RESOURCE_PATH_VARIABLE)
    @PreAuthorize("hasAnyAuthority('E8_US67_TABLE_OF_RESOURCE_PERMISSIONS_IMAGE', 'E8_US101_RESOURCE_NAVIGATION_MENU')")
    @Operation(summary = "Get resource's roles", description = "Retrieved attached roles.", security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<Page<ResourcePermissionDTO>> getRoles(Pageable pageable,
                                                         @Parameter(name = "resource-name", description = "Encoded with base 64 name of resource") @PathVariable(RESOURCE_PATH_VARIABLE) String resourceName,
                                                         @Parameter(name = "resource-type", description = "Resource type, e.g. images, fonts, stylesheets") @PathVariable(RESOURCE_TYPE_PATH_VARIABLE) String type,
                                                         @ParameterObject ResourcePermissionFilter filter,
                                                         @RequestParam(name = "search", required = false) String searchParam);

    @DeleteMapping(RESOURCE_APPLIED_ROLES_ENDPOINT_WITH_RESOURCE_AND_ROLE_PATH_VARIABLES)
    @PreAuthorize("hasAnyAuthority('E8_US68_MANAGE_RESOURCE_PERMISSIONS_IMAGE')")
    @Operation(summary = "Remove role from a resource", description = "Detach custom from a resource", security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<ResourceDTO> deleteRole(
            @Parameter(name = "resource-name", description = "Encoded with base64 new name of resource", required = true) @PathVariable(RESOURCE_PATH_VARIABLE) String name,
            @Parameter(name = "resource-type", description = "Resource type, e.g. images, fonts, stylesheets", required = true) @PathVariable(RESOURCE_TYPE_PATH_VARIABLE) String type,
            @Parameter(name = "role-name", description = "Encoded with base64 role name", required = true) @PathVariable(ROLE_PATH_VARIABLE) String roleName);

    @DeleteMapping(RESOURCE_ENDPOINT_WITH_PATH_VARIABLE_AND_TYPE)
    @PreAuthorize("@permissionHandlerImpl.checkResourceDeletePermissions(#principal.getName(), #type, new String(T(java.util.Base64).getUrlDecoder().decode(#name)))")
    @Operation(summary = "Delete resource", description = "Delete resource by name and type - the resource cannot be deleted if the resource version is used in the template.",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted resource.", content = @Content),
            @ApiResponse(responseCode = "409", description = "The resource is used.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Resource not found", content = @Content),
    })
    ResponseEntity<Void> delete(Principal principal,
                                @Parameter(name = "resource-name", description = "Resource name encoded with base64.", required = true) @PathVariable(RESOURCE_PATH_VARIABLE) String name,
                                @Parameter(name = "resource-type", description = "Resource type, e.g. images, fonts, stylesheets", required = true) @PathVariable(RESOURCE_TYPE_PATH_VARIABLE) String type);

    @PostMapping(RESOURCE_ROLLBACK_ENDPOINT_WITH_PATH_VARIABLE)
    @PreAuthorize("@permissionHandlerImpl.checkResourceRollbackPermissions(#principal.name, #type, new String(T(java.util.Base64).getUrlDecoder().decode(#name)))")
    @Operation(summary = "Rollback resource to selected version", description = "Rollback resource to selected version",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resource rollback was successful."),
            @ApiResponse(responseCode = "404", description = "Resource or resource version not found.")
    })
    ResponseEntity<ResourceDTO> rollbackVersion(
            Principal principal,
            @Parameter(name = "resource-name", description = "Encoded with base64 resource name", required = true) @PathVariable(RESOURCE_PATH_VARIABLE) String name,
            @Parameter(name = "resource-type", description = "Resource type, e.g. images, fonts, stylesheets", required = true) @PathVariable(RESOURCE_TYPE_PATH_VARIABLE) String type,
            @Parameter(name = "resource-version", description = "Resource version number", required = true) @PathVariable(VERSION_PATH_VARIABLE) Long version);

}
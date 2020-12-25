package com.itextpdf.dito.manager.controller.resource;

import com.itextpdf.dito.manager.config.OpenApiConfig;
import com.itextpdf.dito.manager.dto.dependency.DependencyDTO;
import com.itextpdf.dito.manager.dto.dependency.filter.DependencyFilterDTO;
import com.itextpdf.dito.manager.dto.resource.ResourceDTO;
import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.dto.resource.update.ResourceUpdateRequestDTO;
import com.itextpdf.dito.manager.filter.resource.ResourceFilter;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

    String RESOURCE_PATH_VARIABLE = "name";
    String RESOURCE_ENDPOINT_WITH_PATH_VARIABLE = "/{" + RESOURCE_PATH_VARIABLE + "}";
    String RESOURCE_VERSION_ENDPOINT = "/versions";
    String RESOURCE_DEPENDENCIES_ENDPOINT = "/dependencies";
    String RESOURCE_VERSION_ENDPOINT_WITH_PATH_VARIABLE = RESOURCE_ENDPOINT_WITH_PATH_VARIABLE + RESOURCE_VERSION_ENDPOINT;
    String RESOURCE_DEPENDENCIES_ENDPOINT_WITH_PATH_VARIABLE = RESOURCE_ENDPOINT_WITH_PATH_VARIABLE + RESOURCE_DEPENDENCIES_ENDPOINT;
    String RESOURCE_DEPENDENCIES_PAGEABLE_ENDPOINT_WITH_PATH_VARIABLE = RESOURCE_ENDPOINT_WITH_PATH_VARIABLE + RESOURCE_DEPENDENCIES_ENDPOINT + PAGEABLE_ENDPOINT;

    @GetMapping(RESOURCE_VERSION_ENDPOINT_WITH_PATH_VARIABLE)
    @Operation(summary = "Get a list of versions of resource by name.", description = "Get a list of resource versions using the resource name and resource type, sorting and filters.",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<Page<ResourceDTO>> getVersions(Principal principal,
                                                  Pageable pageable,
                                                  @Parameter(description = "Encoded with base64 resource name", required = true) @PathVariable(RESOURCE_PATH_VARIABLE) String name,
                                                  @Parameter(name = "type", description = "Resource type, e.g. image, font, style sheet", required = true) @RequestParam(name = "type") ResourceTypeEnum type,
                                                  @ParameterObject VersionFilter versionFilter,
                                                  @Parameter(description = "Universal search string which filter dependencies names.") @RequestParam(name = "search", required = false) String searchParam);

    @PostMapping(RESOURCE_VERSION_ENDPOINT)
    @Operation(summary = "Create new version of resource", description = "Make a new version of a resource: upload a new resource and a comment for the new version.",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resource new version saved successfully."),
            @ApiResponse(responseCode = "400", description = "Resource file exceeds the file limit."),
            @ApiResponse(responseCode = "400", description = "The file cannot be read."),
            @ApiResponse(responseCode = "400", description = "File extension not supported.")
    })
    ResponseEntity<ResourceDTO> create(Principal principal,
                                       @Parameter(name = "name", description = "Name of an existing resource", required = true, style = ParameterStyle.FORM) @RequestPart String name,
                                       @Parameter(name = "comment", description = "Comment on the new version of the resource", style = ParameterStyle.FORM) @RequestPart(required = false) String comment,
                                       @Parameter(name = "type", description = "Resource type, e.g. image, font, style sheet", required = true, style = ParameterStyle.FORM) @RequestPart String type,
                                       @Parameter(name = "resource", description = "File - image with max size 8mb and format (bmp ,ccitt, gif, jpg, jpg2000, png , svg, wmf), font, style sheet.", required = true, style = ParameterStyle.FORM) @RequestPart("resource") MultipartFile resource);

    @GetMapping(RESOURCE_DEPENDENCIES_PAGEABLE_ENDPOINT_WITH_PATH_VARIABLE)
    @Operation(summary = "Get a list of dependencies of one resource", description = "Retrieving resource dependencies page using sorting and filters.",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponse(responseCode = "200", description = "Information about one resource dependencies is prepared according to the specified conditions.")
    ResponseEntity<Page<DependencyDTO>> listDependencies(Pageable pageable,
                                                         @Parameter(description = "Encoded with base64 resource name", required = true) @PathVariable(RESOURCE_PATH_VARIABLE) String name,
                                                         @ParameterObject DependencyFilterDTO dependencyFilterDTO,
                                                         @Parameter(description = "Universal search string which filter dependencies names.")
                                                         @RequestParam(name = "search", required = false) String searchParam);

    @GetMapping(RESOURCE_DEPENDENCIES_ENDPOINT_WITH_PATH_VARIABLE)
    @Operation(summary = "Get a list of dependencies of one resource", description = "Retrieving list of information about resource dependencies.",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponse(responseCode = "200", description = "Information about resource dependencies")
    ResponseEntity<List<DependencyDTO>> listDependencies(@Parameter(description = "Encoded with base64 resource name", required = true) @PathVariable(RESOURCE_PATH_VARIABLE) String name);

    @GetMapping(RESOURCE_ENDPOINT_WITH_PATH_VARIABLE)
    @Operation(summary = "Get resource", description = "Get resource",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<ResourceDTO> get(@Parameter(name = "name", description = "Encoded with base64 new name of resource", required = true) @PathVariable(RESOURCE_PATH_VARIABLE) String name,
                                    @Parameter(name = "type", description = "Type of resource, image or font or stylesheet", required = true) @RequestParam ResourceTypeEnum type);

    @GetMapping
    @Operation(summary = "Get resource list", description = "Get available resources",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<Page<ResourceDTO>> list(Pageable pageable,
                                           @ParameterObject ResourceFilter filter,
                                           @Parameter(description = "Universal filter to find resource name, modified by and version comment ") @RequestParam(name = "search", required = false) String searchParam);


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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

    @PatchMapping(RESOURCE_ENDPOINT_WITH_PATH_VARIABLE)
    @Operation(summary = "Update resource", description = "Update resource metadata (name, description)", security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resource updated successfully", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResourceDTO.class))}),
            @ApiResponse(responseCode = "409", description = "There is already a resource with the same name")
    })
    ResponseEntity<ResourceDTO> update(@Parameter(name = "name", description = "Base64-encoded name of the resource to be updated", required = true) @PathVariable(RESOURCE_PATH_VARIABLE) String name, @RequestBody ResourceUpdateRequestDTO updateRequestDTO, Principal principal);

    @DeleteMapping(RESOURCE_ENDPOINT_WITH_PATH_VARIABLE)
    @Operation(summary = "Delete resource", description = "Delete resource by name and type - the resource cannot be deleted if the resource version is used in the template.",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted resource.", content = @Content),
            @ApiResponse(responseCode = "409", description = "The resource is used.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Resource not found", content = @Content),
    })
    ResponseEntity<Void> delete(@Parameter(description = "Resource name encoded with base64.", required = true) @PathVariable(RESOURCE_PATH_VARIABLE) String name,
                                @Parameter(name = "type", description = "Resource type, e.g. image, font, style sheet", required = true) @RequestParam ResourceTypeEnum type);

}

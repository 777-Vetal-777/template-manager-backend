package com.itextpdf.dito.manager.controller.template;

import com.itextpdf.dito.manager.config.OpenApiConfig;
import com.itextpdf.dito.manager.dto.dependency.DependencyDTO;
import com.itextpdf.dito.manager.dto.dependency.filter.DependencyFilterDTO;
import com.itextpdf.dito.manager.dto.template.TemplateDTO;
import com.itextpdf.dito.manager.dto.template.TemplateMetadataDTO;
import com.itextpdf.dito.manager.dto.template.TemplateVersionDTO;
import com.itextpdf.dito.manager.dto.template.create.TemplateCreateRequestDTO;
import com.itextpdf.dito.manager.dto.template.update.TemplateUpdateRequestDTO;
import com.itextpdf.dito.manager.entity.TemplateTypeEnum;
import com.itextpdf.dito.manager.filter.template.TemplateFilter;

import com.itextpdf.dito.manager.filter.version.VersionFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import org.springdoc.api.annotations.ParameterObject;
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

@RequestMapping(TemplateController.BASE_NAME)
@Tag(name = "template", description = "templates API")
public interface TemplateController {
    String MAJOR_VERSION = "/v1";
    String BASE_NAME = MAJOR_VERSION + "/templates";

    String TEMPLATE_TYPES_ENDPOINT = "/types";
    String TEMPLATE_PATH_VARIABLE = "name";
    String TEMPLATE_VERSION_ENDPOINT = "/versions";
    String TEMPLATE_ENDPOINT_WITH_PATH_VARIABLE = "/{" + TEMPLATE_PATH_VARIABLE + "}";
    String TEMPLATE_DEPENDENCIES_ENDPOINT_WITH_PATH_VARIABLE = TEMPLATE_ENDPOINT_WITH_PATH_VARIABLE + "/dependencies";
    String TEMPLATE_VERSION_ENDPOINT_WITH_PATH_VARIABLE = TEMPLATE_ENDPOINT_WITH_PATH_VARIABLE + TEMPLATE_VERSION_ENDPOINT;


    @PostMapping
    @Operation(summary = "Create template", description = "Create new template",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = TemplateDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input or template already exists", content = @Content)})
    ResponseEntity<TemplateDTO> create(@RequestBody TemplateCreateRequestDTO templateCreateRequestDTO,
            Principal principal);

    @GetMapping
    @Operation(summary = "Get template list", description = "Get templates",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<Page<TemplateDTO>> listTemplateTypes(Pageable pageable,
            @ParameterObject TemplateFilter templateFilter,
            @Parameter(description = "search by template fields") @RequestParam(name = "search", required = false) String searchParam);

    @GetMapping(TEMPLATE_TYPES_ENDPOINT)
    @Operation(summary = "Get template type list", description = "Get all template types",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<TemplateTypeEnum[]> listTemplateTypes();

    @GetMapping(TEMPLATE_DEPENDENCIES_ENDPOINT_WITH_PATH_VARIABLE)
    @Operation(summary = "Get dependencies list", description = "Retrieving list of information about dependencies using sorting and filters.",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponse(responseCode = "200", description = "Information about dependencies is prepared according to the specified conditions.")
    ResponseEntity<Page<DependencyDTO>> listDependencies(
            @Parameter(description = "Encoded with base64 template name", required = true) @PathVariable(TEMPLATE_PATH_VARIABLE) String name,
            Pageable pageable,
            @ParameterObject DependencyFilterDTO dependencyFilterDTO,
            @Parameter(description = "Universal search string which filter dependencies names.")
            @RequestParam(name = "search", required = false) String searchParam);

    @GetMapping(TEMPLATE_ENDPOINT_WITH_PATH_VARIABLE)
    @Operation(summary = "Get template metadata", description = "Get template metadata",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<TemplateMetadataDTO> get(@Parameter(description = "Template name encoded with base64.") @PathVariable("name") String name);

    @PatchMapping(TEMPLATE_ENDPOINT_WITH_PATH_VARIABLE)
    @Operation(summary = "Update template metadata", description = "Update template metadata",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<TemplateMetadataDTO> update(@Parameter(description = "Template name encoded with base64.") @PathVariable("name") String name,
                                               @RequestBody TemplateUpdateRequestDTO templateUpdateRequestDTO,
                                               Principal principal);

    @GetMapping(TEMPLATE_VERSION_ENDPOINT_WITH_PATH_VARIABLE)
    @Operation(summary = "Get a list of versions of template by name.", description = "Get a list of template versions using the template name and template type, sorting and filters.",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<Page<TemplateVersionDTO>> getVersions(Pageable pageable,
                                                         @Parameter(description = "Encoded with base64 resource name", required = true) @PathVariable(TEMPLATE_PATH_VARIABLE) String name,
                                                         @ParameterObject VersionFilter versionFilter,
                                                         @Parameter(description = "Universal search string.") @RequestParam(name = "search", required = false) String searchParam);


}

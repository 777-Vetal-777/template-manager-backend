package com.itextpdf.dito.manager.controller.template;

import com.itextpdf.dito.manager.config.OpenApiConfig;
import com.itextpdf.dito.manager.dto.template.TemplateDTO;
import com.itextpdf.dito.manager.dto.template.create.TemplateCreateRequestDTO;

import com.itextpdf.dito.manager.filter.template.TemplateFilter;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping(TemplateController.BASE_NAME)
@Tag(name = "template", description = "templates API")
public interface TemplateController {
    String MAJOR_VERSION = "/v1";
    String BASE_NAME = MAJOR_VERSION + "/templates";

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
    ResponseEntity<Page<TemplateDTO>> list(Pageable pageable, @ParameterObject TemplateFilter templateFilter,
                                           @Parameter(description = "search by template fields") @RequestParam(name = "search", required = false) String searchParam);
}

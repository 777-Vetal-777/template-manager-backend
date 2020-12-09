package com.itextpdf.dito.manager.controller.resource;

import com.itextpdf.dito.manager.config.OpenApiConfig;
import com.itextpdf.dito.manager.dto.resource.ResourceDTO;
import com.itextpdf.dito.manager.dto.resource.filter.ResourceFilterDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping(ResourceController.BASE_NAME)
@Tag(name = "resource", description = "resource API")
public interface ResourceController {
    String MAJOR_VERSION = "/v1";
    String BASE_NAME = MAJOR_VERSION + "/resources";

    String RESOURCE_PATH_VARIABLE = "name";
    String RESOURCE_ENDPOINT_WITH_PATH_VARIABLE = "/{" + RESOURCE_PATH_VARIABLE + "}";

    @GetMapping
    @Operation(summary = "Get resource list", description = "Get available resources",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<Page<ResourceDTO>> list(Pageable pageable,
                                           @ParameterObject ResourceFilterDTO filter,
                                           @RequestParam(name = "search", required = false) String searchParam);

    @GetMapping(RESOURCE_ENDPOINT_WITH_PATH_VARIABLE)
    @Operation(summary = "Get resource", description = "Get resource",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<ResourceDTO> get(@PathVariable(RESOURCE_PATH_VARIABLE) String name);
}

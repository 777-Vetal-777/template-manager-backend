package com.itextpdf.dito.manager.controller.dependency;

import com.itextpdf.dito.manager.config.OpenApiConfig;
import com.itextpdf.dito.manager.dto.dependency.filter.DependencyFilterDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping(DependencyController.BASE_NAME)
@Tag(name = "dependency", description = "dependency API")
public interface DependencyController {
    String MAJOR_VERSION = "/v1";
    String BASE_NAME = MAJOR_VERSION + "/dependencies";

    @GetMapping
    @Operation(summary = "Get dependencies list", description = "Retrieving list of information about dependencies using sorting and filters.",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponse(responseCode = "200", description = "Information about dependencies is prepared according to the specified conditions.")
    ResponseEntity<Page<DependencyDTO>> list(Pageable pageable,
                                             @ParameterObject DependencyFilterDTO dependencyFilterDTO,
                                             @Parameter(description = "Universal search string which filter dependencies names.")
                                             @RequestParam(name = "search", required = false) String searchParam);

}

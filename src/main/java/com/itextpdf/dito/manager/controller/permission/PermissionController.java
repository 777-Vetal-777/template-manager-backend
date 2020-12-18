package com.itextpdf.dito.manager.controller.permission;

import com.itextpdf.dito.manager.config.OpenApiConfig;
import com.itextpdf.dito.manager.dto.permission.PermissionDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping(PermissionController.BASE_NAME)
@Tag(name = "permission", description = "permission API")
public interface PermissionController {
    String MAJOR_VERSION = "/v1";
    String BASE_NAME = MAJOR_VERSION + "/permissions";

    String PAGEABLE_ENDPOINT = "/pageable";


    @GetMapping(PAGEABLE_ENDPOINT)
    @Operation(summary = "Get permission list with Pageable", description = "Get available permissions with Pageable",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<Page<PermissionDTO>> list(Pageable pageable,
                                             @RequestParam(name = "search", required = false) String searchParam);

    @GetMapping
    @Operation(summary = "Get permission list", description = "Get available permissions",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<List<PermissionDTO>> list();
}

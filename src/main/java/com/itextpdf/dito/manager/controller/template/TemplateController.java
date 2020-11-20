package com.itextpdf.dito.manager.controller.template;

import com.itextpdf.dito.manager.config.OpenApiConfig;
import com.itextpdf.dito.manager.dto.template.TemplateDTO;
import com.itextpdf.dito.manager.dto.template.create.TemplateCreateRequestDTO;

import java.security.Principal;
import javax.validation.Valid;

import com.itextpdf.dito.manager.dto.template.create.TemplateCreateResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping(TemplateController.BASE_NAME)
public interface TemplateController {
    String MAJOR_VERSION = "/v1";
    String BASE_NAME = MAJOR_VERSION + "/templates";

    @PostMapping
    @Operation(summary = "Create template", security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME))
    ResponseEntity<TemplateCreateResponseDTO> create(@RequestBody @Valid TemplateCreateRequestDTO templateCreateRequestDTO, Principal principal);

    @GetMapping
    @Operation(summary = "Get template list", security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME))
    ResponseEntity<Page<TemplateDTO>> list(Pageable pageable, @RequestParam(name = "search", required = false) String searchParam);
}

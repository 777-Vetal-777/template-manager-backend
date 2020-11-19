package com.itextpdf.dito.manager.controller.template;

import com.itextpdf.dito.manager.dto.template.TemplateDTO;
import com.itextpdf.dito.manager.dto.template.create.TemplateCreateRequestDTO;

import java.security.Principal;
import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(TemplateController.BASE_NAME)
public interface TemplateController {
    String MAJOR_VERSION = "/v1";
    String BASE_NAME = MAJOR_VERSION + "/templates";

    @PostMapping
    ResponseEntity<?> create(@RequestBody @Valid TemplateCreateRequestDTO templateCreateRequestDTO, Principal principal);

    @GetMapping
    ResponseEntity<Page<TemplateDTO>> list(Pageable pageable);
}

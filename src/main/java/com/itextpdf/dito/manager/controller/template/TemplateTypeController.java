package com.itextpdf.dito.manager.controller.template;

import com.itextpdf.dito.manager.dto.template.type.TemplateTypeListResponseDTO;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(TemplateTypeController.BASE_NAME)
public interface TemplateTypeController {
    String MAJOR_VERSION = "/v1";
    String BASE_NAME = MAJOR_VERSION + "/template-types";

    @GetMapping
    ResponseEntity<TemplateTypeListResponseDTO> list();
}

package com.itextpdf.dito.manager.controller.template.impl;

import com.itextpdf.dito.manager.component.mapper.template.TemplateTypeMapper;
import com.itextpdf.dito.manager.controller.template.TemplateTypeController;
import com.itextpdf.dito.manager.dto.template.type.TemplateTypeListResponseDTO;
import com.itextpdf.dito.manager.service.template.TemplateTypeService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TemplateTypeControllerImpl implements TemplateTypeController {
    private final TemplateTypeService templateTypeService;
    private final TemplateTypeMapper templateTypeMapper;

    public TemplateTypeControllerImpl(final TemplateTypeService templateTypeService,
            final TemplateTypeMapper templateTypeMapper) {
        this.templateTypeService = templateTypeService;
        this.templateTypeMapper = templateTypeMapper;
    }

    @Override
    public ResponseEntity<TemplateTypeListResponseDTO> list() {
        final TemplateTypeListResponseDTO templateTypeListResponseDTO = templateTypeMapper
                .map(templateTypeService.getAll());
        return new ResponseEntity(templateTypeListResponseDTO, HttpStatus.OK);
    }
}

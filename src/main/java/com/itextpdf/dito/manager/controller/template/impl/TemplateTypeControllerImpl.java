package com.itextpdf.dito.manager.controller.template.impl;

import com.itextpdf.dito.manager.component.mapper.template.TemplateTypeMapper;
import com.itextpdf.dito.manager.controller.AbstractController;
import com.itextpdf.dito.manager.controller.template.TemplateTypeController;
import com.itextpdf.dito.manager.dto.template.TemplateTypeDTO;
import com.itextpdf.dito.manager.service.template.TemplateTypeService;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TemplateTypeControllerImpl extends AbstractController implements TemplateTypeController {
    private final TemplateTypeService templateTypeService;
    private final TemplateTypeMapper templateTypeMapper;

    public TemplateTypeControllerImpl(final TemplateTypeService templateTypeService,
            final TemplateTypeMapper templateTypeMapper) {
        this.templateTypeService = templateTypeService;
        this.templateTypeMapper = templateTypeMapper;
    }

    @Override
    public ResponseEntity<List<TemplateTypeDTO>> list() {
        final List<TemplateTypeDTO> templateTypeDTOS = templateTypeMapper
                .map(templateTypeService.getAll());
        return new ResponseEntity<>(templateTypeDTOS, HttpStatus.OK);
    }
}

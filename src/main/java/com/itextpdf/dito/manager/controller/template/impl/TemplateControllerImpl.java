package com.itextpdf.dito.manager.controller.template.impl;

import com.itextpdf.dito.manager.component.mapper.template.TemplateMapper;
import com.itextpdf.dito.manager.controller.template.TemplateController;
import com.itextpdf.dito.manager.dto.template.TemplateDTO;
import com.itextpdf.dito.manager.dto.template.create.TemplateCreateRequestDTO;
import com.itextpdf.dito.manager.dto.template.create.TemplateCreateResponseDTO;
import com.itextpdf.dito.manager.service.template.TemplateService;

import java.security.Principal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TemplateControllerImpl implements TemplateController {
    private final TemplateService templateService;
    private final TemplateMapper templateMapper;

    public TemplateControllerImpl(final TemplateService templateService, final TemplateMapper templateMapper) {
        this.templateService = templateService;
        this.templateMapper = templateMapper;
    }

    @Override
    public ResponseEntity<TemplateCreateResponseDTO> create(final TemplateCreateRequestDTO templateCreateRequestDTO, final Principal principal) {
        templateService.create(templateCreateRequestDTO, principal.getName());
        return new ResponseEntity<TemplateCreateResponseDTO>(HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Page<TemplateDTO>> list(final Pageable pageable, final String search) {
        return new ResponseEntity<>(templateMapper.map(templateService.getAll(pageable, search)), HttpStatus.OK);
    }
}

package com.itextpdf.dito.manager.controller.template.impl;

import com.itextpdf.dito.manager.component.mapper.template.TemplateMapper;
import com.itextpdf.dito.manager.controller.AbstractController;
import com.itextpdf.dito.manager.controller.template.TemplateController;
import com.itextpdf.dito.manager.dto.template.TemplateDTO;
import com.itextpdf.dito.manager.dto.template.create.TemplateCreateRequestDTO;
import com.itextpdf.dito.manager.entity.TemplateEntity;
import com.itextpdf.dito.manager.service.template.TemplateService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class TemplateControllerImpl extends AbstractController implements TemplateController {
    private final TemplateService templateService;
    private final TemplateMapper templateMapper;

    public TemplateControllerImpl(final TemplateService templateService, final TemplateMapper templateMapper) {
        this.templateService = templateService;
        this.templateMapper = templateMapper;
    }

    @Override
    public ResponseEntity<TemplateDTO> create(final TemplateCreateRequestDTO templateCreateRequestDTO,
                                              final Principal principal) {
        final TemplateEntity templateEntity = templateService
                .create(templateCreateRequestDTO.getName(), templateCreateRequestDTO.getType(),
                        templateCreateRequestDTO.getDataCollectionName(), principal.getName());
        return new ResponseEntity<>(templateMapper.map(templateEntity), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Page<TemplateDTO>> list(final Pageable pageable, final String searchParam) {
        return new ResponseEntity<>(templateMapper.map(templateService.getAll(pageable, searchParam)), HttpStatus.OK);
    }
}

package com.itextpdf.dito.manager.service.template;

import com.itextpdf.dito.manager.dto.template.create.TemplateCreateRequestDTO;
import com.itextpdf.dito.manager.entity.TemplateEntity;
import com.itextpdf.dito.manager.entity.TemplateFileEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TemplateService {
    TemplateFileEntity create(TemplateCreateRequestDTO templateCreateRequestDTO, String email);

    Page<TemplateEntity> getAll(Pageable pageable, String searchParam);
}

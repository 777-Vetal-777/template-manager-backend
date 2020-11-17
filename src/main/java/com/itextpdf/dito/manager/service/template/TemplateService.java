package com.itextpdf.dito.manager.service.template;

import com.itextpdf.dito.manager.dto.template.create.TemplateCreateRequestDTO;

import java.security.Principal;

public interface TemplateService {
    void create(TemplateCreateRequestDTO templateCreateRequestDTO, Principal principal);
}

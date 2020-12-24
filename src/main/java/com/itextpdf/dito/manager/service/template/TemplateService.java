package com.itextpdf.dito.manager.service.template;

import com.itextpdf.dito.manager.entity.TemplateEntity;

import com.itextpdf.dito.manager.entity.TemplateTypeEnum;
import com.itextpdf.dito.manager.filter.template.TemplateFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TemplateService {
    TemplateEntity create(String templateName, TemplateTypeEnum templateTypeEnum, String dataCollectionName, String email);

    Page<TemplateEntity> getAll(Pageable pageable, TemplateFilter templateFilter, String searchParam);

    TemplateEntity get(String name);

    TemplateEntity update(String name, TemplateEntity updatedTemplateEntity, String userEmail);
}

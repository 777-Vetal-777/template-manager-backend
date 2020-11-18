package com.itextpdf.dito.manager.service.template;

import com.itextpdf.dito.manager.entity.TemplateTypeEntity;

import java.util.List;

public interface TemplateTypeService {
    TemplateTypeEntity findTemplateType(String type);

    List<TemplateTypeEntity> getAll();
}

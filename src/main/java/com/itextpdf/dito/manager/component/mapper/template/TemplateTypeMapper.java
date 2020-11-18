package com.itextpdf.dito.manager.component.mapper.template;

import com.itextpdf.dito.manager.dto.template.type.TemplateTypeListResponseDTO;
import com.itextpdf.dito.manager.entity.TemplateTypeEntity;

import java.util.List;

public interface TemplateTypeMapper {
    TemplateTypeListResponseDTO map(List<TemplateTypeEntity> entities);
}

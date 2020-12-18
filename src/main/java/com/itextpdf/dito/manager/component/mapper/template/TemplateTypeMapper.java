package com.itextpdf.dito.manager.component.mapper.template;

import com.itextpdf.dito.manager.dto.template.TemplateTypeDTO;
import com.itextpdf.dito.manager.entity.TemplateTypeEntity;

import java.util.List;

public interface TemplateTypeMapper {
    List<TemplateTypeDTO> map(List<TemplateTypeEntity> entities);
}

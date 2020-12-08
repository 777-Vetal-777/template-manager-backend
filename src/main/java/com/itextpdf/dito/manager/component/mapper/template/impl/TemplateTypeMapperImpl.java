package com.itextpdf.dito.manager.component.mapper.template.impl;

import com.itextpdf.dito.manager.component.mapper.template.TemplateTypeMapper;
import com.itextpdf.dito.manager.dto.template.TemplateTypeDTO;
import com.itextpdf.dito.manager.entity.TemplateTypeEntity;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class TemplateTypeMapperImpl implements TemplateTypeMapper {
    @Override
    public List<TemplateTypeDTO> map(List<TemplateTypeEntity> entities) {
        return entities.stream().map(e -> new TemplateTypeDTO(e.getName())).collect(Collectors.toList());
    }
}

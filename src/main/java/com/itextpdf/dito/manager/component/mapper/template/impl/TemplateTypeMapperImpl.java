package com.itextpdf.dito.manager.component.mapper.template.impl;

import com.itextpdf.dito.manager.component.mapper.template.TemplateTypeMapper;
import com.itextpdf.dito.manager.dto.template.type.TemplateTypeListResponseDTO;
import com.itextpdf.dito.manager.entity.TemplateTypeEntity;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class TemplateTypeMapperImpl implements TemplateTypeMapper {
    @Override
    public TemplateTypeListResponseDTO map(List<TemplateTypeEntity> entities) {
        TemplateTypeListResponseDTO result = new TemplateTypeListResponseDTO();

        List<String> types = entities.stream().map(e -> e.getName()).collect(Collectors.toList());
        result.setTypes(types);

        return result;
    }
}

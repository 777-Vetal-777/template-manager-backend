package com.itextpdf.dito.manager.component.mapper.template.impl;

import com.itextpdf.dito.manager.component.mapper.template.TemplateMapper;
import com.itextpdf.dito.manager.dto.template.TemplateDTO;
import com.itextpdf.dito.manager.entity.TemplateEntity;
import com.itextpdf.dito.manager.entity.TemplateFileEntity;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class TemplateMapperImpl implements TemplateMapper {
    @Override
    public TemplateDTO map(final TemplateEntity entity) {
        final TemplateDTO result = new TemplateDTO();
        result.setName(entity.getName());
        result.setType(entity.getType().getName());
        final TemplateFileEntity file = entity.getFiles().get(0);
        result.setAuthor(file.getAuthor().getEmail());
        result.setLastUpdate(file.getVersion());
        return result;
    }

    @Override
    public List<TemplateDTO> map(final List<TemplateEntity> entities) {
        final List<TemplateDTO> result = new ArrayList<>();

        for (final TemplateEntity entity : entities) {
            result.add(map(entity));
        }

        return result;
    }

    @Override
    public Page<TemplateDTO> map(final Page<TemplateEntity> entities) {
        return entities.map(this::map);
    }
}

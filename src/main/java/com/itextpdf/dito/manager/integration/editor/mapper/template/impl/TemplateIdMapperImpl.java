package com.itextpdf.dito.manager.integration.editor.mapper.template.impl;

import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.integration.editor.mapper.template.TemplateIdMapper;
import org.springframework.stereotype.Component;

@Component
public class TemplateIdMapperImpl implements TemplateIdMapper {
    @Override
    public String mapToId(final TemplateEntity entity) {
        return entity.getUuid();
    }
}

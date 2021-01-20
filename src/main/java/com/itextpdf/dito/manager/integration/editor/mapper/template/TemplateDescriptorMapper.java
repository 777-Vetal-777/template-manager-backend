package com.itextpdf.dito.manager.integration.editor.mapper.template;

import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.integration.editor.dto.template.TemplateDescriptor;

import java.util.List;

public interface TemplateDescriptorMapper {
    TemplateDescriptor map(TemplateEntity entity);

    List<TemplateDescriptor> map(List<TemplateEntity> entities);
}

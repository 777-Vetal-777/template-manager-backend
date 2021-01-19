package com.itextpdf.dito.manager.integration.editor.mapper.template;

import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.integration.editor.dto.template.TemplateDescriptor;

public interface TemplateDescriptorMapper {
    TemplateDescriptor map(TemplateEntity templateEntity);
}

package com.itextpdf.dito.manager.integration.editor.mapper.template;

import com.itextpdf.dito.editor.server.common.core.descriptor.ExternalTemplateDescriptor;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;

import java.util.List;

public interface TemplateDescriptorMapper {
    ExternalTemplateDescriptor map(TemplateEntity entity);

    List<ExternalTemplateDescriptor> map(List<TemplateEntity> entities);

    String encodeToBase64(final String value);
}

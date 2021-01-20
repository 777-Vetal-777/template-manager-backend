package com.itextpdf.dito.manager.integration.editor.mapper.template.impl;

import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.integration.editor.dto.template.TemplateDescriptor;
import com.itextpdf.dito.manager.integration.editor.mapper.template.TemplateDescriptorMapper;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class TemplateDescriptorMapperImpl implements TemplateDescriptorMapper {
    @Override
    public TemplateDescriptor map(final TemplateEntity templateEntity) {
        final TemplateDescriptor result = new TemplateDescriptor();
        final String templateName = templateEntity.getName();
        result.setId(encodeToBase64(templateName));
        result.setDisplayName(templateName);
        final String dataCollectionName = templateEntity.getLatestFile().getDataCollectionFile().getDataCollection()
                .getName();
        result.setDataCollectionId(encodeToBase64(dataCollectionName));
        return result;
    }

    @Override
    public List<TemplateDescriptor> map(List<TemplateEntity> entities) {
        return entities.stream().map(this::map).collect(Collectors.toList());
    }

    private String encodeToBase64(final String value) {
        return new String(Base64.getUrlEncoder().encode(value.getBytes()));
    }
}

package com.itextpdf.dito.manager.integration.editor.mapper.template.impl;

import com.itextpdf.dito.editor.server.common.core.descriptor.ExternalTemplateDescriptor;
import com.itextpdf.dito.editor.server.common.core.descriptor.TemplateFragmentType;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionFileEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;
import com.itextpdf.dito.manager.integration.editor.mapper.template.TemplateDescriptorMapper;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class TemplateDescriptorMapperImpl implements TemplateDescriptorMapper {
    @Override
    public ExternalTemplateDescriptor map(final TemplateEntity templateEntity) {
        final String templateName = templateEntity.getName();
        final TemplateFragmentType templateFragmentType = TemplateFragmentType.valueOf(templateEntity.getType().toString());
        final String dataCollectionId = Optional.ofNullable(templateEntity)
                .map(TemplateEntity::getLatestFile)
                .map(TemplateFileEntity::getDataCollectionFile)
                .map(DataCollectionFileEntity::getDataCollection)
                .map(DataCollectionEntity::getUuid)
                .orElse(null);
        return new ExternalTemplateDescriptor(encodeToBase64(templateName), templateName, dataCollectionId, templateFragmentType);
    }

    @Override
    public List<ExternalTemplateDescriptor> map(List<TemplateEntity> entities) {
        return entities.stream().map(this::map).collect(Collectors.toList());
    }

    @Override
    public String encodeToBase64(final String value) {
        return new String(Base64.getUrlEncoder().encode(value.getBytes()));
    }
}

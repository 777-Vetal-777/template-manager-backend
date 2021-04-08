package com.itextpdf.dito.manager.integration.editor.mapper.template.impl;

import com.itextpdf.dito.editor.server.common.core.descriptor.ExternalTemplateDescriptor;
import com.itextpdf.dito.editor.server.common.core.descriptor.TemplateFragmentType;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionFileEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;
import com.itextpdf.dito.manager.integration.editor.mapper.datacollection.DataCollectionIdMapper;
import com.itextpdf.dito.manager.integration.editor.mapper.template.TemplateDescriptorMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class TemplateDescriptorMapperImpl implements TemplateDescriptorMapper {

    private final DataCollectionIdMapper dataCollectionIdMapper;

    public TemplateDescriptorMapperImpl(final DataCollectionIdMapper dataCollectionIdMapper) {
        this.dataCollectionIdMapper = dataCollectionIdMapper;
    }

    @Override
    public ExternalTemplateDescriptor map(final TemplateEntity templateEntity) {
        final String templateName = templateEntity.getName();
        final TemplateFragmentType templateFragmentType = TemplateFragmentType.valueOf(templateEntity.getType().toString());
        final String dataCollectionId = Optional.of(templateEntity)
                .map(TemplateEntity::getLatestFile)
                .map(TemplateFileEntity::getDataCollectionFile)
                .map(DataCollectionFileEntity::getDataCollection)
                .map(dataCollectionIdMapper::mapToId)
                .orElse(null);
        return new ExternalTemplateDescriptor(templateEntity.getUuid(), templateName, dataCollectionId, templateFragmentType);
    }

    @Override
    public List<ExternalTemplateDescriptor> map(List<TemplateEntity> entities) {
        return entities.stream().map(this::map).collect(Collectors.toList());
    }

}

package com.itextpdf.dito.manager.integration.editor.controller.template.impl;

import com.itextpdf.dito.manager.controller.AbstractController;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.integration.editor.controller.template.TemplateManagementController;
import com.itextpdf.dito.manager.integration.editor.dto.template.TemplateAddDescriptor;
import com.itextpdf.dito.manager.integration.editor.dto.template.TemplateDescriptor;
import com.itextpdf.dito.manager.integration.editor.dto.template.TemplateUpdateDescriptor;
import com.itextpdf.dito.manager.integration.editor.mapper.template.TemplateDescriptorMapper;
import com.itextpdf.dito.manager.service.template.TemplateService;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TemplateManagementControllerImpl extends AbstractController implements TemplateManagementController {
    private final TemplateService templateService;
    private final TemplateDescriptorMapper templateDescriptorMapper;

    public TemplateManagementControllerImpl(final TemplateService templateService,
            final TemplateDescriptorMapper templateDescriptorMapper) {
        this.templateService = templateService;
        this.templateDescriptorMapper = templateDescriptorMapper;
    }

    @Override
    public TemplateDescriptor getDescriptor(final String templateId) {
        final TemplateEntity templateEntity = templateService.get(decodeBase64(templateId));
        return templateDescriptorMapper.map(templateEntity);
    }

    @Override
    public InputStream get(final String templateId) {
        final TemplateEntity templateEntity = templateService.get(decodeBase64(templateId));
        return new ByteArrayInputStream(templateEntity.getLatestFile().getData());
    }

    @Override
    public List<TemplateDescriptor> getAllDescriptors(final String workspaceId) {
        // At now we support only single workspace, that's why all templates will be returned.
        return templateDescriptorMapper.map(templateService.getAll());
    }

    @Override
    public TemplateDescriptor update(final String templateId, final TemplateUpdateDescriptor descriptor,
            final InputStream data) {
        return null;
    }

    @Override
    public TemplateDescriptor add(final String workspaceId, final TemplateAddDescriptor descriptor,
            final InputStream data) {
        return null;
    }

    @Override
    public TemplateDescriptor delete(final String templateId) {
        return null;
    }
}

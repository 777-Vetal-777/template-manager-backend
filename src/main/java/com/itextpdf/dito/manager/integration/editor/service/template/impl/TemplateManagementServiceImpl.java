package com.itextpdf.dito.manager.integration.editor.service.template.impl;

import com.itextpdf.dito.manager.entity.TemplateTypeEnum;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.integration.editor.service.template.TemplateManagementService;
import com.itextpdf.dito.manager.service.template.TemplateService;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TemplateManagementServiceImpl implements TemplateManagementService {
    private final TemplateService templateService;

    public TemplateManagementServiceImpl(final TemplateService templateService) {
        this.templateService = templateService;
    }


    @Override
    public TemplateEntity get(final String name) {
        return templateService.get(name);
    }

    @Override
    public List<TemplateEntity> getAll() {
        return templateService.getAll();
    }

    @Override
    public TemplateEntity createNewVersion(final String name, final byte[] data, final String email, final String newName) {
        return templateService.createNewVersion(name, data, email, null, newName);
    }

    @Override
    public TemplateEntity create(final String name, final String email) {
        return templateService.create(name, TemplateTypeEnum.STANDARD, null, email);
    }

    @Override
    public TemplateEntity delete(final String templateName) {
        return templateService.delete(templateName);
    }
}

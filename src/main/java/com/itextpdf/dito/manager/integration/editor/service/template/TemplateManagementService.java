package com.itextpdf.dito.manager.integration.editor.service.template;

import com.itextpdf.dito.manager.entity.template.TemplateEntity;

import java.util.List;

public interface TemplateManagementService {

    TemplateEntity get(String name);

    List<TemplateEntity> getAll();

    TemplateEntity createNewVersion(String name, byte[] data, String email, String newName, String comment);

    TemplateEntity create(String templateName, String email);

    TemplateEntity create(String name, byte[] data, String dataCollectionName, String email);

    TemplateEntity delete(String templateName);
}

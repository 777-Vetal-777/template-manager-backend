package com.itextpdf.dito.manager.service.template;

import com.itextpdf.dito.manager.entity.template.TemplateEntity;

import java.io.OutputStream;

public interface TemplatePreviewGenerator {
    OutputStream generatePreview(String templateName);
}

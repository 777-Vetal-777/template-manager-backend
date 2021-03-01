package com.itextpdf.dito.manager.service.template;

public interface TemplateExportService {
    byte[] export(String templateName, boolean exportDependencies);
}

package com.itextpdf.dito.manager.service.template;

import com.itextpdf.dito.manager.model.template.TemplateExportVersion;

public interface TemplateDtmExportService {
    byte[] export(String templateName, boolean exportDependencies, TemplateExportVersion versionsToExport);
}

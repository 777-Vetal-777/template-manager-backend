package com.itextpdf.dito.manager.dto.template.export;

import com.itextpdf.dito.manager.model.template.TemplateExportVersion;

public class TemplateExportSettingsDTO {
    private Boolean exportDependencies;
    private TemplateExportVersion versions;

    public Boolean getExportDependencies() {
        return exportDependencies;
    }

    public void setExportDependencies(Boolean exportDependencies) {
        this.exportDependencies = exportDependencies;
    }

    public TemplateExportVersion getVersions() {
        return versions;
    }

    public void setVersions(TemplateExportVersion versions) {
        this.versions = versions;
    }

    @Override
    public String toString() {
        return "TemplateExportSettingsDTO {" +
                "exportDependencies=" + exportDependencies +
                ", versions=" + versions +
                '}';
    }
}

package com.itextpdf.dito.manager.dto.template.deployment;

public class TemplateDeploymentDTO {

    private String alias;
    private final TemplateMetaDTO meta = new TemplateMetaDTO();

    public void setVersion(String version) {
        this.meta.setVersion(version);
    }

    public String getVersion() {
        return this.meta.getVersion();
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}

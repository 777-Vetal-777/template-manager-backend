package com.itextpdf.dito.manager.dto.template.export;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TemplateExportDTO {
    @JsonProperty(defaultValue = "true")
    private Boolean resources;

    public Boolean getResources() {
        return resources;
    }

    public void setResources(Boolean resources) {
        this.resources = resources;
    }
}

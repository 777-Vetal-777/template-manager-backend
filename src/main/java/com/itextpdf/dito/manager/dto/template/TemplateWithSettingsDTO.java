package com.itextpdf.dito.manager.dto.template;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TemplateWithSettingsDTO extends TemplateDTO{
    @JsonProperty("newPage")
    private Boolean startOnNewPage;

    public Boolean isStartOnNewPage() {
        return startOnNewPage;
    }

    public void setStartOnNewPage(Boolean startOnNewPage) {
        this.startOnNewPage = startOnNewPage;
    }
}

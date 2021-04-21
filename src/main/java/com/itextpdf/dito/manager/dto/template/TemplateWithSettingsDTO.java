package com.itextpdf.dito.manager.dto.template;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.itextpdf.dito.manager.model.template.part.VisibleOnSettings;

public class TemplateWithSettingsDTO extends TemplateDTO{
    @JsonProperty("newPage")
    private Boolean startOnNewPage;
    @JsonProperty("visible_on")
    private VisibleOnSettings visibleOn;

    public Boolean isStartOnNewPage() {
        return startOnNewPage;
    }

    public void setStartOnNewPage(Boolean startOnNewPage) {
        this.startOnNewPage = startOnNewPage;
    }

    public VisibleOnSettings getVisibleOn() {
        return visibleOn;
    }

    public void setVisibleOn(VisibleOnSettings visibleOn) {
        this.visibleOn = visibleOn;
    }
}

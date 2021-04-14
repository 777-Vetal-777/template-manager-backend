package com.itextpdf.dito.manager.model.template.part;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PartSettings {
    private Boolean startOnNewPage;
    private VisibleOnSettings visibleOn;

    public Boolean getStartOnNewPage() {
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

    @Override
    public String toString() {
        return "PartSettings{" +
                "startOnNewPage=" + startOnNewPage +
                ", visibleOn=" + visibleOn +
                '}';
    }
}

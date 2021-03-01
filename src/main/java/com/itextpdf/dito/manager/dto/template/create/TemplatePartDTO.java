package com.itextpdf.dito.manager.dto.template.create;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;

public class TemplatePartDTO {
    @NotBlank
    @Schema(example = "Standard template name")
    private String name;
    private String condition;

    //settings
    @JsonProperty("newPage")
    private Boolean startOnNewPage;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Boolean getStartOnNewPage() {
        return startOnNewPage;
    }

    public void setStartOnNewPage(Boolean startOnNewPage) {
        this.startOnNewPage = startOnNewPage;
    }

    @Override
    public String toString() {
        return "TemplatePartDTO{" +
                "name='" + name + '\'' +
                ", condition='" + condition + '\'' +
                ", startOnNewPage=" + startOnNewPage +
                '}';
    }
}

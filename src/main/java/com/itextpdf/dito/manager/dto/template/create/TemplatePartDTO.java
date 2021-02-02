package com.itextpdf.dito.manager.dto.template.create;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;

public class TemplatePartDTO {
    @NotBlank
    @Schema(example = "Header for composition template")
    private String templateName;
    private String condition;

    //settings
    @JsonProperty("new_page")
    private Boolean startOnNewPage;

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
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
}

package com.itextpdf.dito.manager.dto.template.create;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;

public class TemplateCreateRequestDTO {
    @NotBlank
    @Schema(example = "My-template")
    private String name;
    @NotBlank
    @Schema(example = "standard")
    private String type;
    @Schema(example = "some-data-collection")
    private String dataCollection;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDataCollection() {
        return dataCollection;
    }

    public void setDataCollection(String dataCollection) {
        this.dataCollection = dataCollection;
    }
}

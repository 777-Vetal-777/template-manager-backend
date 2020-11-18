package com.itextpdf.dito.manager.dto.template.create;

import javax.validation.constraints.NotBlank;

public class TemplateCreateRequestDTO {
    @NotBlank
    private String name;
    @NotBlank
    private String type;

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
}

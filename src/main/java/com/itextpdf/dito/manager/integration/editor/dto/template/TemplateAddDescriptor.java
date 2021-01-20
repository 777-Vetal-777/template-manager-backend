package com.itextpdf.dito.manager.integration.editor.dto.template;

import javax.validation.constraints.NotBlank;

public class TemplateAddDescriptor {
    @NotBlank
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

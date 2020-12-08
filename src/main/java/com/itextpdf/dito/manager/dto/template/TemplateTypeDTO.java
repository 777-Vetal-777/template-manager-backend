package com.itextpdf.dito.manager.dto.template;

public class TemplateTypeDTO {
    private String name;

    public TemplateTypeDTO() {

    }

    public TemplateTypeDTO(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

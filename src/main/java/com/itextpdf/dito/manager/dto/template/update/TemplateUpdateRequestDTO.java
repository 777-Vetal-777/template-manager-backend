package com.itextpdf.dito.manager.dto.template.update;

import javax.validation.constraints.NotBlank;

public class TemplateUpdateRequestDTO {
    @NotBlank
    String name;
    String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "TemplateUpdateRequestDTO{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}

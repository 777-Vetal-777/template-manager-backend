package com.itextpdf.dito.manager.dto.datasample.update;

import javax.validation.constraints.NotBlank;

public class DataSampleUpdateRequestDTO {
    @NotBlank
    private String name;
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "DataSampleUpdateRequestDTO{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}

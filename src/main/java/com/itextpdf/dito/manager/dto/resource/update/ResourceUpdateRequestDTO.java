package com.itextpdf.dito.manager.dto.resource.update;

import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class ResourceUpdateRequestDTO {
    @NotBlank
    private String name;
    private String description;
    @NotNull
    private ResourceTypeEnum type;

    public ResourceTypeEnum getType() {
        return type;
    }

    public void setType(ResourceTypeEnum type) {
        this.type = type;
    }

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
}

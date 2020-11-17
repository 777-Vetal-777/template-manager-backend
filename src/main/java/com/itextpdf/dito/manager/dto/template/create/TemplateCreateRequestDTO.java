package com.itextpdf.dito.manager.dto.template.create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class TemplateCreateRequestDTO {
    @NotBlank
    private String name;
    @NotBlank
    private String type;
    @NotNull
    @NotEmpty
    private byte[] data;

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

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}

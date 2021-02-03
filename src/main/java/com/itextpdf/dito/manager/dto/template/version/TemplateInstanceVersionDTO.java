package com.itextpdf.dito.manager.dto.template.version;

public class TemplateInstanceVersionDTO {
    private String name;
    private Long version;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}

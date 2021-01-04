package com.itextpdf.dito.manager.model.resource;

public class ResourceDependencyModel {
    private String name;
    private Long version;
    private Boolean active;

    public ResourceDependencyModel(String name, Long version, Boolean active) {
        this.name = name;
        this.version = version;
        this.active = active;
    }

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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
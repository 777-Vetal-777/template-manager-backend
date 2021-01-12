package com.itextpdf.dito.manager.model.resource;

import com.itextpdf.dito.manager.dto.dependency.DependencyDirectionType;
import com.itextpdf.dito.manager.dto.dependency.DependencyType;
import com.itextpdf.dito.manager.model.dependency.DependencyModel;

public class ResourceDependencyModel implements DependencyModel {
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

    @Override
    public DependencyType getDependencyType() {
        return DependencyType.IMAGE;
    }

    @Override
    public String getStage() {
        return null;
    }

    @Override
    public DependencyDirectionType getDirectionType() {
        return DependencyDirectionType.HARD;
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
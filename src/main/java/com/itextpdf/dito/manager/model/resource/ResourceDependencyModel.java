package com.itextpdf.dito.manager.model.resource;

import com.itextpdf.dito.manager.dto.dependency.DependencyDirectionType;
import com.itextpdf.dito.manager.dto.dependency.DependencyType;
import com.itextpdf.dito.manager.model.dependency.DependencyModel;

public class ResourceDependencyModel implements DependencyModel {
    private String name;
    private Long version;
    private String stage;

    public ResourceDependencyModel(String name, Long version, String stage) {
        this.name = name;
        this.version = version;
        this.stage = stage;
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
        return stage;
    }

    @Override
    public DependencyDirectionType getDirectionType() {
        return DependencyDirectionType.HARD;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

}
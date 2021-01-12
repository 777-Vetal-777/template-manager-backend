package com.itextpdf.dito.manager.dto.dependency;

import com.itextpdf.dito.manager.model.dependency.DependencyModel;

public class DependencyDTO implements DependencyModel {
    private String name;
    private Long version;
    private DependencyType dependencyType;
    private String stage;
    private DependencyDirectionType directionType;

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public DependencyType getDependencyType() {
        return dependencyType;
    }

    @Override
    public String getStage() {
        return stage;
    }

    public void setDependencyType(DependencyType dependencyType) {
        this.dependencyType = dependencyType;
    }

    public void setStage(String stage) {
        this.stage=stage;
    }

    @Override
    public DependencyDirectionType getDirectionType() {
        return directionType;
    }

    public void setDirectionType(DependencyDirectionType directionType) {
        this.directionType = directionType;
    }
}

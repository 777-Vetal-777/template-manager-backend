package com.itextpdf.dito.manager.dto.dependency.filter;

import com.itextpdf.dito.manager.dto.dependency.DependencyDirectionType;
import com.itextpdf.dito.manager.dto.dependency.DependencyType;

import java.util.List;


public class DependencyFilter {
    private String name;
    private Long version;
    private List<DependencyType> dependencyType;
    private String stage;
    private List<DependencyDirectionType> directionType;

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

    public List<DependencyType> getDependencyType() {
        return dependencyType;
    }

    public void setDependencyType(List<DependencyType> dependencyType) {
        this.dependencyType = dependencyType;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public List<DependencyDirectionType> getDirectionType() {
        return directionType;
    }

    public void setDirectionType(List<DependencyDirectionType> directionType) {
        this.directionType = directionType;
    }

}

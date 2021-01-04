package com.itextpdf.dito.manager.filter.resource.dependency;

import com.itextpdf.dito.manager.dto.dependency.DependencyDirectionType;
import com.itextpdf.dito.manager.dto.dependency.DependencyType;

import java.util.List;


public class ResourceDependencyFilter {
    private String name;
    private Long version;
    private List<DependencyType> dependencyType;
    private List<Boolean> active;
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

    public List<Boolean> getActive() {
        return active;
    }

    public void setActive(List<Boolean> active) {
        this.active = active;
    }

    public List<DependencyDirectionType> getDirectionType() {
        return directionType;
    }

    public void setDirectionType(List<DependencyDirectionType> directionType) {
        this.directionType = directionType;
    }
}

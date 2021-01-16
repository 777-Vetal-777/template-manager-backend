package com.itextpdf.dito.manager.filter.template;

import com.itextpdf.dito.manager.dto.dependency.DependencyDirectionType;
import com.itextpdf.dito.manager.dto.dependency.DependencyType;

import java.util.List;

public class TemplateDependencyFilter {
    private String dependencyName;
    private Long version;
    private List<DependencyType> dependencyType;
    private String stageName;
    private List<DependencyDirectionType> directionType;

    public String getDependencyName() {
        return dependencyName;
    }

    public void setDependencyName(String dependencyName) {
        this.dependencyName = dependencyName;
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

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    public List<DependencyDirectionType> getDirectionType() {
        return directionType;
    }

    public void setDirectionType(List<DependencyDirectionType> directionType) {
        this.directionType = directionType;
    }

}

package com.itextpdf.dito.manager.dto.dependency.filter;

import com.itextpdf.dito.manager.dto.dependency.DependencyDirectionType;
import com.itextpdf.dito.manager.dto.dependency.DependencyType;


public class DependencyFilterDTO {
    private String sort;
    private String name;
    private Long version;
    private DependencyType dependencyType;
    private Boolean active;
    private DependencyDirectionType directionType;

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public DependencyType getDependencyType() {
        return dependencyType;
    }

    public void setDependencyType(DependencyType dependencyType) {
        this.dependencyType = dependencyType;
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

    public DependencyDirectionType getDirectionType() {
        return directionType;
    }

    public void setDirectionType(DependencyDirectionType directionType) {
        this.directionType = directionType;
    }

}

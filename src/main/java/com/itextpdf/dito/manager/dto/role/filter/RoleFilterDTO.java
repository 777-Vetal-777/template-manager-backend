package com.itextpdf.dito.manager.dto.role.filter;

import com.itextpdf.dito.manager.entity.RoleType;

import java.util.List;

public class RoleFilterDTO {
    private String name;
    private List<RoleType> types;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RoleType> getTypes() {
        return types;
    }

    public void setTypes(List<RoleType> types) {
        this.types = types;
    }
}

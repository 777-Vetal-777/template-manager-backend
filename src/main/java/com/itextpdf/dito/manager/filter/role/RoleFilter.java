package com.itextpdf.dito.manager.filter.role;

import com.itextpdf.dito.manager.entity.RoleType;

import java.util.List;

public class RoleFilter {
    private String name;
    private List<RoleType> type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RoleType> getType() {
        return type;
    }

    public void setType(List<RoleType> type) {
        this.type = type;
    }
}

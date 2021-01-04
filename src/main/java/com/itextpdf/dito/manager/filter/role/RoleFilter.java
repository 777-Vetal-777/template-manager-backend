package com.itextpdf.dito.manager.filter.role;

import com.itextpdf.dito.manager.entity.RoleTypeEnum;

import java.util.List;

public class RoleFilter {
    private String name;
    private List<RoleTypeEnum> type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RoleTypeEnum> getType() {
        return type;
    }

    public void setType(List<RoleTypeEnum> type) {
        this.type = type;
    }
}

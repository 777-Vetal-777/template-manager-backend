package com.itextpdf.dito.manager.dto.role.filter;

import com.itextpdf.dito.manager.entity.RoleType;

import java.util.List;

public class RoleFilterDTO {
    private String name;
    private RoleType type;
    private List<String> users;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RoleType getType() {
        return type;
    }

    public void setType(RoleType type) {
        this.type = type;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }
}

package com.itextpdf.dito.manager.dto.permission;

public class PermissionDTO {
    private String name;
    private Boolean availableForCustomRole;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getAvailableForCustomRole() {
        return availableForCustomRole;
    }

    public void setAvailableForCustomRole(Boolean availableForCustomRole) {
        this.availableForCustomRole = availableForCustomRole;
    }
}

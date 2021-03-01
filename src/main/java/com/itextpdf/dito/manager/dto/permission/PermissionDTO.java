package com.itextpdf.dito.manager.dto.permission;

public class PermissionDTO {
    private String name;
    private Boolean optionalForCustomRole;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getOptionalForCustomRole() {
        return optionalForCustomRole;
    }

    public void setOptionalForCustomRole(Boolean optionalForCustomRole) {
        this.optionalForCustomRole = optionalForCustomRole;
    }

    @Override
    public String toString() {
        return "PermissionDTO{" +
                "name='" + name + '\'' +
                ", optionalForCustomRole=" + optionalForCustomRole +
                '}';
    }
}

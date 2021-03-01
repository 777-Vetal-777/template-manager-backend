package com.itextpdf.dito.manager.dto.resource.update;

import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class ApplyRoleRequestDTO {
    @NotBlank
    private String roleName;
    @NotNull
    private List<String> permissions;

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    @Override
    public String toString() {
        return "ApplyRoleRequestDTO{" +
                "roleName='" + roleName + '\'' +
                ", permissions=" + permissions +
                '}';
    }
}

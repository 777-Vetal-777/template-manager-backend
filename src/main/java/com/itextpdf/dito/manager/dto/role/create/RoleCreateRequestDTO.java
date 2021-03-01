package com.itextpdf.dito.manager.dto.role.create;

import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

public class RoleCreateRequestDTO {
    @NotBlank
    private String name;
    @NotEmpty
    private List<@NotBlank String> permissions;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    @Override
    public String toString() {
        return "RoleCreateRequestDTO{" +
                "name='" + name + '\'' +
                ", permissions=" + permissions +
                '}';
    }
}

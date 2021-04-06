package com.itextpdf.dito.manager.dto.role;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

public abstract class AbstractRoleModifyRequestDTO {
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
        return this.getClass().getName() + "{" +
                "name='" + name + '\'' +
                ", permissions=" + permissions +
                '}';
    }
}

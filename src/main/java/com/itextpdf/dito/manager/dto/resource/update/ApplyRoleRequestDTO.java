package com.itextpdf.dito.manager.dto.resource.update;

import javax.validation.constraints.NotBlank;

public class ApplyRoleRequestDTO {
    @NotBlank
    private String roleName;

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}

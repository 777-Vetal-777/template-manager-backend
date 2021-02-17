package com.itextpdf.dito.manager.dto.user.update;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class UpdatePasswordRequestDTO {
    @NotEmpty
    @Size(min = 12, max = 64)
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

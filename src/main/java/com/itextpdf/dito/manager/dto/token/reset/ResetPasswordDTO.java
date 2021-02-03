package com.itextpdf.dito.manager.dto.token.reset;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class ResetPasswordDTO {
    @NotBlank
    private String token;
    @NotEmpty
    @Size(min = 12, max = 64)
    private String password;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

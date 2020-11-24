package com.itextpdf.dito.manager.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;

public class AuthenticationRequestDTO {
    @NotBlank
    @Schema(example = "admin@email.com")
    private String login;
    @NotBlank
    @Schema(example = "admin@email.com")
    private String password;

    public AuthenticationRequestDTO(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

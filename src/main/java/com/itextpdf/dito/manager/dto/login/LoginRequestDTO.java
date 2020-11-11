package com.itextpdf.dito.manager.dto.login;

import javax.validation.constraints.NotNull;

public class LoginRequestDTO {
    @NotNull
    private String login;
    @NotNull
    private String password;

    public LoginRequestDTO(@NotNull String login, @NotNull String password) {
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

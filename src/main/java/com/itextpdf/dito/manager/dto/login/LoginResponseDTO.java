package com.itextpdf.dito.manager.dto.login;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;

public class LoginResponseDTO {
    private String token;
    private String login;
    private Collection<? extends GrantedAuthority> authorities;

    public LoginResponseDTO() {

    }

    public LoginResponseDTO(String token, String login,
            Collection<? extends GrantedAuthority> authorities) {
        this.token = token;
        this.login = login;
        this.authorities = authorities;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }
}

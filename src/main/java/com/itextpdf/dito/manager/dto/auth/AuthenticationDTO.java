package com.itextpdf.dito.manager.dto.auth;

import java.util.Set;

public class AuthenticationDTO {
    private String accessToken;
    private String refreshToken;
    private Set<String> authorities;

    public AuthenticationDTO(String accessToken, String refreshToken, Set<String> authorities) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.authorities = authorities;
    }

    public AuthenticationDTO() {
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Set<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<String> authorities) {
        this.authorities = authorities;
    }
}

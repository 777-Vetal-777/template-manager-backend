package com.itextpdf.dito.manager.dto.token;

public class TokenDTO {
    private String token;

    public TokenDTO(final String token) {
        this.token = token;
    }

    public TokenDTO() {

    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

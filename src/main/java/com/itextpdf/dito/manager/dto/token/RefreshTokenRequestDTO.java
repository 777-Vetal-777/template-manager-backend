package com.itextpdf.dito.manager.dto.token;

import javax.validation.constraints.NotBlank;

public class RefreshTokenRequestDTO {
    @NotBlank
    private String refreshToken;

    public RefreshTokenRequestDTO(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public RefreshTokenRequestDTO() {
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}

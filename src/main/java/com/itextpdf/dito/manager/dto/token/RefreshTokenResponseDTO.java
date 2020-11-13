package com.itextpdf.dito.manager.dto.token;


public class RefreshTokenResponseDTO {
    private String refreshedAccessToken;

    public RefreshTokenResponseDTO(String refreshedAccessToken) {
        this.refreshedAccessToken = refreshedAccessToken;
    }

    public RefreshTokenResponseDTO() {
    }

    public String getRefreshedAccessToken() {
        return refreshedAccessToken;
    }

    public void setRefreshedAccessToken(String refreshedAccessToken) {
        this.refreshedAccessToken = refreshedAccessToken;
    }
}

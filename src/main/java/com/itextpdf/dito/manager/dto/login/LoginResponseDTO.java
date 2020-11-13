package com.itextpdf.dito.manager.dto.login;

public class LoginResponseDTO extends AccessTokenResponseDTO {
    private String refreshToken;

    public LoginResponseDTO(String accessToken, String refreshToken) {
        super(accessToken);
        this.refreshToken = refreshToken;
    }

    public LoginResponseDTO() {
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}

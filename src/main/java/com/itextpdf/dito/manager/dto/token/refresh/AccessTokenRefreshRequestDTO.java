package com.itextpdf.dito.manager.dto.token.refresh;

import com.itextpdf.dito.manager.dto.token.TokenDTO;

import javax.validation.Valid;

public class AccessTokenRefreshRequestDTO {
    @Valid
    private TokenDTO refreshToken;

    public AccessTokenRefreshRequestDTO(
            @Valid TokenDTO refreshToken) {
        this.refreshToken = refreshToken;
    }

    public AccessTokenRefreshRequestDTO() {

    }

    public TokenDTO getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(TokenDTO refreshToken) {
        this.refreshToken = refreshToken;
    }
}

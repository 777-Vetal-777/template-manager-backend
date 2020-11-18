package com.itextpdf.dito.manager.controller.token.impl;

import com.itextpdf.dito.manager.controller.token.TokenController;
import com.itextpdf.dito.manager.dto.token.refresh.RefreshTokenRequestDTO;
import com.itextpdf.dito.manager.dto.token.refresh.RefreshTokenResponseDTO;
import com.itextpdf.dito.manager.exception.InvalidRefreshTokenException;
import com.itextpdf.dito.manager.service.token.TokenService;

import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenControllerImpl implements TokenController {
    private final TokenService tokenService;

    public TokenControllerImpl(final TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public ResponseEntity<RefreshTokenResponseDTO> refreshToken(@Valid RefreshTokenRequestDTO refreshTokenRequestDTO)
            throws InvalidRefreshTokenException {
        final String refreshedAccessToken = tokenService.refreshToken(refreshTokenRequestDTO.getRefreshToken());
        final RefreshTokenResponseDTO refreshTokenResponse = new RefreshTokenResponseDTO(refreshedAccessToken);
        return new ResponseEntity<>(refreshTokenResponse, HttpStatus.OK);
    }
}

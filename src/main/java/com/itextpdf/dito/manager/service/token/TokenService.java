package com.itextpdf.dito.manager.service.token;

import com.itextpdf.dito.manager.exception.InvalidRefreshTokenException;

public interface TokenService {
    String generateAccessToken(String subject);

    String generateRefreshToken(String subject);

    String refreshToken(String refreshToken) throws InvalidRefreshTokenException;
}

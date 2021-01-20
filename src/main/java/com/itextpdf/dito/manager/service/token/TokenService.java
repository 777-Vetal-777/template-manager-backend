package com.itextpdf.dito.manager.service.token;

import com.itextpdf.dito.manager.exception.token.InvalidRefreshTokenException;

import java.util.Date;

public interface TokenService {
    String generateAccessToken(String subject);

    String generateRefreshToken(String subject);

    String refreshToken(String refreshToken) throws InvalidRefreshTokenException;

    boolean isTokenIssuedAfterUserChanges(String token, Date userChangesDate);

    String getTokenForEditor(String subject);
}

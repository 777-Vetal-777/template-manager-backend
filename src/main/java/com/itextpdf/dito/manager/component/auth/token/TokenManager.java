package com.itextpdf.dito.manager.component.auth.token;

import org.springframework.security.core.Authentication;

public interface TokenManager {
    String generateAccessToken(Authentication authentication);

    String generateRefreshToken(Authentication authentication);

    String getAccessTokenByRefreshToken(String refreshToken);

    boolean validate(String token);

    String getSubject(String token);
}

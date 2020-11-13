package com.itextpdf.dito.manager.service.auth.impl;

import com.itextpdf.dito.manager.component.auth.token.TokenManager;
import com.itextpdf.dito.manager.dto.login.AccessTokenResponseDTO;
import com.itextpdf.dito.manager.dto.login.LoginResponseDTO;
import com.itextpdf.dito.manager.service.auth.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final TokenManager jwtProvider;

    public AuthServiceImpl(final AuthenticationManager authenticationManager,
                           final TokenManager jwtProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public LoginResponseDTO authenticate(String username, String password) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        final String accessToken = jwtProvider.generateAccessToken(authentication);
        final String refreshToken = jwtProvider.generateRefreshToken(authentication);
        return new LoginResponseDTO(accessToken, refreshToken);
    }

    @Override
    public AccessTokenResponseDTO refreshToken(String refreshToken) {
        return new AccessTokenResponseDTO(jwtProvider.getAccessTokenByRefreshToken(refreshToken));
    }
}

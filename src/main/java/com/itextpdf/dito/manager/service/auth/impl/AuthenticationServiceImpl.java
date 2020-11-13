package com.itextpdf.dito.manager.service.auth.impl;

import com.itextpdf.dito.manager.dto.auth.AuthenticationResponseDTO;
import com.itextpdf.dito.manager.service.auth.AuthenticationService;
import com.itextpdf.dito.manager.service.token.TokenService;

import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthenticationServiceImpl(final AuthenticationManager authenticationManager,
            final TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @Override
    public AuthenticationResponseDTO authenticate(final String subject, final String password) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(subject, password));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return collectAuthenticationResponse(authentication);
    }

    private AuthenticationResponseDTO collectAuthenticationResponse(Authentication authentication) {
        AuthenticationResponseDTO result;

        final String subject = authentication.getName();
        final String accessToken = tokenService.generateAccessToken(subject);
        final String refreshToken = tokenService.generateRefreshToken(subject);
        final Set<String> authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        result = new AuthenticationResponseDTO(accessToken, refreshToken, authorities);

        return result;
    }
}

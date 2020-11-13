package com.itextpdf.dito.manager.service.token.impl;

import com.itextpdf.dito.manager.component.auth.token.builder.TokenBuilder;
import com.itextpdf.dito.manager.component.auth.token.helper.TokenHelper;
import com.itextpdf.dito.manager.exception.InvalidRefreshTokenException;
import com.itextpdf.dito.manager.service.token.TokenService;

import org.springframework.stereotype.Service;

@Service
public class TokenServiceImpl implements TokenService {
    private final TokenBuilder tokenBuilder;
    private final TokenHelper tokenHelper;

    public TokenServiceImpl(final TokenBuilder tokenBuilder, final TokenHelper tokenHelper) {
        this.tokenBuilder = tokenBuilder;
        this.tokenHelper = tokenHelper;
    }

    @Override
    public String generateAccessToken(final String subject) {
        return tokenBuilder.buildAccessToken(subject);
    }

    @Override
    public String generateRefreshToken(final String subject) {
        return tokenBuilder.buildRefreshToken(subject);
    }

    @Override
    public String refreshToken(String refreshToken) throws InvalidRefreshTokenException {
        String result;

        if (tokenHelper.isValid(refreshToken)) {
            final String subject = tokenHelper.getSubject(refreshToken);
            result = tokenBuilder.buildAccessToken(subject);
        } else {
            throw new InvalidRefreshTokenException();
        }

        return result;
    }
}

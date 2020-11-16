package com.itextpdf.dito.manager.service.token.impl;

import com.itextpdf.dito.manager.component.auth.token.builder.TokenBuilder;
import com.itextpdf.dito.manager.component.auth.token.builder.impl.JwtAccessTokenBuilder;
import com.itextpdf.dito.manager.component.auth.token.builder.impl.JwtRefreshTokenBuilder;
import com.itextpdf.dito.manager.component.auth.token.helper.TokenHelper;
import com.itextpdf.dito.manager.component.auth.token.helper.impl.JwtRefreshTokenHelper;
import com.itextpdf.dito.manager.exception.InvalidRefreshTokenException;
import com.itextpdf.dito.manager.service.token.TokenService;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class TokenServiceImpl implements TokenService {
    private final TokenHelper refreshTokenHelper;
    private final TokenBuilder accessTokenBuilder;
    private final TokenBuilder refreshTokenBuilder;

    public TokenServiceImpl(
            final @Qualifier(JwtRefreshTokenHelper.BEAN_ID) TokenHelper refreshTokenHelper,
            final @Qualifier(JwtAccessTokenBuilder.BEAN_ID) TokenBuilder accessTokenBuilder,
            final @Qualifier(JwtRefreshTokenBuilder.BEAN_ID) TokenBuilder refreshTokenBuilder) {
        this.refreshTokenHelper = refreshTokenHelper;
        this.accessTokenBuilder = accessTokenBuilder;
        this.refreshTokenBuilder = refreshTokenBuilder;
    }

    @Override
    public String generateAccessToken(final String subject) {
        return accessTokenBuilder.build(subject);
    }

    @Override
    public String generateRefreshToken(final String subject) {
        return refreshTokenBuilder.build(subject);
    }

    @Override
    public String refreshToken(String refreshToken) throws InvalidRefreshTokenException {
        String result;

        if (refreshTokenHelper.isValid(refreshToken)) {
            final String subject = refreshTokenHelper.getSubject(refreshToken);
            result = accessTokenBuilder.build(subject);
        } else {
            throw new InvalidRefreshTokenException();
        }

        return result;
    }
}

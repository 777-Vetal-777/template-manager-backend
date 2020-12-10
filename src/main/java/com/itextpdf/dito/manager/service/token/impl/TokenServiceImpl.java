package com.itextpdf.dito.manager.service.token.impl;

import com.itextpdf.dito.manager.component.auth.token.builder.TokenBuilder;
import com.itextpdf.dito.manager.component.auth.token.builder.impl.JwtAccessTokenBuilder;
import com.itextpdf.dito.manager.component.auth.token.builder.impl.JwtRefreshTokenBuilder;
import com.itextpdf.dito.manager.component.auth.token.helper.TokenHelper;
import com.itextpdf.dito.manager.component.auth.token.helper.impl.JwtRefreshTokenHelper;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.exception.token.InvalidRefreshTokenException;
import com.itextpdf.dito.manager.service.token.TokenService;
import com.itextpdf.dito.manager.service.user.UserService;

import java.util.Date;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class TokenServiceImpl implements TokenService {
    private final TokenHelper refreshTokenHelper;
    private final TokenBuilder accessTokenBuilder;
    private final TokenBuilder refreshTokenBuilder;
    private final UserService userService;

    public TokenServiceImpl(
            final @Qualifier(JwtRefreshTokenHelper.BEAN_ID) TokenHelper refreshTokenHelper,
            final @Qualifier(JwtAccessTokenBuilder.BEAN_ID) TokenBuilder accessTokenBuilder,
            final @Qualifier(JwtRefreshTokenBuilder.BEAN_ID) TokenBuilder refreshTokenBuilder,
            final UserService userService) {
        this.refreshTokenHelper = refreshTokenHelper;
        this.accessTokenBuilder = accessTokenBuilder;
        this.refreshTokenBuilder = refreshTokenBuilder;
        this.userService = userService;
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

        if (!refreshTokenHelper.isValid(refreshToken)) {
            throw new InvalidRefreshTokenException();
        }

        final String subject = refreshTokenHelper.getSubject(refreshToken);

        // Check that token was issued after user's data changes were performed.
        final UserEntity userEntity = userService.findByEmail(subject);
        final boolean legal = isTokenIssuedAfterUserChanges(refreshToken, userEntity.getModifiedAt());
        if (!legal) {
            throw new InvalidRefreshTokenException();
        }

        result = accessTokenBuilder.build(subject);

        return result;
    }

    @Override
    public boolean isTokenIssuedAfterUserChanges(String token, String email) {
        final Date lastUserChangesDate = userService.findByEmail(email).getModifiedAt();
        return isTokenIssuedAfterUserChanges(token, lastUserChangesDate);
    }

    @Override
    public boolean isTokenIssuedAfterUserChanges(String token, Date userChangesDate) {
        return refreshTokenHelper.isTokenWasIssuedAfter(token, userChangesDate);
    }
}

package com.itextpdf.dito.manager.service.token.impl;

import com.itextpdf.dito.manager.component.auth.token.builder.TokenBuilder;
import com.itextpdf.dito.manager.component.auth.token.builder.impl.JwtAccessTokenBuilder;
import com.itextpdf.dito.manager.component.auth.token.builder.impl.JwtEditorTokenBuilder;
import com.itextpdf.dito.manager.component.auth.token.builder.impl.JwtRefreshTokenBuilder;
import com.itextpdf.dito.manager.component.auth.token.builder.impl.JwtResetPasswordTokenBuilder;
import com.itextpdf.dito.manager.component.auth.token.helper.TokenHelper;
import com.itextpdf.dito.manager.component.auth.token.helper.impl.JwtRefreshTokenHelper;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.exception.token.InvalidRefreshTokenException;
import com.itextpdf.dito.manager.exception.token.InvalidResetPasswordTokenException;
import com.itextpdf.dito.manager.exception.user.UserNotFoundOrNotActiveException;
import com.itextpdf.dito.manager.repository.user.UserRepository;
import com.itextpdf.dito.manager.service.token.TokenService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TokenServiceImpl implements TokenService {
    private final TokenHelper refreshTokenHelper;
    private final TokenBuilder accessTokenBuilder;
    private final TokenBuilder refreshTokenBuilder;
    private final TokenBuilder editorTokenBuilder;
    private final TokenBuilder resetPasswordTokenBuilder;
    private final UserRepository userRepository;
    @Value("${security.jwt.private-key}")
    private String privateKey;

    public TokenServiceImpl(
            final @Qualifier(JwtRefreshTokenHelper.BEAN_ID) TokenHelper refreshTokenHelper,
            final @Qualifier(JwtAccessTokenBuilder.BEAN_ID) TokenBuilder accessTokenBuilder,
            final @Qualifier(JwtRefreshTokenBuilder.BEAN_ID) TokenBuilder refreshTokenBuilder,
            final @Qualifier(JwtEditorTokenBuilder.BEAN_ID) TokenBuilder editorTokenBuilder,
            final @Qualifier(JwtResetPasswordTokenBuilder.BEAN_ID) TokenBuilder resetPasswordTokenBuilder,
            final UserRepository userRepository) {
        this.refreshTokenHelper = refreshTokenHelper;
        this.accessTokenBuilder = accessTokenBuilder;
        this.refreshTokenBuilder = refreshTokenBuilder;
        this.editorTokenBuilder = editorTokenBuilder;
        this.resetPasswordTokenBuilder = resetPasswordTokenBuilder;
        this.userRepository = userRepository;
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
        final UserEntity userEntity = userRepository.findByEmailAndActiveTrue(subject).orElseThrow(() -> new UserNotFoundOrNotActiveException(subject));
        final boolean legal = isTokenIssuedAfterUserChanges(refreshToken, userEntity.getModifiedAt());
        if (!legal) {
            throw new InvalidRefreshTokenException();
        }

        result = accessTokenBuilder.build(subject);

        return result;
    }

    @Override
    public boolean isTokenIssuedAfterUserChanges(String token, Date userChangesDate) {
        return refreshTokenHelper.isTokenWasIssuedAfter(token, userChangesDate);
    }

    @Override
    public String getTokenForEditor(final String subject) {
        return editorTokenBuilder.build(subject);
    }

    @Override
    public String generateResetPasswordToken(final UserEntity userEntity) {
        final String token = resetPasswordTokenBuilder.build(userEntity.getEmail());
        final Claims claims = getTokenBody(token);
        userEntity.setResetPasswordTokenDate(claims.getIssuedAt());
        userRepository.save(userEntity);
        return token;
    }

    @Override
    public Optional<UserEntity> checkResetPasswordToken(final String token) {
        final Claims body = getTokenBody(token);
        final Date expirationDate = body.getExpiration();
        final Date createdDateToken = body.getIssuedAt();
        final String email = body.getSubject();
        final Optional<UserEntity> userEntity = userRepository.findByEmail(email);
        boolean activeToken = false;
        if (userEntity.isPresent()) {
            Date createdDateTokenDb = userEntity.get().getResetPasswordTokenDate();
            if (createdDateTokenDb == null) {
                throw new InvalidResetPasswordTokenException();
            }
            Instant instant = createdDateTokenDb.toInstant();
            instant = instant.truncatedTo(ChronoUnit.SECONDS);
            createdDateTokenDb = Date.from(instant);

            if (createdDateTokenDb.equals(createdDateToken) && expirationDate.after(new Date())) {
                activeToken = true;
            }
        }

        return activeToken
                ? userEntity
                : Optional.empty();
    }

    private Claims getTokenBody(final String token) {
        final Claims body = Jwts.parser()
                .setSigningKey(privateKey)
                .parseClaimsJws(token.replace(JwtResetPasswordTokenBuilder.BEAN_ID, ""))
                .getBody();
        return body;
    }
}

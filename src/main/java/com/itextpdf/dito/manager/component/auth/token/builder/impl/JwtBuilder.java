package com.itextpdf.dito.manager.component.auth.token.builder.impl;

import com.itextpdf.dito.manager.component.auth.token.builder.TokenBuilder;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtBuilder implements TokenBuilder {
    @Value("${security.jwt.private-key}")
    private String privateKey;

    @Value("${security.jwt.access-token.time-to-live}")
    private int accessTokenTimeToLive;

    @Value("${security.jwt.refresh-token.time-to-live}")
    private int refreshTokenTimeToLive;

    @Override
    public String buildAccessToken(final String subject) {
        return buildToken(subject, accessTokenTimeToLive);
    }

    @Override
    public String buildRefreshToken(final String subject) {
        return buildToken(subject, refreshTokenTimeToLive);
    }

    private String buildToken(final String email, final int tokenTimeToLive) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + tokenTimeToLive * 1000))
                .signWith(SignatureAlgorithm.HS512, privateKey)
                .compact();
    }
}

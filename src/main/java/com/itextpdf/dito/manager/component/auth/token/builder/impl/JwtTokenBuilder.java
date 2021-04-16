package com.itextpdf.dito.manager.component.auth.token.builder.impl;

import com.itextpdf.dito.manager.component.auth.token.builder.TokenBuilder;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;

public abstract class JwtTokenBuilder implements TokenBuilder {
    @Value("${security.jwt.private-key}")
    private String privateKey;

    private Map<String, Object> claims;

    protected void init() {
        claims = new HashMap<>();
        claims.put(getIdAlias(), getId());
    }

    @Override
    public String build(final String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + getTimeToLive() * 1000L))
                .signWith(SignatureAlgorithm.HS512, privateKey)
                .compact();
    }

    protected abstract String getIdAlias();

    protected abstract String getId();

    protected abstract int getTimeToLive();
}

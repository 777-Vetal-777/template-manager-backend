package com.itextpdf.dito.manager.component.auth.token.impl;

import com.itextpdf.dito.manager.component.auth.token.TokenManager;
import com.itextpdf.dito.manager.entity.UserEntity;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class JwtManagerImpl implements TokenManager {
    private static final Logger log = LogManager.getLogger(JwtManagerImpl.class);

    @Value("${security.jwt.private-key}")
    private String privateKey;

    @Value("${security.jwt.time-to-live}")
    private int timeToLive;

    @Override
    public String generate(final Authentication authentication) {
        UserEntity principal = (UserEntity) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((principal.getEmail()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + timeToLive * 1000))
                .signWith(SignatureAlgorithm.HS512, privateKey)
                .compact();
    }

    @Override
    public boolean validate(final String authToken) {
        boolean result = false;

        try {
            Jwts.parser().setSigningKey(privateKey).parseClaimsJws(authToken);
            result = true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature -> Message: {}", e);
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token -> Message: {}", e);
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token -> Message: {}", e);
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token -> Message: {}", e);
        } catch (IllegalArgumentException e) {
            log.error("Invalid JWT value -> Message: {}", e);
        }

        return result;
    }

    @Override
    public String getSubject(final String token) {
        return Jwts.parser()
                .setSigningKey(privateKey)
                .parseClaimsJws(token)
                .getBody().getSubject();
    }
}

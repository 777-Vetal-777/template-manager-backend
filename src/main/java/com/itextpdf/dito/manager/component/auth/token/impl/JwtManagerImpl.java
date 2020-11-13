package com.itextpdf.dito.manager.component.auth.token.impl;

import com.itextpdf.dito.manager.component.auth.token.TokenManager;
import com.itextpdf.dito.manager.entity.UserEntity;

import com.itextpdf.dito.manager.exception.InvalidRefreshTokenException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class JwtManagerImpl implements TokenManager {
    private static final Logger log = LogManager.getLogger(JwtManagerImpl.class);

    @Value("${security.jwt.private-key}")
    private String privateKey;

    @Value("${security.jwt.access-token.time-to-live}")
    private int accessTokenTime;

    @Value("${security.jwt.refresh-token.time-to-live}")
    private int refreshTokenTime;

    private final UserDetailsService userDetailsService;

    public JwtManagerImpl(final UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public String generateAccessToken(final Authentication authentication) {
        return generateAccessToken((UserEntity) authentication.getPrincipal());
    }

    @Override
    public String generateRefreshToken(final Authentication authentication) {
        final UserEntity principal = (UserEntity) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((principal.getEmail()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + refreshTokenTime * 1000))
                .signWith(SignatureAlgorithm.HS512, privateKey)
                .compact();
    }

    @Override
    public String getAccessTokenByRefreshToken(String refreshToken) {
        if (!StringUtils.isEmpty(refreshToken) && validate(refreshToken)) {
            final String username = getSubject(refreshToken);
            final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            return generateAccessToken((UserEntity) userDetails);
        } else {
            throw new InvalidRefreshTokenException("Invalid or expired refresh token");
        }
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

    private String generateAccessToken(UserEntity principal) {
        final Map<String, Object> claims = new HashMap<>();
        final String commaSeparatedListOfAuthorities = principal.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        claims.put("authorities", commaSeparatedListOfAuthorities);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject((principal.getEmail()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + accessTokenTime * 1000))
                .signWith(SignatureAlgorithm.HS512, privateKey)
                .compact();
    }
}

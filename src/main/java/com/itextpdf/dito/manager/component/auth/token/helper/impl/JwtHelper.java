package com.itextpdf.dito.manager.component.auth.token.helper.impl;

import com.itextpdf.dito.manager.component.auth.token.helper.TokenHelper;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtHelper implements TokenHelper {
    private static final Logger log = LogManager.getLogger(JwtHelper.class);

    @Value("${security.jwt.private-key}")
    private String privateKey;

    @Override
    public boolean isValid(final String token) {
        boolean result = false;

        try {
            Jwts.parser().setSigningKey(privateKey).parseClaimsJws(token);
            result = true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature -> Message: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token -> Message: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token -> Message: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token -> Message: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("Invalid JWT value -> Message: {}", e.getMessage());
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

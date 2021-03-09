package com.itextpdf.dito.manager.component.auth.token.helper.impl;

import com.itextpdf.dito.manager.component.auth.token.helper.TokenHelper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

public abstract class JwtTokenHelper implements TokenHelper {
    private static final Logger log = LogManager.getLogger(JwtTokenHelper.class);

    @Value("${security.jwt.private-key}")
    private String privateKey;
    @Value("${security.jwt.id-alias}")
    private String idAlias;

    @Override
    public boolean isValid(final String token) {
        boolean result = false;

        Jws<Claims> claims = parse(token);
        if (claims != null) {
            result = claims.getBody().get(idAlias).equals(getId());
        }

        return result;
    }

    @Override
    public String getSubject(final String token) {
        return parse(token)
                .getBody()
                .getSubject();
    }

    @Override
    public boolean isTokenWasIssuedAfter(final String token, final Date date) {
        boolean result = true;

        if (date != null) {
            result = parse(token).getBody().getIssuedAt().after(date) || parse(token).getBody().getIssuedAt().equals(date);
        }

        return result;
    }

    protected Jws<Claims> parse(final String token) {
        Jws<Claims> result = null;

        try {
            result = Jwts.parser().setSigningKey(privateKey).parseClaimsJws(token);
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

    protected abstract String getId();
}

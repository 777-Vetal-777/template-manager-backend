package com.itextpdf.dito.manager.component.auth.token.extractor.impl;

import com.itextpdf.dito.manager.component.auth.token.extractor.TokenExtractor;

import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class JwtExtractor implements TokenExtractor {
    private static final Logger log = LogManager.getLogger(JwtExtractor.class);

    @Override
    public String extract(final HttpServletRequest httpServletRequest) {
        String result = null;

        final String authHeader = httpServletRequest.getHeader("Authorization");
        if (!StringUtils.isEmpty(authHeader) && authHeader.startsWith("Bearer ")) {
            result = authHeader.replace("Bearer ", "");
        } else {
            log.error("Incorrect token format: token must not be NULL and should start with 'Bearer '");
        }

        return result;
    }
}

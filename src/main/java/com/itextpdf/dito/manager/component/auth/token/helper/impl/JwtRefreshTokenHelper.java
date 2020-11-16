package com.itextpdf.dito.manager.component.auth.token.helper.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component(JwtRefreshTokenHelper.BEAN_ID)
public class JwtRefreshTokenHelper extends JwtTokenHelper {
    public static final String BEAN_ID = "jwtRefreshTokenHelper";

    @Value("${security.jwt.refresh-token.id}")
    private String id;

    @Override
    protected String getId() {
        return id;
    }
}

package com.itextpdf.dito.manager.component.auth.token.helper.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component(JwtAccessTokenHelper.BEAN_ID)
public class JwtAccessTokenHelper extends JwtTokenHelper {
    public static final String BEAN_ID = "jwtAccessTokenHelper";

    @Value("${security.jwt.access-token.id}")
    private String id;

    @Override
    protected String getId() {
        return id;
    }
}

package com.itextpdf.dito.manager.component.auth.token.builder.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component(JwtResetPasswordTokenBuilder.BEAN_ID)
public class JwtResetPasswordTokenBuilder extends JwtTokenBuilder{
    public static final String BEAN_ID = "jwtResetPasswordTokenBuilder";

    @Value("${security.jwt.id-alias}")
    private String idAlias;
    @Value("${security.jwt.reset-password-token.id}")
    private String id;
    @Value("${security.jwt.reset-password-token.time-to-live}")
    private int timeToLive;

    @PostConstruct
    public void init() {
        super.init();
    }

    @Override
    public String getIdAlias() {
        return idAlias;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public int getTimeToLive() {
        return timeToLive;
    }
}

package com.itextpdf.dito.manager.component.auth.token.builder.impl;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component(JwtRefreshTokenBuilder.BEAN_ID)
public class JwtRefreshTokenBuilder extends JwtTokenBuilder {
    public static final String BEAN_ID = "jwtRefreshTokenBuilder";

    @Value("${security.jwt.id-alias}")
    private String idAlias;
    @Value("${security.jwt.refresh-token.id}")
    private String id;
    @Value("${security.jwt.refresh-token.time-to-live}")
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

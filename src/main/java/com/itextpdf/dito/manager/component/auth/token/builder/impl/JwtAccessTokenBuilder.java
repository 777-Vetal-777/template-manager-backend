package com.itextpdf.dito.manager.component.auth.token.builder.impl;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component(JwtAccessTokenBuilder.BEAN_ID)
public class JwtAccessTokenBuilder extends JwtTokenBuilder {
    public static final String BEAN_ID = "jwtAccessTokenBuilder";

    @Value("${security.jwt.id-alias}")
    private String idAlias;
    @Value("${security.jwt.access-token.id}")
    private String id;
    @Value("${security.jwt.access-token.time-to-live}")
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

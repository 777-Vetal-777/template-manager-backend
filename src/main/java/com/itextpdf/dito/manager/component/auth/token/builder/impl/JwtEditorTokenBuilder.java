package com.itextpdf.dito.manager.component.auth.token.builder.impl;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component(JwtEditorTokenBuilder.BEAN_ID)
public class JwtEditorTokenBuilder extends JwtTokenBuilder {
    public static final String BEAN_ID = "jwtEditorTokenBuilder";

    @Value("${security.jwt.id-alias}")
    private String idAlias;
    @Value("${security.jwt.editor-token.id}")
    private String id;
    @Value("${security.jwt.editor-token.time-to-live}")
    private int timeToLive;

    @PostConstruct
    public void init() {
        super.init();
    }

    @Override
    protected String getIdAlias() {
        return idAlias;
    }

    @Override
    protected String getId() {
        return id;
    }

    @Override
    protected int getTimeToLive() {
        return timeToLive;
    }
}

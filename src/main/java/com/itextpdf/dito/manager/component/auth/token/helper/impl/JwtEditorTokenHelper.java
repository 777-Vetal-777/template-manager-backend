package com.itextpdf.dito.manager.component.auth.token.helper.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component(JwtEditorTokenHelper.BEAN_ID)
public class JwtEditorTokenHelper extends JwtTokenHelper {
    public static final String BEAN_ID = "jwtEditorTokenHelper";

    @Value("${security.jwt.editor-token.id}")
    private String id;

    @Override
    protected String getId() {
        return id;
    }
}

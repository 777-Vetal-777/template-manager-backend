package com.itextpdf.dito.manager.component.auth.token.builder;

public interface TokenBuilder {
    String buildAccessToken(String subject);

    String buildRefreshToken(String subject);
}

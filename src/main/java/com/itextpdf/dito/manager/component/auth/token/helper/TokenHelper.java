package com.itextpdf.dito.manager.component.auth.token.helper;

public interface TokenHelper {
    boolean isValid(String token);

    String getSubject(String token);
}

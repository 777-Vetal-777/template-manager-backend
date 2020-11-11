package com.itextpdf.dito.manager.component.auth.token;

import org.springframework.security.core.Authentication;

public interface TokenManager {
    String generate(Authentication authentication);

    boolean validate(String token);

    String getSubject(String token);
}

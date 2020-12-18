package com.itextpdf.dito.manager.service.auth;

import com.itextpdf.dito.manager.dto.auth.AuthenticationDTO;

public interface AuthenticationService {
    AuthenticationDTO authenticate(String username, String password);
}

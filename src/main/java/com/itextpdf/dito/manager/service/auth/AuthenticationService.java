package com.itextpdf.dito.manager.service.auth;

import com.itextpdf.dito.manager.dto.auth.AuthenticationResponseDTO;

public interface AuthenticationService {
    AuthenticationResponseDTO authenticate(String username, String password);
}

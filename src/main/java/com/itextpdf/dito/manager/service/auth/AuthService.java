package com.itextpdf.dito.manager.service.auth;

import com.itextpdf.dito.manager.dto.login.AccessTokenResponseDTO;
import com.itextpdf.dito.manager.dto.login.LoginResponseDTO;

public interface AuthService {
    LoginResponseDTO authenticate(String username, String password);

    AccessTokenResponseDTO refreshToken(String refreshToken);
}

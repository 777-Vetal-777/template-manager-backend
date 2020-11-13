package com.itextpdf.dito.manager.controller.login.impl;

import com.itextpdf.dito.manager.controller.login.LoginController;
import com.itextpdf.dito.manager.dto.login.AccessTokenResponseDTO;
import com.itextpdf.dito.manager.dto.login.LoginRequestDTO;
import com.itextpdf.dito.manager.dto.login.LoginResponseDTO;

import javax.validation.Valid;

import com.itextpdf.dito.manager.dto.login.RefreshTokenRequestDto;
import com.itextpdf.dito.manager.service.auth.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginControllerImpl implements LoginController {

    private final AuthService authService;

    public LoginControllerImpl(final AuthService authService) {
        this.authService = authService;
    }

    public ResponseEntity<LoginResponseDTO> login(final @Valid LoginRequestDTO loginRequest) {
        return new ResponseEntity<>(authService.authenticate(loginRequest.getLogin(), loginRequest.getPassword()),
                HttpStatus.OK);
    }

    @Override
    public ResponseEntity<AccessTokenResponseDTO> refreshToken(RefreshTokenRequestDto requestDto) {
        return new ResponseEntity<>(authService.refreshToken(requestDto.getRefreshToken()), HttpStatus.OK);
    }
}

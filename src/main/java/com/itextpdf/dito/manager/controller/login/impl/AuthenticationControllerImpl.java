package com.itextpdf.dito.manager.controller.login.impl;

import com.itextpdf.dito.manager.controller.login.AuthenticationController;
import com.itextpdf.dito.manager.dto.auth.AuthenticationRequestDTO;
import com.itextpdf.dito.manager.dto.auth.AuthenticationResponseDTO;
import com.itextpdf.dito.manager.service.auth.AuthenticationService;

import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationControllerImpl implements AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationControllerImpl(final AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public ResponseEntity<AuthenticationResponseDTO> login(
            final @Valid AuthenticationRequestDTO authenticationRequestDTO) {
        return new ResponseEntity<>(authenticationService
                .authenticate(authenticationRequestDTO.getLogin(), authenticationRequestDTO.getPassword()),
                HttpStatus.OK);
    }
}

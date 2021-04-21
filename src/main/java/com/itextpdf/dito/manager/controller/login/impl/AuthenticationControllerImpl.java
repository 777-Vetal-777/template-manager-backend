package com.itextpdf.dito.manager.controller.login.impl;

import com.itextpdf.dito.manager.controller.AbstractController;
import com.itextpdf.dito.manager.controller.login.AuthenticationController;
import com.itextpdf.dito.manager.dto.auth.AuthenticationDTO;
import com.itextpdf.dito.manager.dto.auth.AuthenticationRequestDTO;
import com.itextpdf.dito.manager.service.auth.AuthenticationService;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationControllerImpl extends AbstractController implements AuthenticationController {
    private static final Logger log = LogManager.getLogger(AuthenticationControllerImpl.class);

    private final AuthenticationService authenticationService;

    public AuthenticationControllerImpl(final AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public ResponseEntity<AuthenticationDTO> login(
            final @Valid AuthenticationRequestDTO authenticationRequestDTO) {
        log.info("Login using userName: {} and password was started", authenticationRequestDTO.getLogin());
        final String lowerName = authenticationRequestDTO.getLogin().toLowerCase();
        final AuthenticationDTO authentication = authenticationService.authenticate(lowerName, authenticationRequestDTO.getPassword());
        log.info("Login using userName: {} and password was finished successfully", lowerName);
        return new ResponseEntity<>(authentication, HttpStatus.OK);
    }
}

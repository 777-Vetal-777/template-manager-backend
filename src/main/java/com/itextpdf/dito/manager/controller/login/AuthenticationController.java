package com.itextpdf.dito.manager.controller.login;

import com.itextpdf.dito.manager.dto.auth.AuthenticationRequestDTO;
import com.itextpdf.dito.manager.dto.auth.AuthenticationResponseDTO;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(AuthenticationController.BASE_NAME)
public interface AuthenticationController {
    String MAJOR_VERSION = "/v1";
    String BASE_NAME = MAJOR_VERSION + "/authentication";

    @PostMapping
    ResponseEntity<AuthenticationResponseDTO> login(@RequestBody AuthenticationRequestDTO loginRequest);

}

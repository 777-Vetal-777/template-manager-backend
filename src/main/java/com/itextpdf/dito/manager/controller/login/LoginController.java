package com.itextpdf.dito.manager.controller.login;

import com.itextpdf.dito.manager.dto.login.AccessTokenResponseDTO;
import com.itextpdf.dito.manager.dto.login.LoginRequestDTO;
import com.itextpdf.dito.manager.dto.login.LoginResponseDTO;

import com.itextpdf.dito.manager.dto.login.RefreshTokenRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(LoginController.BASE_NAME)
public interface LoginController {
    String MAJOR_VERSION = "/v1";
    String BASE_NAME = MAJOR_VERSION + "/login";

    @PostMapping
    ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequest);

    @PostMapping("/refresh-token")
    ResponseEntity<AccessTokenResponseDTO> refreshToken(@RequestBody RefreshTokenRequestDto loginRequest);

}

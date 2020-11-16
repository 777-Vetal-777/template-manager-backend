package com.itextpdf.dito.manager.controller.token;

import com.itextpdf.dito.manager.dto.token.RefreshTokenRequestDTO;
import com.itextpdf.dito.manager.dto.token.RefreshTokenResponseDTO;
import com.itextpdf.dito.manager.exception.InvalidRefreshTokenException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(TokenController.BASE_NAME)
public interface TokenController {
    String MAJOR_VERSION = "/v1";
    String BASE_NAME = MAJOR_VERSION + "/tokens";
    String REFRESH_ENDPOINT = "/refresh";

    @PostMapping(REFRESH_ENDPOINT)
    ResponseEntity<RefreshTokenResponseDTO> refreshToken(@RequestBody RefreshTokenRequestDTO loginRequest)
            throws InvalidRefreshTokenException;

}

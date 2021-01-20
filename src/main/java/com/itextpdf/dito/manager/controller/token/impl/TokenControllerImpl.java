package com.itextpdf.dito.manager.controller.token.impl;

import com.itextpdf.dito.manager.controller.AbstractController;
import com.itextpdf.dito.manager.controller.token.TokenController;
import com.itextpdf.dito.manager.dto.token.TokenDTO;
import com.itextpdf.dito.manager.dto.token.refresh.AccessTokenRefreshRequestDTO;
import com.itextpdf.dito.manager.exception.token.InvalidRefreshTokenException;
import com.itextpdf.dito.manager.service.token.TokenService;

import java.security.Principal;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenControllerImpl extends AbstractController implements TokenController {
    private final TokenService tokenService;

    public TokenControllerImpl(final TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public ResponseEntity<TokenDTO> refresh(@Valid AccessTokenRefreshRequestDTO accessTokenRefreshRequestDTO)
            throws InvalidRefreshTokenException {
        final String refreshedAccessToken = tokenService
                .refreshToken(accessTokenRefreshRequestDTO.getRefreshToken().getToken());
        return new ResponseEntity<>(new TokenDTO(refreshedAccessToken), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<TokenDTO> editor(final Principal principal) {
        final String editorToken = tokenService.getTokenForEditor(principal.getName());
        return new ResponseEntity<>(new TokenDTO(editorToken), HttpStatus.OK);
    }
}

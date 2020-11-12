package com.itextpdf.dito.manager.controller.login.impl;

import com.itextpdf.dito.manager.component.auth.token.impl.JwtManagerImpl;
import com.itextpdf.dito.manager.controller.login.LoginController;
import com.itextpdf.dito.manager.dto.login.LoginRequestDTO;
import com.itextpdf.dito.manager.dto.login.LoginResponseDTO;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginControllerImpl implements LoginController {
    private final AuthenticationManager authenticationManager;
    private final JwtManagerImpl jwtProvider;

    public LoginControllerImpl(final AuthenticationManager authenticationManager,
            final JwtManagerImpl jwtProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
    }

    public ResponseEntity<LoginResponseDTO> login(final @Valid LoginRequestDTO loginRequest) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getLogin(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        final String jwt = jwtProvider.generate(authentication);
        final UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return new ResponseEntity<>(new LoginResponseDTO(jwt, userDetails.getUsername(), userDetails.getAuthorities()),
                HttpStatus.OK);
    }
}

package com.itextpdf.dito.manager.dto;

import lombok.Value;

import javax.validation.constraints.NotNull;

@Value
public class LoginRequest {
    @NotNull
    String login;
    @NotNull
    String password;
}

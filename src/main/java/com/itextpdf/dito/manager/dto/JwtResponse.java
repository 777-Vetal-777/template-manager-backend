package com.itextpdf.dito.manager.dto;

import lombok.Value;


@Value
public class JwtResponse {
    String token;
    String login;
}

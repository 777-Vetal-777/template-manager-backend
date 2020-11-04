package com.itext.ditomanager.dto;

import lombok.Value;


@Value
public class JwtResponse {
    String token;
    String login;
}

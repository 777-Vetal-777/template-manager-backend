package com.itextpdf.dito.manager.dto;

import lombok.Value;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;


@Value
public class JwtResponse {
    String token;
    String login;
    Collection<? extends GrantedAuthority> authorities;
}

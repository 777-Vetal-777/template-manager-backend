package com.itextpdf.dito.manager.dto;

import lombok.Value;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Value
public class UserCreateRequest {
    @NotNull
    @NotEmpty
    @Pattern(regexp="[A-Za-z0-9._%-+]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}")
    String email;
    @NotNull
    @NotEmpty
    String password;
    @NotNull
    @NotEmpty
    String firstName;
    @NotNull
    @NotEmpty
    String lastName;
}

package com.itextpdf.dito.manager.dto.user.create;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;


public class UserCreateRequestDTO {
    @NotBlank
    @Pattern(regexp = "[A-Za-z0-9._%-+]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}")
    @Schema(example = "user@example.com")
    String email;
    @NotBlank
    @Size(min = 12, max = 64)
    @Schema(example = "my_secret_password")
    String password;
    @NotBlank
    @Schema(example = "Harry")
    String firstName;
    @NotBlank
    @Schema(example = "Kane")
    String lastName;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}

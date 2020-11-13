package com.itextpdf.dito.manager.dto.user.create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;


public class UserCreateRequestDTO {
    @NotBlank
    @Pattern(regexp = "[A-Za-z0-9._%-+]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}")
    String email;
    @NotBlank
    String password;
    @NotBlank
    String firstName;
    @NotBlank
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

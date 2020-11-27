package com.itextpdf.dito.manager.dto.user.create;

import javax.validation.constraints.NotBlank;

public class UserUpdateRequestDTO {
    @NotBlank
    String firstName;
    @NotBlank
    String lastName;

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

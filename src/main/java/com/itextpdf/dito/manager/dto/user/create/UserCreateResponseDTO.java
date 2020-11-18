package com.itextpdf.dito.manager.dto.user.create;

import com.itextpdf.dito.manager.dto.user.UserDTO;

public class UserCreateResponseDTO {
    private UserDTO createdUser;

    public UserCreateResponseDTO() {
    }

    public UserCreateResponseDTO(final UserDTO userDTO) {
        this.createdUser = userDTO;
    }

    public UserDTO getCreatedUser() {
        return createdUser;
    }

    public void setCreatedUser(UserDTO createdUser) {
        this.createdUser = createdUser;
    }
}

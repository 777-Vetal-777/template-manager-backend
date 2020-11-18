package com.itextpdf.dito.manager.dto.user.list;

import com.itextpdf.dito.manager.dto.user.UserDTO;

import org.springframework.data.domain.Page;

public class UserListResponseDTO {
    private Page<UserDTO> users;

    public UserListResponseDTO() {

    }

    public UserListResponseDTO(Page<UserDTO> users) {

    }

    public Page<UserDTO> getUsers() {
        return users;
    }

    public void setUsers(Page<UserDTO> users) {
        this.users = users;
    }

}

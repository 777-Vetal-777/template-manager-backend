package com.itextpdf.dito.manager.dto.user.list;

import com.itextpdf.dito.manager.dto.user.create.UserCreateResponseDTO;
import com.itextpdf.dito.manager.entity.UserEntity;

import java.util.List;

public class UserListResponseDTO {
    private List<UserCreateResponseDTO> users;

    public UserListResponseDTO() {
    }

    public UserListResponseDTO(List<UserCreateResponseDTO> users) {
        this.users = users;
    }

    public List<UserCreateResponseDTO> getUsers() {
        return users;
    }

    public void setUsers(List<UserCreateResponseDTO> users) {
        this.users = users;
    }
}

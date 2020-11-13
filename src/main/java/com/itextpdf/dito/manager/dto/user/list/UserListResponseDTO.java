package com.itextpdf.dito.manager.dto.user.list;

import com.itextpdf.dito.manager.entity.UserEntity;

import java.util.List;

public class UserListResponseDTO {
    private List<UserEntity> userEntities;

    public UserListResponseDTO() {

    }

    public UserListResponseDTO(List<UserEntity> userEntities) {
        this.userEntities = userEntities;
    }

    public List<UserEntity> getUserEntities() {
        return userEntities;
    }

    public void setUserEntities(List<UserEntity> userEntities) {
        this.userEntities = userEntities;
    }
}

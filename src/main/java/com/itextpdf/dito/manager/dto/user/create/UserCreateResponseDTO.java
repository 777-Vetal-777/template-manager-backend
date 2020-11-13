package com.itextpdf.dito.manager.dto.user.create;

import com.itextpdf.dito.manager.entity.UserEntity;

public class UserCreateResponseDTO {
    private UserEntity userEntity;

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public UserCreateResponseDTO() {

    }
    public UserCreateResponseDTO(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }
}

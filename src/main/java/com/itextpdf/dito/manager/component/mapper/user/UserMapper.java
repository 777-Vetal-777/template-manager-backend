package com.itextpdf.dito.manager.component.mapper.user;

import com.itextpdf.dito.manager.dto.user.create.UserCreateRequestDTO;
import com.itextpdf.dito.manager.entity.UserEntity;

public interface UserMapper {
    UserEntity map(UserCreateRequestDTO request);
}

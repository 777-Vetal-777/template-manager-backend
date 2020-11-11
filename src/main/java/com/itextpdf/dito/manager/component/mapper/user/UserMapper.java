package com.itextpdf.dito.manager.component.mapper.user;

import com.itextpdf.dito.manager.dto.user.UserCreateRequestDTO;
import com.itextpdf.dito.manager.entity.UserEntity;

public interface UserMapper {
    UserEntity map(UserCreateRequestDTO request);
}

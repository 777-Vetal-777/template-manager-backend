package com.itextpdf.dito.manager.component.mapper.user;

import com.itextpdf.dito.manager.dto.user.UserDTO;
import com.itextpdf.dito.manager.dto.user.create.UserCreateRequestDTO;
import com.itextpdf.dito.manager.entity.UserEntity;

import java.util.List;
import org.springframework.data.domain.Page;

public interface UserMapper {
    UserEntity map(UserCreateRequestDTO dto);

    UserDTO map(UserEntity dto);

    List<UserDTO> map(List<UserEntity> entities);

    Page<UserDTO> map(Page<UserEntity> entities);
}

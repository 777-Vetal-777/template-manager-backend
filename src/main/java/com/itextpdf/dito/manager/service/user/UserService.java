package com.itextpdf.dito.manager.service.user;

import com.itextpdf.dito.manager.dto.user.UserCreateRequestDTO;
import com.itextpdf.dito.manager.entity.UserEntity;

import java.util.List;

public interface UserService {
    UserEntity create(UserCreateRequestDTO request);

    List<UserEntity> getAll(String sortBy, Boolean desc);

    void delete(Long id);
}

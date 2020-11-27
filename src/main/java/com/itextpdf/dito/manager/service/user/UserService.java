package com.itextpdf.dito.manager.service.user;

import com.itextpdf.dito.manager.dto.user.create.UserCreateRequestDTO;
import com.itextpdf.dito.manager.dto.user.create.UserUpdateRequest;
import com.itextpdf.dito.manager.entity.UserEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    UserEntity create(UserCreateRequestDTO request);

    UserEntity findByEmail(String email);

    UserEntity updateUser(UserUpdateRequest updateRequest, String email);

    Page<UserEntity> getAll(Pageable pageable, String searchParam);

    void delete(List<String> emails);

    void lock(UserEntity user);

    UserEntity unblock(String email);
}

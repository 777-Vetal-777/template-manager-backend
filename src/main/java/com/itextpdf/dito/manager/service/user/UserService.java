package com.itextpdf.dito.manager.service.user;

import com.itextpdf.dito.manager.dto.user.create.UserCreateRequestDTO;
import com.itextpdf.dito.manager.dto.user.create.UserCreateResponseDTO;
import com.itextpdf.dito.manager.dto.user.list.UserListResponseDTO;
import com.itextpdf.dito.manager.entity.UserEntity;

import java.util.List;

public interface UserService {
    UserCreateResponseDTO create(UserCreateRequestDTO request);

    UserListResponseDTO getAll(String sortBy, Boolean desc);

    void delete(Long id);
}

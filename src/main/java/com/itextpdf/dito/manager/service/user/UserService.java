package com.itextpdf.dito.manager.service.user;

import com.itextpdf.dito.manager.dto.user.create.UserCreateRequestDTO;
import com.itextpdf.dito.manager.dto.user.update.UpdateUsersRolesActionEnum;
import com.itextpdf.dito.manager.dto.user.update.UserUpdateRequestDTO;
import com.itextpdf.dito.manager.entity.UserEntity;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserEntity create(UserCreateRequestDTO request);

    UserEntity findByEmail(String email);

    UserEntity updateUser(UserUpdateRequestDTO updateRequest, String email);

    Page<UserEntity> getAll(Pageable pageable, String searchParam);

    void updateActivationStatus(List<String> emails, boolean activateAction);

    void lock(UserEntity user);

    UserEntity unblock(String email);

    void updatePassword(String oldPassword, String newPassword, String userEmail);

    void updateUsersRoles(List<String> emails, List<String> roles,
            UpdateUsersRolesActionEnum actionEnum);
}

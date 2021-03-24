package com.itextpdf.dito.manager.service.user;

import com.itextpdf.dito.manager.dto.user.update.UpdateUsersRolesActionEnum;
import com.itextpdf.dito.manager.entity.UserEntity;

import java.util.List;

import com.itextpdf.dito.manager.filter.user.UserFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserEntity create(UserEntity request, List<String> roles, UserEntity currentUser, String frontURL);

    UserEntity findActiveUserByEmail(String email);

    UserEntity findByEmail(String email);

    UserEntity updateUser(UserEntity userEntity, String email);

    Page<UserEntity> getAll(Pageable pageable, UserFilter userFilter, String searchParam);

    void updateActivationStatus(List<String> emails, boolean activateAction);

    void lock(UserEntity user);

    UserEntity unblock(String email);

    UserEntity updatePassword(String oldPassword, String newPassword, String userEmail);

    UserEntity updatePassword(String newPassword, String userEmail, UserEntity admin, String frontURL);

    UserEntity updatePasswordSpecifiedByAdmin(String newPassword, String email);

    List<UserEntity> updateUsersRoles(List<String> emails, List<String> roles,
                                      UpdateUsersRolesActionEnum actionEnum);

    Integer calculateCountOfUsersWithOnlyOneRole(String roleName);

    void forgotPassword(String email, String frontURL);

    void resetPassword(String token, String password);

    boolean lockedUsersExist();
}

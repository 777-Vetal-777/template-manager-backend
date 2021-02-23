package com.itextpdf.dito.manager.controller.user.impl;

import com.itextpdf.dito.manager.component.mapper.user.UserMapper;
import com.itextpdf.dito.manager.controller.AbstractController;
import com.itextpdf.dito.manager.controller.user.UserController;
import com.itextpdf.dito.manager.dto.user.EmailDTO;
import com.itextpdf.dito.manager.dto.token.reset.ResetPasswordDTO;
import com.itextpdf.dito.manager.dto.user.UserDTO;
import com.itextpdf.dito.manager.dto.user.create.UserCreateRequestDTO;
import com.itextpdf.dito.manager.dto.user.unblock.UsersUnblockRequestDTO;
import com.itextpdf.dito.manager.dto.user.update.PasswordChangeRequestDTO;
import com.itextpdf.dito.manager.dto.user.update.UpdatePasswordRequestDTO;
import com.itextpdf.dito.manager.dto.user.update.UserRolesUpdateRequestDTO;
import com.itextpdf.dito.manager.dto.user.update.UserUpdateRequestDTO;
import com.itextpdf.dito.manager.dto.user.update.UsersActivateRequestDTO;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.filter.user.UserFilter;
import com.itextpdf.dito.manager.service.user.UserService;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserControllerImpl extends AbstractController implements UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    public UserControllerImpl(final UserService userService, final UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @Override
    public ResponseEntity<UserDTO> create(@Valid final UserCreateRequestDTO userCreateRequestDTO, Principal principal) {
        final UserEntity currentUser = userService.findActiveUserByEmail(principal.getName());
        final UserEntity user = userService
                .create(userMapper.map(userCreateRequestDTO), userCreateRequestDTO.getRoles(), currentUser);
        return new ResponseEntity<>(userMapper.map(user), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<UserDTO> update(final String userName, final UserUpdateRequestDTO userUpdateRequestDTO) {
        final UserDTO user = userMapper.map(userService.updateUser(userMapper.map(userUpdateRequestDTO), decodeBase64(userName)));
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<UserDTO> updatePassword(final String userName, final UpdatePasswordRequestDTO requestDTO) {
        final UserEntity userEntity = userService.updatePassword(requestDTO.getPassword(),decodeBase64(userName));
        return new ResponseEntity<>(userMapper.map(userEntity), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<UserDTO> get(final String userName, final Principal principal) {
        final UserDTO user = userMapper.map(userService.findByEmail(decodeBase64(userName)));
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Page<UserDTO>> list(final Pageable pageable, final UserFilter userFilter, final String searchParam) {
        return new ResponseEntity<>(userMapper.map(userService.getAll(pageable, userFilter, searchParam)), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> updateActivationStatus(final @Valid UsersActivateRequestDTO usersActivateRequestDTO) {
        userService.updateActivationStatus(usersActivateRequestDTO.getEmails(), usersActivateRequestDTO.isActivate());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<UserDTO> currentUser(Principal principal) {
        UserDTO user = userMapper.map(userService.findActiveUserByEmail(principal.getName()));
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<UserDTO> updateCurrentUser(final UserUpdateRequestDTO userUpdateRequestDTO,
            Principal principal) {
        final UserDTO user = userMapper
                .map(userService.updateUser(userMapper.map(userUpdateRequestDTO), principal.getName()));
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<UserDTO>> unblock(final UsersUnblockRequestDTO usersUnblockRequestDTO) {
        final List<UserDTO> unblockedUsers = usersUnblockRequestDTO.getUserEmails()
                .stream()
                .map(email -> userMapper.map(userService.unblock(email)))
                .collect(Collectors.toList());
        return new ResponseEntity<>(unblockedUsers, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<UserDTO> updatePassword(final @Valid PasswordChangeRequestDTO passwordChangeRequestDTO,
            final Principal principal) {
        final UserEntity userEntity = userService.updatePassword(passwordChangeRequestDTO.getOldPassword(),
                passwordChangeRequestDTO.getNewPassword(),
                principal.getName());
        return new ResponseEntity<>(userMapper.map(userEntity), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<UserDTO> updateAdminPasswordToUser(final UpdatePasswordRequestDTO updatePasswordRequestDTO, final Principal principal) {
        final UserEntity userEntity = userService.updatePasswordSpecifiedByAdmin(updatePasswordRequestDTO.getPassword(), principal.getName());
        return new ResponseEntity<>(userMapper.map(userEntity), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<UserDTO>> updateUsersRoles(
            @Valid final UserRolesUpdateRequestDTO userRolesUpdateRequestDTO) {
        final List<UserEntity> userEntities = userService
                .updateUsersRoles(userRolesUpdateRequestDTO.getEmails(), userRolesUpdateRequestDTO.getRoles(),
                        userRolesUpdateRequestDTO.getUpdateUsersRolesActionEnum());
        return new ResponseEntity<>(userMapper.map(userEntities), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> forgotPassword(final @Valid EmailDTO emailDTO) {
        userService.forgotPassword(emailDTO.getEmail());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<Void> resetPassword(final @Valid ResetPasswordDTO resetPasswordDTO) {
        userService.resetPassword(resetPasswordDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

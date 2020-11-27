package com.itextpdf.dito.manager.controller.user.impl;

import com.itextpdf.dito.manager.component.mapper.user.UserMapper;
import com.itextpdf.dito.manager.controller.user.UserController;
import com.itextpdf.dito.manager.dto.user.UserDTO;
import com.itextpdf.dito.manager.dto.user.create.UserCreateRequestDTO;
import com.itextpdf.dito.manager.dto.user.create.UserCreateResponseDTO;
import com.itextpdf.dito.manager.dto.user.update.UsersActivateRequestDTO;
import com.itextpdf.dito.manager.dto.user.update.UserUpdateRequestDTO;
import com.itextpdf.dito.manager.dto.user.update.UpdatePasswordRequestDTO;
import com.itextpdf.dito.manager.dto.user.unblock.UsersUnblockRequestDTO;
import com.itextpdf.dito.manager.service.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class UserControllerImpl implements UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    public UserControllerImpl(final UserService userService, final UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @Override
    public ResponseEntity<UserCreateResponseDTO> create(final UserCreateRequestDTO userCreateRequest) {
        final UserDTO user = userMapper.map(userService.create(userCreateRequest));
        return new ResponseEntity<>(new UserCreateResponseDTO(user), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Page<UserDTO>> list(final Pageable pageable, final String searchParam) {
        return new ResponseEntity<>(userMapper.map(userService.getAll(pageable, searchParam)), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> updateActivationStatus(final @Valid UsersActivateRequestDTO activateRequestDTO) {
        userService.updateActivationStatus(activateRequestDTO.getEmails(), activateRequestDTO.isActivate());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<UserDTO> currentUser(Principal principal) {
        UserDTO user = userMapper.map(userService.findByEmail(principal.getName()));
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<UserDTO> updateCurrentUser(final UserUpdateRequestDTO updateRequest, Principal principal) {
        UserDTO user = userMapper.map(userService.updateUser(updateRequest, principal.getName()));
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<UserDTO>> unblock(final UsersUnblockRequestDTO userUnblockRequestDTO) {
        final List<UserDTO> unblockedUsers = userUnblockRequestDTO.getUserEmails()
                .stream()
                .map(email -> userMapper.map(userService.unblock(email)))
                .collect(Collectors.toList());
        return new ResponseEntity<>(unblockedUsers, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> updatePassword(final UpdatePasswordRequestDTO updatePasswordRequestDTO,
                                               final Principal principal) {
        userService.updatePassword(updatePasswordRequestDTO.getOldPassword(),
                updatePasswordRequestDTO.getNewPassword(),
                principal.getName());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

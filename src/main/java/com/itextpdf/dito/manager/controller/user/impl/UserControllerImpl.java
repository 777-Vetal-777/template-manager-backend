package com.itextpdf.dito.manager.controller.user.impl;

import com.itextpdf.dito.manager.component.mapper.user.UserMapper;
import com.itextpdf.dito.manager.controller.user.UserController;
import com.itextpdf.dito.manager.dto.user.UserDTO;
import com.itextpdf.dito.manager.dto.user.create.UserCreateRequestDTO;
import com.itextpdf.dito.manager.dto.user.create.UserCreateResponseDTO;
import com.itextpdf.dito.manager.service.user.UserService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<Page<UserDTO>> list(final Pageable pageable) {
        return new ResponseEntity<>(userMapper.map(userService.getAll(pageable)), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> delete(final String email) {
        userService.delete(email);
        return new ResponseEntity<>(email, HttpStatus.OK);
    }
}

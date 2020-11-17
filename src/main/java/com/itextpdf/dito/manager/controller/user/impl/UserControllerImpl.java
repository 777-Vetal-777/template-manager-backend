package com.itextpdf.dito.manager.controller.user.impl;

import com.itextpdf.dito.manager.controller.user.UserController;
import com.itextpdf.dito.manager.dto.user.create.UserCreateRequestDTO;
import com.itextpdf.dito.manager.dto.user.create.UserCreateResponseDTO;
import com.itextpdf.dito.manager.dto.user.list.UserListResponseDTO;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.service.user.UserService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserControllerImpl implements UserController {

    private final UserService userService;

    public UserControllerImpl(final UserService userService) {
        this.userService = userService;
    }

    @Override
    public ResponseEntity<UserCreateResponseDTO> create(final UserCreateRequestDTO userCreateRequest) {
        return new ResponseEntity<>(userService.create(userCreateRequest), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<UserListResponseDTO> list(final String sortBy,
                                                    Boolean desc) {
        return new ResponseEntity<>(userService.getAll(sortBy, desc), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Long> delete(final Long id) {
        userService.delete(id);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }
}

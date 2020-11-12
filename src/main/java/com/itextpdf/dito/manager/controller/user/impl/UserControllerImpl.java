package com.itextpdf.dito.manager.controller.user.impl;

import com.itextpdf.dito.manager.controller.user.UserController;
import com.itextpdf.dito.manager.dto.user.UserCreateRequestDTO;
import com.itextpdf.dito.manager.service.user.UserService;

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
    public ResponseEntity<?> create(final UserCreateRequestDTO userCreateRequest) {
        return new ResponseEntity<>(userService.create(userCreateRequest), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<?> list(final String sortBy,
            Boolean desc) {
        return new ResponseEntity<>(userService.getAll(sortBy, desc), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> delete(final Long id) {
        userService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

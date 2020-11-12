package com.itextpdf.dito.manager.controller.user.impl;

import com.itextpdf.dito.manager.controller.user.UserController;
import com.itextpdf.dito.manager.dto.user.UserCreateRequestDTO;
import com.itextpdf.dito.manager.service.user.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserControllerImpl implements UserController {

    private final UserService userService;

    public UserControllerImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public ResponseEntity<?> create(final @RequestBody UserCreateRequestDTO userCreateRequest) {
        return new ResponseEntity<>(userService.create(userCreateRequest), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<?> list(final @RequestParam(required = false) String sortBy,
                                  @RequestParam(required = false) Boolean desc) {
        return new ResponseEntity<>(userService.getAll(sortBy, desc), HttpStatus.OK);
    }


    @Override
    public ResponseEntity<?> delete(final @PathVariable("id") Long id) {
        userService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

package com.itextpdf.dito.manager.controller.user;

import com.itextpdf.dito.manager.dto.user.UserDTO;
import com.itextpdf.dito.manager.dto.user.create.UserCreateRequestDTO;
import com.itextpdf.dito.manager.dto.user.create.UserCreateResponseDTO;
import com.itextpdf.dito.manager.dto.user.list.UserListResponseDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(UserController.BASE_NAME)
public interface UserController {
    String MAJOR_VERSION = "/v1";
    String BASE_NAME = MAJOR_VERSION + "/users";
    String USER_DELETE_PATH_VARIABLE = "email";
    String USER_DELETE_ENDPOINT = "/{" + USER_DELETE_PATH_VARIABLE + "}";

    @PostMapping
    ResponseEntity<UserCreateResponseDTO> create(@RequestBody UserCreateRequestDTO userCreateRequest);

    @GetMapping
    ResponseEntity<Page<UserDTO>> list(Pageable pageable);

    @DeleteMapping(USER_DELETE_ENDPOINT)
    ResponseEntity<String> delete(@PathVariable(USER_DELETE_PATH_VARIABLE) String email);
}

package com.itextpdf.dito.manager.controller.user;

import com.itextpdf.dito.manager.dto.user.UserCreateRequestDTO;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping(UserController.BASE_NAME)
public interface UserController {
    String MAJOR_VERSION = "/v1";
    String BASE_NAME = MAJOR_VERSION + "/users";

    @PostMapping
    public ResponseEntity<?> create(final @RequestBody UserCreateRequestDTO userCreateRequest);

    @GetMapping
    public ResponseEntity<?> list(final @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) Boolean desc);

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(final @PathVariable("id") Long id);
}

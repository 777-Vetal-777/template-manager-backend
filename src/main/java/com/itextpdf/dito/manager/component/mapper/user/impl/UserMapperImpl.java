package com.itextpdf.dito.manager.component.mapper.user.impl;

import com.itextpdf.dito.manager.component.mapper.user.UserMapper;
import com.itextpdf.dito.manager.dto.user.UserCreateRequestDTO;
import com.itextpdf.dito.manager.entity.UserEntity;

import org.springframework.stereotype.Component;

@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserEntity map(final UserCreateRequestDTO request) {
        final UserEntity result = new UserEntity();

        result.setEmail(request.getEmail());
        result.setPassword(request.getPassword());
        result.setFirstName(request.getFirstName());
        result.setLastName(request.getLastName());

        return result;
    }
}

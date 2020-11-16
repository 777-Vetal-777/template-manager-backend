package com.itextpdf.dito.manager.component.mapper.user.impl;


import com.itextpdf.dito.manager.component.mapper.user.UserMapper;
import com.itextpdf.dito.manager.dto.user.create.UserCreateRequestDTO;
import com.itextpdf.dito.manager.entity.UserEntity;

import org.springframework.stereotype.Component;

@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserEntity map(final UserCreateRequestDTO dto) {
        final UserEntity result = new UserEntity();

        result.setEmail(dto.getEmail());
        result.setPassword(dto.getPassword());
        result.setFirstName(dto.getFirstName());
        result.setLastName(dto.getLastName());

        return result;
    }
}
package com.itextpdf.dito.manager.component.mapper.user.impl;


import com.itextpdf.dito.manager.component.mapper.user.UserMapper;
import com.itextpdf.dito.manager.dto.user.create.UserCreateRequestDTO;
import com.itextpdf.dito.manager.dto.user.create.UserCreateResponseDTO;
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

    @Override
    public UserCreateResponseDTO map(final UserEntity entity) {
        final UserCreateResponseDTO result = new UserCreateResponseDTO();
        result.setId(entity.getId());
        result.setEmail(entity.getEmail());
        result.setActive(entity.getActive());
        result.setFirstName(entity.getFirstName());
        result.setLastName(entity.getLastName());
        return result;
    }
}
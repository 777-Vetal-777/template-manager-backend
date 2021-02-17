package com.itextpdf.dito.manager.component.mapper.user.impl;


import com.itextpdf.dito.manager.component.mapper.role.RoleMapper;
import com.itextpdf.dito.manager.component.mapper.user.UserMapper;
import com.itextpdf.dito.manager.dto.user.UserDTO;
import com.itextpdf.dito.manager.dto.user.create.UserCreateRequestDTO;
import com.itextpdf.dito.manager.dto.user.update.UserUpdateRequestDTO;
import com.itextpdf.dito.manager.entity.UserEntity;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class UserMapperImpl implements UserMapper {
    private final RoleMapper roleMapper;

    public UserMapperImpl(final RoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }

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
    public UserEntity map(UserUpdateRequestDTO dto) {
        final UserEntity result = new UserEntity();

        result.setFirstName(dto.getFirstName());
        result.setLastName(dto.getLastName());

        return result;
    }

    @Override
    public UserDTO map(final UserEntity entity) {
        final UserDTO result = new UserDTO();

        result.setEmail(entity.getEmail());
        result.setFirstName(entity.getFirstName());
        result.setLastName(entity.getLastName());
        result.setActive(entity.isEnabled());
        result.setPasswordUpdatedByAdmin(entity.getPasswordUpdatedByAdmin());
        result.setBlocked(!entity.isAccountNonLocked());
        result.setRoles(entity.getRoles().stream().map(roleMapper::mapWithoutUsers).collect(Collectors.toList()));
        result.setAuthorities(entity.getAuthorities().stream().map(SimpleGrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        return result;
    }

    @Override
    public List<UserDTO> map(final Collection<UserEntity> entities) {
        return entities != null
                ? entities.stream().map(this::map).collect(Collectors.toList())
                : Collections.emptyList();
    }

    @Override
    public Page<UserDTO> map(final Page<UserEntity> entities) {
        return entities.map(this::map);
    }

}
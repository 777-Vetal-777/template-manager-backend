package com.itextpdf.dito.manager.service.user.impl;

import com.itextpdf.dito.manager.component.mapper.user.UserMapper;
import com.itextpdf.dito.manager.dto.user.UserCreateRequestDTO;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.exception.UserNotFoundException;
import com.itextpdf.dito.manager.repository.role.RoleRepository;
import com.itextpdf.dito.manager.repository.user.UserRepository;
import com.itextpdf.dito.manager.service.user.UserService;

import java.util.List;
import java.util.Set;
import liquibase.util.BooleanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import static java.lang.String.format;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final UserMapper userMapper;

    public UserServiceImpl(final UserRepository userRepository,
            final RoleRepository roleRepository, final PasswordEncoder encoder,
            final UserMapper userMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.userMapper = userMapper;
    }

    public UserEntity create(final UserCreateRequestDTO request) {
        if (userRepository.findByEmailAndActiveTrue(request.getEmail()).isPresent()) {
            throw new UserNotFoundException(format("User with email %s already exists", request.getEmail()));
        }
        final UserEntity user = userMapper.map(request);
        user.setPassword(encoder.encode(request.getPassword()));
        //TODO generate temporal password and email log-in link
        //TODO implement adding roles when requirements are completed
        user.setRoles(Set.of(roleRepository.findByName("GLOBAL_ADMINISTRATOR").orElseThrow()));
        return userRepository.save(user);
    }

    public List<UserEntity> getAll(final String sortBy, final Boolean desc) {
        List<UserEntity> result;
        final Sort.Direction direction = BooleanUtils.isTrue(desc)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        if (!StringUtils.isEmpty(sortBy)) {
            result = userRepository.findAllByActiveTrue(Sort.by(direction, sortBy));
        } else {
            result = userRepository.findAllByActiveTrue();
        }
        return result;
    }

    public void delete(final Long id) {
        final UserEntity user = userRepository.findByIdAndActiveTrue(id).orElseThrow(() ->
                new UserNotFoundException(format("User with id=%s doesn't exists or inactive", id)));
        user.setActive(Boolean.FALSE);
        userRepository.save(user);
    }
}

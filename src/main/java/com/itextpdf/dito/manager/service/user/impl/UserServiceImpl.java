package com.itextpdf.dito.manager.service.user.impl;

import com.itextpdf.dito.manager.component.mapper.user.UserMapper;
import com.itextpdf.dito.manager.dto.user.create.UserCreateRequestDTO;
import com.itextpdf.dito.manager.dto.user.create.UserUpdateRequest;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.exception.UserAlreadyExistsException;
import com.itextpdf.dito.manager.exception.UserNotFoundException;
import com.itextpdf.dito.manager.repository.role.RoleRepository;
import com.itextpdf.dito.manager.repository.user.UserRepository;
import com.itextpdf.dito.manager.service.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Set;

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

    @Override
    public UserEntity findByEmail(final String email) {
        return userRepository.findByEmailAndActiveTrue(email).orElseThrow(UserNotFoundException::new);
    }

    @Override
    public UserEntity updateUser(UserUpdateRequest updateRequest, String email) {
        UserEntity user = findByEmail(email);
        user.setFirstName(updateRequest.getFirstName());
        user.setLastName(updateRequest.getLastName());

        return userRepository.save(user);
    }

    @Override
    public UserEntity create(final UserCreateRequestDTO request) {
        if (userRepository.findByEmailAndActiveTrue(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException(new StringBuilder("User with email ")
                    .append(request.getEmail())
                    .append(" already exists")
                    .toString());
        }
        final UserEntity user = userMapper.map(request);
        user.setPassword(encoder.encode(request.getPassword()));
        //TODO generate temporal password and email log-in link
        //TODO implement adding roles when requirements are completed
        user.setRoles(Set.of(roleRepository.findByName("GLOBAL_ADMINISTRATOR")));
        return userRepository.save(user);
    }

    @Override
    public Page<UserEntity> getAll(Pageable pageable, String searchParam) {
        return StringUtils.isEmpty(searchParam)
                ? userRepository.findAllByActiveTrue(pageable)
                : userRepository.search(pageable, searchParam);
    }

    @Override
    public void delete(final String email) {
        final UserEntity user = userRepository.findByEmailAndActiveTrue(email).orElseThrow(() ->
                new UserNotFoundException(format("User with id=%s doesn't exists or inactive", email)));
        user.setActive(Boolean.FALSE);
        userRepository.save(user);
    }

    @Override
    public void lock(UserEntity user) {
        user.setLocked(true);
        userRepository.save(user);
    }

    @Override
    public UserEntity unblock(final String email) {
        final UserEntity user = userRepository.findByEmailAndActiveTrue(email).orElseThrow(() ->
                new UserNotFoundException(format("User with id=%s doesn't exists or inactive", email)));
        user.setLocked(Boolean.FALSE);
        return userRepository.save(user);
    }
}

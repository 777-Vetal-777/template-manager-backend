package com.itextpdf.dito.manager.service.user.impl;

import com.itextpdf.dito.manager.component.mapper.user.UserMapper;
import com.itextpdf.dito.manager.dto.user.create.UserCreateRequestDTO;
import com.itextpdf.dito.manager.dto.user.update.UserUpdateRequestDTO;
import com.itextpdf.dito.manager.entity.RoleEntity;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.exception.ChangePasswordException;
import com.itextpdf.dito.manager.exception.RoleNotFoundException;
import com.itextpdf.dito.manager.exception.UserAlreadyExistsException;
import com.itextpdf.dito.manager.exception.UserNotFoundException;
import com.itextpdf.dito.manager.repository.login.FailedLoginRepository;
import com.itextpdf.dito.manager.repository.role.RoleRepository;
import com.itextpdf.dito.manager.repository.user.UserRepository;
import com.itextpdf.dito.manager.service.AbstractService;
import com.itextpdf.dito.manager.service.user.UserService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import static java.lang.String.format;

@Service
public class UserServiceImpl extends AbstractService implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final FailedLoginRepository failedLoginRepository;
    private final PasswordEncoder encoder;
    private final UserMapper userMapper;

    public UserServiceImpl(final UserRepository userRepository,
                           final RoleRepository roleRepository,
                           final FailedLoginRepository failedLoginRepository,
                           final PasswordEncoder encoder,
                           final UserMapper userMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.failedLoginRepository = failedLoginRepository;
        this.encoder = encoder;
        this.userMapper = userMapper;
    }

    @Override
    public UserEntity findByEmail(final String email) {
        return userRepository.findByEmailAndActiveTrue(email).orElseThrow(UserNotFoundException::new);
    }

    @Override
    public UserEntity updateUser(UserUpdateRequestDTO updateRequest, String email) {
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
        Set<RoleEntity> roles = request.getRoles().stream()
                .map(role -> roleRepository.findByName(role).orElseThrow(RoleNotFoundException::new))
                .collect(Collectors.toSet());
        user.setRoles(roles);
        return userRepository.save(user);
    }

    @Override
    public Page<UserEntity> getAll(Pageable pageable, String searchParam) {
        throwExceptionIfSortedFieldIsNotSupported(pageable.getSort());
        return StringUtils.isEmpty(searchParam)
                ? userRepository.findAll(pageable)
                : userRepository.search(pageable, searchParam);
    }

    @Override
    @Transactional
    public void updateActivationStatus(final List<String> emails, final boolean activateAction) {
        Integer activeUsers = userRepository.countDistinctByEmailIn(emails);
        if (activeUsers != emails.size()) {
            throw new UserNotFoundException("Some of the specified users do not exist");
        }
        userRepository.updateActivationStatus(emails, activateAction);
    }

    @Override
    public void lock(UserEntity user) {
        user.setLocked(true);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public UserEntity unblock(final String email) {
        final UserEntity user = userRepository.findByEmailAndActiveTrue(email).orElseThrow(() ->
                new UserNotFoundException(format("User with id=%s doesn't exists or inactive", email)));
        user.setLocked(Boolean.FALSE);
        failedLoginRepository.deleteByUser(user);
        userRepository.save(user);
        return user;
    }

    @Override
    public void updatePassword(final String oldPassword,
                               final String newPassword,
                               final String userEmail) {
        final UserEntity user = findByEmail(userEmail);
        if (!encoder.matches(oldPassword, user.getPassword())) {
            throw new ChangePasswordException("Incorrect password");
        }
        if (encoder.matches(newPassword, user.getPassword())) {
            throw new ChangePasswordException("New password should not be equal to old password");
        }
        user.setPassword(encoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public UserEntity update(final String email, final UserEntity user) {
        final UserEntity persisted = userRepository.findByEmail(email).orElseThrow(() ->
                new UserNotFoundException(email));

        final Set<RoleEntity> roles = user.getRoles();
        if (roles != null) {
            for (final RoleEntity role : roles) {
                final RoleEntity roleEntity = roleRepository.findByName(role.getName()).orElseThrow(
                        RoleNotFoundException::new);
                persisted.getRoles().add(roleEntity);
            }
        }

        return userRepository.save(persisted);
    }

    @Override
    protected List<String> getSupportedSortFields() {
        return UserRepository.SUPPORTED_SORT_FIELDS;
    }
}

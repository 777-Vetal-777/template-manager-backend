package com.itextpdf.dito.manager.service.user.impl;

import com.itextpdf.dito.manager.component.mail.MailClient;
import com.itextpdf.dito.manager.component.mapper.user.UserMapper;
import com.itextpdf.dito.manager.dto.user.update.UpdateUsersRolesActionEnum;
import com.itextpdf.dito.manager.entity.RoleEntity;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.exception.role.AttemptToAttachGlobalAdministratorRoleException;
import com.itextpdf.dito.manager.exception.role.RoleNotFoundException;
import com.itextpdf.dito.manager.exception.user.InvalidPasswordException;
import com.itextpdf.dito.manager.exception.user.NewPasswordTheSameAsOldPasswordException;
import com.itextpdf.dito.manager.exception.user.UserAlreadyExistsException;
import com.itextpdf.dito.manager.exception.user.UserNotFoundException;
import com.itextpdf.dito.manager.repository.login.FailedLoginRepository;
import com.itextpdf.dito.manager.repository.role.RoleRepository;
import com.itextpdf.dito.manager.repository.user.UserRepository;
import com.itextpdf.dito.manager.service.AbstractService;
import com.itextpdf.dito.manager.service.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class UserServiceImpl extends AbstractService implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final FailedLoginRepository failedLoginRepository;
    private final PasswordEncoder encoder;
    private final UserMapper userMapper;
    private MailClient mailClient;

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
        return userRepository.findByEmailAndActiveTrue(email).orElseThrow(() -> new UserNotFoundException(email));
    }

    @Override
    public UserEntity updateUser(UserEntity userEntity, String email) {
        UserEntity persistedUserEntity = findByEmail(email);
        persistedUserEntity.setFirstName(userEntity.getFirstName());
        persistedUserEntity.setLastName(userEntity.getLastName());

        return userRepository.save(persistedUserEntity);
    }

    @Override
    public UserEntity create(final UserEntity userEntity, final List<String> roles) {
        if (userRepository.findByEmailAndActiveTrue(userEntity.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException(userEntity.getEmail());
        }

        if (isGlobalAdminRolePresented(roles)) {
            throw new AttemptToAttachGlobalAdministratorRoleException();
        }

        userEntity.setPassword(encoder.encode(userEntity.getPassword()));
        Set<RoleEntity> persistedRoles = roles.stream()
                .map(role -> roleRepository.findByName(role).orElseThrow(() -> new RoleNotFoundException(role)))
                .collect(Collectors.toSet());
        userEntity.setRoles(persistedRoles);
        if (mailClient != null) {
            mailClient.sendRegistrationMessage(userEntity.getEmail(), userEntity.getPassword());
        }
        return userRepository.save(userEntity);
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
            throw new UserNotFoundException();
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
                new UserNotFoundException(email));
        user.setLocked(Boolean.FALSE);
        failedLoginRepository.deleteByUser(user);
        userRepository.save(user);
        return user;
    }

    @Override
    public UserEntity updatePassword(final String oldPassword,
            final String newPassword,
            final String userEmail) {
        final UserEntity user = findByEmail(userEmail);
        if (!encoder.matches(oldPassword, user.getPassword())) {
            throw new InvalidPasswordException();
        }
        if (encoder.matches(newPassword, user.getPassword())) {
            throw new NewPasswordTheSameAsOldPasswordException();
        }
        user.setPassword(encoder.encode(newPassword));
        return userRepository.save(user);
    }

    @Override
    public List<UserEntity> updateUsersRoles(final List<String> emails, final List<String> roles,
            final UpdateUsersRolesActionEnum actionEnum) {
        if (isGlobalAdminRolePresented(roles)) {
            throw new AttemptToAttachGlobalAdministratorRoleException();
        }

        final List<UserEntity> userEntities = retrieveUsers(emails);
        final List<RoleEntity> roleEntities = retrieveRoles(roles);

        switch (actionEnum) {
            case ADD:
                for (final UserEntity userEntity : userEntities) {
                    userEntity.getRoles().addAll(roleEntities);
                }
                break;
            case REMOVE:
                for (final UserEntity userEntity : userEntities) {
                    userEntity.getRoles().removeAll(roleEntities);
                }
                break;
            default:
                break;
        }

        return userRepository.saveAll(userEntities);
    }

    private List<UserEntity> retrieveUsers(final List<String> emails) {
        final List<UserEntity> result = new ArrayList<>();

        for (final String email : emails) {
            final UserEntity userEntity = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException(email));
            result.add(userEntity);
        }

        return result;
    }

    private List<RoleEntity> retrieveRoles(final List<String> roles) {
        final List<RoleEntity> result = new ArrayList<>();

        for (final String role : roles) {
            final RoleEntity roleEntity = roleRepository.findByName(role)
                    .orElseThrow(() -> new RoleNotFoundException(role));
            result.add(roleEntity);
        }

        return result;
    }


    private boolean isGlobalAdminRolePresented(final List<String> roles) {
        return roles.contains("GLOBAL_ADMINISTRATOR");
    }

    @Override
    protected List<String> getSupportedSortFields() {
        return UserRepository.SUPPORTED_SORT_FIELDS;
    }

    @Autowired(required = false)
    public void setMailClient(final MailClient mailClient) {
        this.mailClient = mailClient;
    }
}

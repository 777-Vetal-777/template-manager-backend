package com.itextpdf.dito.manager.service.user.impl;

import com.itextpdf.dito.manager.component.mapper.user.UserMapper;
import com.itextpdf.dito.manager.dto.user.create.UserCreateRequestDTO;
import com.itextpdf.dito.manager.dto.user.update.UpdateUsersRolesActionEnum;
import com.itextpdf.dito.manager.dto.user.update.UserUpdateRequestDTO;
import com.itextpdf.dito.manager.entity.RoleEntity;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.exception.ChangePasswordException;
import com.itextpdf.dito.manager.exception.InvalidPasswordException;
import com.itextpdf.dito.manager.exception.RoleNotFoundException;
import com.itextpdf.dito.manager.exception.UserAlreadyExistsException;
import com.itextpdf.dito.manager.exception.UserNotFoundException;
import com.itextpdf.dito.manager.repository.login.FailedLoginRepository;
import com.itextpdf.dito.manager.repository.role.RoleRepository;
import com.itextpdf.dito.manager.repository.user.UserRepository;
import com.itextpdf.dito.manager.service.AbstractService;
import com.itextpdf.dito.manager.service.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Service
public class UserServiceImpl extends AbstractService implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final FailedLoginRepository failedLoginRepository;
    private final PasswordEncoder encoder;
    private final UserMapper userMapper;
    //private final MailClient mailClient;

//    private final String MAIL_FROM = "ditotemplatemanager@gmail.com";
//    private final String MAIL_BODY = "<p>You are registered as a user in Template manager <p>Login: %s <p>Password: %s <p> <p><a href=%s>Please, reset your password after 1st-time login</a>";
//    private final String MAIL_SUBJECT = "DITO registration";
//    private final String FRONT_URL;

    public UserServiceImpl(final UserRepository userRepository,
                           final RoleRepository roleRepository,
                           final FailedLoginRepository failedLoginRepository,
                           final PasswordEncoder encoder,
                           final UserMapper userMapper)
    //  final MailClient mailClient,//@Value("${spring.mail.front-redirect}") final String frontUrl)
    {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.failedLoginRepository = failedLoginRepository;
        this.encoder = encoder;
        this.userMapper = userMapper;
//        this.mailClient = mailClient;
//        this.FRONT_URL = frontUrl;
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
        //sendMailToUser(request);
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
            throw new InvalidPasswordException("Incorrect password");
        }
        if (encoder.matches(newPassword, user.getPassword())) {
            throw new ChangePasswordException("New password should not be equal to old password");
        }
        user.setPassword(encoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public void updateUsersRoles(final List<String> emails, final List<String> roles,
                                 final UpdateUsersRolesActionEnum actionEnum) {
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

        userRepository.saveAll(userEntities);
    }

    private List<UserEntity> retrieveUsers(final List<String> emails) {
        final List<UserEntity> result = new ArrayList<>();

        for (final String email : emails) {
            final UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
            result.add(userEntity);
        }

        return result;
    }

    private List<RoleEntity> retrieveRoles(final List<String> roles) {
        final List<RoleEntity> result = new ArrayList<>();

        for (final String role : roles) {
            final RoleEntity roleEntity = roleRepository.findByName(role).orElseThrow(RoleNotFoundException::new);
            result.add(roleEntity);
        }

        return result;
    }

//    private void sendMailToUser(UserCreateRequestDTO request) {
//        final String mailBody = String.format(MAIL_BODY, request.getEmail(), request.getPassword(), FRONT_URL.concat("/login"));
//        mailClient.send(MAIL_FROM, request.getEmail(), MAIL_SUBJECT, mailBody);
//    }

    @Override
    protected List<String> getSupportedSortFields() {
        return UserRepository.SUPPORTED_SORT_FIELDS;
    }
}

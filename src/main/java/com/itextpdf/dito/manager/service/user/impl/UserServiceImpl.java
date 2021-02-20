package com.itextpdf.dito.manager.service.user.impl;

import com.itextpdf.dito.manager.component.mail.MailClient;
import com.itextpdf.dito.manager.dto.token.reset.ResetPasswordDTO;
import com.itextpdf.dito.manager.dto.user.update.UpdateUsersRolesActionEnum;
import com.itextpdf.dito.manager.entity.RoleEntity;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.exception.role.AttemptToAttachGlobalAdministratorRoleException;
import com.itextpdf.dito.manager.exception.role.RoleNotFoundException;
import com.itextpdf.dito.manager.exception.role.UnableToDeleteSingularRoleException;
import com.itextpdf.dito.manager.exception.token.InvalidResetPasswordTokenException;
import com.itextpdf.dito.manager.exception.user.InvalidPasswordException;
import com.itextpdf.dito.manager.exception.user.NewPasswordTheSameAsOldPasswordException;
import com.itextpdf.dito.manager.exception.user.UserAlreadyExistsException;
import com.itextpdf.dito.manager.exception.user.UserNotFoundException;
import com.itextpdf.dito.manager.exception.user.UserNotFoundOrNotActiveException;
import com.itextpdf.dito.manager.filter.user.UserFilter;
import com.itextpdf.dito.manager.repository.login.FailedLoginRepository;
import com.itextpdf.dito.manager.repository.role.RoleRepository;
import com.itextpdf.dito.manager.repository.user.UserRepository;
import com.itextpdf.dito.manager.service.AbstractService;
import com.itextpdf.dito.manager.service.token.TokenService;
import com.itextpdf.dito.manager.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.itextpdf.dito.manager.filter.FilterUtils.getBooleanMultiselectFromFilter;
import static com.itextpdf.dito.manager.filter.FilterUtils.getStringFromFilter;

@Service
public class UserServiceImpl extends AbstractService implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final FailedLoginRepository failedLoginRepository;
    private final PasswordEncoder encoder;
    private MailClient mailClient;
    private final TokenService tokenService;

    public UserServiceImpl(final UserRepository userRepository,
                           final RoleRepository roleRepository,
                           final FailedLoginRepository failedLoginRepository,
                           final PasswordEncoder encoder,
                           final TokenService tokenService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.failedLoginRepository = failedLoginRepository;
        this.encoder = encoder;
        this.tokenService = tokenService;
    }

    @Override
    public UserEntity updateUser(final UserEntity userEntity, final String email) {
        final UserEntity persistedUserEntity = findActiveUserByEmail(email);
        persistedUserEntity.setFirstName(userEntity.getFirstName());
        persistedUserEntity.setLastName(userEntity.getLastName());
        persistedUserEntity.setPasswordUpdatedByAdmin(false);
        return userRepository.save(persistedUserEntity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserEntity create(final UserEntity userEntity, final List<String> roles, final UserEntity currentUser) {
        if (userRepository.findByEmailAndActiveTrue(userEntity.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException(userEntity.getEmail());
        }

        if (isGlobalAdminRolePresented(roles)) {
            throw new AttemptToAttachGlobalAdministratorRoleException();
        }
        String password = userEntity.getPassword();
        userEntity.setPassword(encoder.encode(userEntity.getPassword()));
        Set<RoleEntity> persistedRoles = roles.stream()
                .map(role -> roleRepository.findByNameAndMasterTrue(role).orElseThrow(() -> new RoleNotFoundException(role)))
                .collect(Collectors.toSet());
        userEntity.setRoles(persistedRoles);
        userEntity.setPasswordUpdatedByAdmin(true);
        UserEntity savedUser = userRepository.save(userEntity);
        if (mailClient != null) {
            mailClient.sendRegistrationMessage(savedUser, password, currentUser);
        }
        return savedUser;
    }

    @Override
    public Page<UserEntity> getAll(final Pageable pageable, final UserFilter userFilter, final String searchParam) {
        throwExceptionIfSortedFieldIsNotSupported(pageable.getSort());

        final Pageable pageWithSort = updateSort(pageable);
        final String email = getStringFromFilter(userFilter.getEmail());
        final String firstName = getStringFromFilter(userFilter.getFirstName());
        final String lastName = getStringFromFilter(userFilter.getLastName());
        final List<String> securityRoles = userFilter.getRoles();
        final Boolean active = getBooleanMultiselectFromFilter(userFilter.getActive());

        return StringUtils.isEmpty(searchParam)
                ? userRepository.filter(pageWithSort, email, firstName, lastName, securityRoles, active)
                : userRepository.search(pageWithSort, email, firstName, lastName, securityRoles, active, searchParam.toLowerCase());
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
        final UserEntity user = findActiveUserByEmail(userEmail);
        if (!encoder.matches(oldPassword, user.getPassword())) {
            throw new InvalidPasswordException();
        }
        checkNewPasswordSameAsOld(newPassword, user.getPassword());
        user.setPassword(encoder.encode(newPassword));
        user.setModifiedAt(new Date());
        user.setPasswordUpdatedByAdmin(false);
        return userRepository.save(user);
    }

    @Override
    public UserEntity updatePassword(final String newPassword, final String userEmail) {
        final UserEntity user = findActiveUserByEmail(userEmail);
        checkNewPasswordSameAsOld(newPassword, user.getPassword());
        user.setPassword(encoder.encode(newPassword));
        user.setModifiedAt(new Date());
        //Password has been updated by the admin.
        user.setPasswordUpdatedByAdmin(true);
        return userRepository.save(user);
    }

    @Override
    public UserEntity findActiveUserByEmail(final String email) {
        return userRepository.findByEmailAndActiveTrue(email).orElseThrow(() -> new UserNotFoundOrNotActiveException(email));
    }

    @Override
    public UserEntity findByEmail(final String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundOrNotActiveException(email));
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
                    final Set<RoleEntity> userRoles = userEntity.getRoles();
                    if (userRoles.size() <= roleEntities.size()) {
                        throw new UnableToDeleteSingularRoleException();
                    }
                    userEntity.getRoles().removeAll(roleEntities);
                }
                break;
            default:
                break;
        }

        return userRepository.saveAll(userEntities);
    }

    @Override
    public Integer calculateCountOfUsersWithOnlyOneRole(final String roleName) {
        return userRepository.countOfUserWithOnlyOneRole(roleName);
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
            final RoleEntity roleEntity = roleRepository.findByNameAndMasterTrue(role)
                    .orElseThrow(() -> new RoleNotFoundException(role));
            result.add(roleEntity);
        }

        return result;
    }


    private boolean isGlobalAdminRolePresented(final List<String> roles) {
        return roles.contains("GLOBAL_ADMINISTRATOR");
    }

    /**
     * W/A for sorting roles by number of users (as sort param cannot be changed on FE side).
     *
     * @param pageable from request
     * @return pageable with updated sort params
     */
    private Pageable updateSort(Pageable pageable) {
        Sort newSort = Sort.by(pageable.getSort().stream()
                .map(sortParam -> {
                    if ("active".equals(sortParam.getProperty())) {
                        //W/A for sorting: on FE false shows as NOT ACTIVE, TRUE as ACTIVE.
                        sortParam = new Sort.Order(sortParam.isAscending() ? Sort.Direction.DESC : Sort.Direction.ASC, "active");
                    }
                    if ("roles".equals(sortParam.getProperty())) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "roles.size");
                    }
                    return ("roles.size".equals(sortParam.getProperty()) || "active".equals(sortParam.getProperty()) ? sortParam : sortParam.ignoreCase());
                })
                .collect(Collectors.toList()));
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), newSort);
    }

    @Override
    protected List<String> getSupportedSortFields() {
        return UserRepository.SUPPORTED_SORT_FIELDS;
    }

    @Autowired(required = false)
    public void setMailClient(final MailClient mailClient) {
        this.mailClient = mailClient;
    }

    @Override
    public void forgotPassword(final String email) {
        final Optional<UserEntity> userEntity = userRepository.findByEmail(email);
        if (userEntity.isPresent()) {
            final String token = tokenService.generateResetPasswordToken(userEntity.get());
            mailClient.sendResetMessage(userEntity.get(), token);
        }
    }

    @Override
    public void resetPassword(final ResetPasswordDTO resetPasswordDTO) {
        final UserEntity userEntity = tokenService.checkResetPasswordToken(resetPasswordDTO.getToken())
                .orElseThrow(() -> new InvalidResetPasswordTokenException());
        userEntity.setPassword(encoder.encode(resetPasswordDTO.getPassword()));
        userEntity.setModifiedAt(new Date());
        userEntity.setResetPasswordTokenDate(null);
        userEntity.setPasswordUpdatedByAdmin(false);
        userRepository.save(userEntity);
    }

    private void checkNewPasswordSameAsOld(final String newPassword, final String oldPasswords) {
        if (encoder.matches(newPassword, oldPasswords)) {
            throw new NewPasswordTheSameAsOldPasswordException();
        }
    }
}

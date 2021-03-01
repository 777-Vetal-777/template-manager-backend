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
import com.itextpdf.dito.manager.exception.user.PasswordNotSpecifiedByAdminException;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    private static final Logger log = LogManager.getLogger(UserServiceImpl.class);

    private static final String ACTIVE = "active";
	
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
        log.info("Update user by email: {} with user: {} was started", email, userEntity);
        final UserEntity persistedUserEntity = findActiveUserByEmail(email);
        persistedUserEntity.setFirstName(userEntity.getFirstName());
        persistedUserEntity.setLastName(userEntity.getLastName());
        persistedUserEntity.setPasswordUpdatedByAdmin(false);
        final UserEntity savedUserEntity = userRepository.save(persistedUserEntity);
        log.info("Update user by email: {} with user: {} was finished successfully", email, userEntity);
        return savedUserEntity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserEntity create(final UserEntity userEntity, final List<String> roles, final UserEntity currentUser) {
        log.info("Create userEntity: {} with roles: {} by user:{} was started", userEntity.getEmail(), roles, currentUser.getEmail());
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
        log.info("Create userEntity: {} with roles: {} by user:{} was finished successfully", userEntity.getEmail(), roles, currentUser.getEmail());
        return savedUser;
    }

    @Override
    public Page<UserEntity> getAll(final Pageable pageable, final UserFilter userFilter, final String searchParam) {
        log.info("Get all users by filter: {} and search was started", userFilter, searchParam);
        throwExceptionIfSortedFieldIsNotSupported(pageable.getSort());

        final Pageable pageWithSort = updateSort(pageable);
        final String email = getStringFromFilter(userFilter.getEmail());
        final String firstName = getStringFromFilter(userFilter.getFirstName());
        final String lastName = getStringFromFilter(userFilter.getLastName());
        final List<String> securityRoles = userFilter.getRoles();
        final Boolean active = getBooleanMultiselectFromFilter(userFilter.getActive());

        final Page<UserEntity> userEntities = StringUtils.isEmpty(searchParam)
                ? userRepository.filter(pageWithSort, email, firstName, lastName, securityRoles, active)
                : userRepository.search(pageWithSort, email, firstName, lastName, securityRoles, active, searchParam.toLowerCase());
        log.info("Get all users by filter: {} and search was finished successfully", userFilter, searchParam);
        return userEntities;
    }

    @Override
    @Transactional
    public void updateActivationStatus(final List<String> emails, final boolean activateAction) {
        log.info("Update activation status for emails: {} and status: {} was started", emails, activateAction);
        Integer activeUsers = userRepository.countDistinctByEmailIn(emails);
        if (activeUsers != emails.size()) {
            throw new UserNotFoundException();
        }
        userRepository.updateActivationStatus(emails, activateAction);
        log.info("Update activation status for emails: {} and status: {} was finished successfully", emails, activateAction);
    }

    @Override
    public void lock(UserEntity user) {
        user.setLocked(true);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public UserEntity unblock(final String email) {
        log.info("Unblock user by with email: {} was started", email);
        final UserEntity user = userRepository.findByEmailAndActiveTrue(email).orElseThrow(() ->
                new UserNotFoundException(email));
        user.setLocked(Boolean.FALSE);
        failedLoginRepository.deleteByUser(user);
        userRepository.save(user);
        log.info("Unblock user by with email: {} was finished successfully", email);
        return user;
    }

    @Override
    public UserEntity updatePassword(final String oldPassword,
                                     final String newPassword,
                                     final String userEmail) {
        log.info("Update password for user: {} was started", userEmail);
        final UserEntity user = findActiveUserByEmail(userEmail);
        if (!encoder.matches(oldPassword, user.getPassword())) {
            throw new InvalidPasswordException();
        }
        checkNewPasswordSameAsOld(newPassword, user.getPassword());
        user.setPassword(encoder.encode(newPassword));
        user.setModifiedAt(new Date());
        user.setPasswordUpdatedByAdmin(false);
        log.info("Update password for user: {} was finished successfully", userEmail);
        return userRepository.save(user);
    }

    @Override
    public UserEntity updatePassword(final String newPassword, final String userEmail, final UserEntity admin) {
        log.info("Update password for user: {} was started", userEmail);
        final UserEntity user = findActiveUserByEmail(userEmail);
        checkNewPasswordSameAsOld(newPassword, user.getPassword());
        user.setPassword(encoder.encode(newPassword));
        user.setModifiedAt(new Date());
        //Password has been updated by the admin.
        user.setPasswordUpdatedByAdmin(true);
        final UserEntity savedUser = userRepository.save(user);
        if (mailClient != null) {
            mailClient.sendPasswordsWasUpdatedByAdminMessage(savedUser, newPassword, admin);
        }
        log.info("Update password for user: {} was finished successfully", userEmail);
        return savedUser;
    }

    @Override
    public UserEntity updatePasswordSpecifiedByAdmin(final String newPassword, final String email) {
        log.info("Update password for user: {} was started", email);
        final UserEntity user = findActiveUserByEmail(email);
        checkNewPasswordSameAsOld(newPassword, user.getPassword());
        checkUserPasswordIsSpecifiedByAdmin(user);
        user.setPassword(encoder.encode(newPassword));
        user.setModifiedAt(new Date());
        user.setPasswordUpdatedByAdmin(false);
        log.info("Update password for user: {} was finished successfully", email);
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
        log.info("Update user roles for emails: {} with roles: {} and actionEnum: {} was started", emails, roles, actionEnum);
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
					final List<Long> userRolesId = userRoles.stream().map(RoleEntity::getId)
							.collect(Collectors.toList());
					final List<Long> roleEntitiesId = roleEntities.stream().map(RoleEntity::getId)
							.collect(Collectors.toList());
					userRolesId.removeAll(roleEntitiesId);
                    if (userRolesId.isEmpty()) {
                        throw new UnableToDeleteSingularRoleException();
                    }
                    userEntity.getRoles().removeAll(roleEntities);
                }
                break;
            default:
                break;
        }

        log.info("Update user roles for emails: {} with roles: {} and actionEnum: {} was finished successfully", emails, roles, actionEnum);
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
                    if (ACTIVE.equals(sortParam.getProperty())) {
                        //W/A for sorting: on FE false shows as NOT ACTIVE, TRUE as ACTIVE.
                        sortParam = new Sort.Order(sortParam.isAscending() ? Sort.Direction.DESC : Sort.Direction.ASC, ACTIVE);
                    }
                    if ("roles".equals(sortParam.getProperty())) {
                        sortParam = new Sort.Order(sortParam.getDirection(), "roles.size");
                    }
                    return ("roles.size".equals(sortParam.getProperty()) || ACTIVE.equals(sortParam.getProperty()) ? sortParam : sortParam.ignoreCase());
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
        log.info("Forgot password for email: {} was started", email);
        final Optional<UserEntity> userEntity = userRepository.findByEmail(email);
        if (userEntity.isPresent()) {
            final String token = tokenService.generateResetPasswordToken(userEntity.get());
            mailClient.sendResetMessage(userEntity.get(), token);
        }
        log.info("Forgot password for email: {} was finished successfully", email);
    }

    @Override
    public void resetPassword(final ResetPasswordDTO resetPasswordDTO) {
        log.info("Reset password with token: {} was started", resetPasswordDTO.getToken());
        final UserEntity userEntity = tokenService.checkResetPasswordToken(resetPasswordDTO.getToken())
                .orElseThrow(InvalidResetPasswordTokenException::new);
        userEntity.setPassword(encoder.encode(resetPasswordDTO.getPassword()));
        userEntity.setModifiedAt(new Date());
        userEntity.setResetPasswordTokenDate(null);
        userEntity.setPasswordUpdatedByAdmin(false);
        log.info("Reset password with token: {} was finished successfully", resetPasswordDTO.getToken());
        userRepository.save(userEntity);
    }

    private void checkNewPasswordSameAsOld(final String newPassword, final String oldPasswords) {
        if (encoder.matches(newPassword, oldPasswords)) {
            throw new NewPasswordTheSameAsOldPasswordException();
        }
    }

    private void checkUserPasswordIsSpecifiedByAdmin(final UserEntity userEntity) {
        if (!userEntity.getPasswordUpdatedByAdmin()) {
            throw new PasswordNotSpecifiedByAdminException();
        }
    }
}

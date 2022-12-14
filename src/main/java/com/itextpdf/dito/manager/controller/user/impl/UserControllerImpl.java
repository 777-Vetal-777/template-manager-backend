package com.itextpdf.dito.manager.controller.user.impl;

import com.itextpdf.dito.manager.component.encoder.Encoder;
import com.itextpdf.dito.manager.component.mapper.user.UserMapper;
import com.itextpdf.dito.manager.controller.AbstractController;
import com.itextpdf.dito.manager.controller.user.UserController;
import com.itextpdf.dito.manager.dto.auth.AuthenticationDTO;
import com.itextpdf.dito.manager.dto.user.EmailDTO;
import com.itextpdf.dito.manager.dto.token.reset.ResetPasswordDTO;
import com.itextpdf.dito.manager.dto.user.UserDTO;
import com.itextpdf.dito.manager.dto.user.create.UserCreateRequestDTO;
import com.itextpdf.dito.manager.dto.user.unblock.UsersUnblockRequestDTO;
import com.itextpdf.dito.manager.dto.user.update.PasswordChangeRequestDTO;
import com.itextpdf.dito.manager.dto.user.update.UpdatePasswordRequestDTO;
import com.itextpdf.dito.manager.dto.user.update.UserRolesUpdateRequestDTO;
import com.itextpdf.dito.manager.dto.user.update.UserUpdateRequestDTO;
import com.itextpdf.dito.manager.dto.user.update.UsersActivateRequestDTO;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.filter.user.UserFilter;
import com.itextpdf.dito.manager.service.auth.AuthenticationService;
import com.itextpdf.dito.manager.service.user.UserService;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserControllerImpl extends AbstractController implements UserController {
    private static final Logger log = LogManager.getLogger(UserControllerImpl.class);
    private final UserService userService;
    private final UserMapper userMapper;
    private final AuthenticationService authenticationService;
    private final Encoder encoder;
    private final String frontRedirect;

    public UserControllerImpl(final UserService userService, final UserMapper userMapper,
                              final AuthenticationService authenticationService,
                              final Encoder encoder,
                              @Value("${spring.mail.front-redirect}") final String frontRedirect) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.authenticationService = authenticationService;
        this.encoder = encoder;
        this.frontRedirect = frontRedirect;
    }

    @Override
    public ResponseEntity<UserDTO> create(@Valid final UserCreateRequestDTO userCreateRequestDTO, final HttpServletRequest request, final Principal principal) {
        log.info("Create user with params: {} was started", userCreateRequestDTO);
        userCreateRequestDTO.setEmail(userCreateRequestDTO.getEmail().toLowerCase());
        final String frontURL = getOriginHeaderOrDefault(request);
        final UserEntity currentUser = userService.findActiveUserByEmail(principal.getName());
        final UserEntity user = userService
                .create(userMapper.map(userCreateRequestDTO), userCreateRequestDTO.getRoles(), currentUser, frontURL);
        log.info("Create user with params: {} was finished successfully", userCreateRequestDTO);
        return new ResponseEntity<>(userMapper.map(user), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<UserDTO> update(final String userName, @Valid final UserUpdateRequestDTO userUpdateRequestDTO) {
        final String lowerDecodedUserName = encoder.decode(userName).toLowerCase();
        log.info("Update user by name: {} and  with params: {} was started", lowerDecodedUserName, userUpdateRequestDTO);
        final UserDTO user = userMapper.map(userService.updateUser(userMapper.map(userUpdateRequestDTO), lowerDecodedUserName));
        log.info("Update user by name: {} and  with params: {} was finished successfully", userName, userUpdateRequestDTO);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<UserDTO> updatePassword(final String userName, final UpdatePasswordRequestDTO requestDTO, final HttpServletRequest request, final Principal principal) {
        log.info("Update password by userName: {} was started", userName);
        final String frontURL = getOriginHeaderOrDefault(request);
        final String decodedLowerName = encoder.decode(userName).toLowerCase();
        final UserEntity adminEntity = userService.findByEmail(principal.getName());
        final UserEntity userEntity = userService.updatePassword(requestDTO.getPassword(), decodedLowerName, adminEntity, frontURL);
        log.info("Update password by userName: {} was finished successfully", userName);
        return new ResponseEntity<>(userMapper.map(userEntity), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<UserDTO> get(final String userName, final Principal principal) {
        log.info("Get info about user by email: {} was started", userName);
        final String lowerCaseName = encoder.decode(userName).toLowerCase();
        final UserDTO user = userMapper.map(userService.findByEmail(lowerCaseName));
        log.info("Get info about user by email: {} was finished successfully", userName);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Page<UserDTO>> list(final Pageable pageable, final UserFilter userFilter, final String searchParam) {
        log.info("Get users list by filter: {} and searchParam: {} was started", userFilter, searchParam);
        final Page<UserEntity> userEntities = userService.getAll(pageable, userFilter, searchParam);
        log.info("Get users list by filter: {} and searchParam: {} was finished successfully", userFilter, searchParam);
        return new ResponseEntity<>(userMapper.map(userEntities), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> updateActivationStatus(final @Valid UsersActivateRequestDTO usersActivateRequestDTO) {
        log.info("Activate(deactivate) users in batch by params: {} was started", usersActivateRequestDTO);
        usersActivateRequestDTO.setEmails(usersActivateRequestDTO.getEmails().stream().map(String::toLowerCase).collect(Collectors.toList()));
        userService.updateActivationStatus(usersActivateRequestDTO.getEmails(), usersActivateRequestDTO.isActivate());
        log.info("Activate(deactivate) users in batch by params: {} was finished successfully", usersActivateRequestDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<UserDTO> currentUser(final Principal principal) {
        UserDTO user = userMapper.map(userService.findActiveUserByEmail(principal.getName()));
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<UserDTO> updateCurrentUser(@Valid final UserUpdateRequestDTO userUpdateRequestDTO,
                                                     final Principal principal) {
        final UserDTO user = userMapper
                .map(userService.updateUser(userMapper.map(userUpdateRequestDTO), principal.getName()));
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<UserDTO>> unblock(final UsersUnblockRequestDTO usersUnblockRequestDTO) {
        log.info("Unblock users with params: {} was started", usersUnblockRequestDTO);
        final List<UserDTO> unblockedUsers = usersUnblockRequestDTO.getUserEmails()
                .stream()
                .map(String::toLowerCase)
                .map(email -> userMapper.map(userService.unblock(email)))
                .collect(Collectors.toList());
        log.info("Unblock users with params: {} was finished successfully", usersUnblockRequestDTO);
        return new ResponseEntity<>(unblockedUsers, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<AuthenticationDTO> updatePassword(final @Valid PasswordChangeRequestDTO passwordChangeRequestDTO,
                                                            final Principal principal) {
        final UserEntity userEntity = userService.updatePassword(passwordChangeRequestDTO.getOldPassword(),
                passwordChangeRequestDTO.getNewPassword(),
                principal.getName());
        final AuthenticationDTO newTokenDto = authenticationService.authenticate(userEntity.getEmail(), passwordChangeRequestDTO.getNewPassword());
        return new ResponseEntity<>(newTokenDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<AuthenticationDTO> updateAdminPasswordToUser(final UpdatePasswordRequestDTO updatePasswordRequestDTO, final Principal principal) {
        final UserEntity userEntity = userService.updatePasswordSpecifiedByAdmin(updatePasswordRequestDTO.getPassword(), principal.getName());
        final AuthenticationDTO authenticationDTO = authenticationService.authenticate(userEntity.getEmail(), updatePasswordRequestDTO.getPassword());
        return new ResponseEntity<>(authenticationDTO, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<UserDTO>> updateUsersRoles(
            @Valid final UserRolesUpdateRequestDTO userRolesUpdateRequestDTO) {
        final List<UserEntity> userEntities = userService
                .updateUsersRoles(userRolesUpdateRequestDTO.getEmails(), userRolesUpdateRequestDTO.getRoles(),
                        userRolesUpdateRequestDTO.getUpdateUsersRolesActionEnum());
        return new ResponseEntity<>(userMapper.map(userEntities), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> forgotPassword(final @Valid EmailDTO emailDTO, final HttpServletRequest request) {
        log.info("Forgot password by email: {} was started", emailDTO.getEmail());
        final String frontURL = getOriginHeaderOrDefault(request);
        userService.forgotPassword(emailDTO.getEmail(), frontURL);
        log.info("Forgot password by email: {} was finished successfully", emailDTO.getEmail());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<Void> resetPassword(final @Valid ResetPasswordDTO resetPasswordDTO) {
        log.info("Reset password by token: {} was started", resetPasswordDTO.getToken());
        userService.resetPassword(resetPasswordDTO.getToken(), resetPasswordDTO.getPassword());
        log.info("Reset password by token: {} was finished successfully", resetPasswordDTO.getToken());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<Boolean> lockedUsersExist() {
        return new ResponseEntity<>(userService.lockedUsersExist(), HttpStatus.OK);
    }

    private String getOriginHeaderOrDefault(final HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(HttpHeaders.ORIGIN)).orElse(frontRedirect);
    }
}

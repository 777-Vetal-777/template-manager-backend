package com.itextpdf.dito.manager.component.listener.auth;

import com.itextpdf.dito.manager.entity.FailedLoginAttemptEntity;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.repository.login.FailedLoginRepository;
import com.itextpdf.dito.manager.service.user.UserService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {
    private static final Logger log = LogManager.getLogger(AuthenticationFailureListener.class);

    private final UserService userService;
    private final FailedLoginRepository failedLoginRepository;

    private final int maximumFailedLoginAttempts;

    public AuthenticationFailureListener(
            final UserService userService,
            final FailedLoginRepository failedLoginRepository,
            final @Value("${security.login.failure.max-attempts}") int maximumFailedLoginAttempts) {
        this.userService = userService;
        this.failedLoginRepository = failedLoginRepository;
        this.maximumFailedLoginAttempts = maximumFailedLoginAttempts;
    }

    @Override
    public void onApplicationEvent(final AuthenticationFailureBadCredentialsEvent event) {
        final String principal = (String) event.getAuthentication().getPrincipal();
        final UserEntity user = userService.findUserByEmail(principal);

        if (log.isDebugEnabled()) {
            log(user.getEmail());
        }

        failedLoginRepository.save(new FailedLoginAttemptEntity(user));

        if (isMaximumCountOfFailuresExceededByUser(user)) {
            lockUser(user);
        }
    }

    private void log(final String email) {
        log.debug(new StringBuilder("Authentication is failed for user ").append(email).append("."));
    }

    private boolean isMaximumCountOfFailuresExceededByUser(final UserEntity user) {
        return failedLoginRepository.countByUser(user) >= maximumFailedLoginAttempts;
    }

    private void lockUser(final UserEntity user) {
        userService.lock(user);

        if (log.isDebugEnabled()) {
            log.debug(new StringBuilder("User ").append(user.getEmail()).append(" is locked."));
        }
    }
}

package com.itextpdf.dito.manager.component.listener.auth;

import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.repository.login.FailedLoginRepository;

import javax.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {
    private static final Logger log = LogManager.getLogger(AuthenticationSuccessListener.class);

    private final FailedLoginRepository failedLoginRepository;

    public AuthenticationSuccessListener(final FailedLoginRepository failedLoginRepository) {
        this.failedLoginRepository = failedLoginRepository;
    }

    @Override
    @Transactional
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        final UserEntity user = (UserEntity) event.getAuthentication().getPrincipal();

        if (log.isDebugEnabled()) {
            log(user.getEmail());
        }

        failedLoginRepository.deleteByUser(user);
    }

    private void log(final String email) {
        log.debug(new StringBuilder("User ").append(email).append(" is logged in."));
    }
}

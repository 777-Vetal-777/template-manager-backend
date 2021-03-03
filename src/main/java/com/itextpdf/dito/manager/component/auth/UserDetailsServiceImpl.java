package com.itextpdf.dito.manager.component.auth;

import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.exception.user.UserNotFoundException;
import com.itextpdf.dito.manager.repository.user.UserRepository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger LOG = LogManager.getLogger(UserDetailsServiceImpl.class);

    @Value("${security.credentials.admin-email:#{null}}")
    private String defaultAdminEmail;
    @Value("${security.credentials.admin-password:#{null}}")
    private String defaultAdminPassword;

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public UserDetailsServiceImpl(final UserRepository userRepository,
                                  final PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @Override
    public UserDetails loadUserByUsername(final String username) {
        return userRepository.findByEmailAndActiveTrue(username)
                .orElseThrow(
                        () -> new UsernameNotFoundException(
                                new StringBuilder("No user with email ").append(username).append(" was found")
                                        .toString()));
    }

    /**
     * Default admin username and password should be set as env variables:
     * ${DITO_MANAGER_DEFAULT_ADMIN_EMAIL} and ${DITO_MANAGER_DEFAULT_ADMIN_PASSWORD}.
     * Otherwise default values from db.changelog-0.0.1.xml will be used.
     */
    @PostConstruct
    public void updateAdminProperties() {
        final Long defaultAdminId = 1L;
        if (!StringUtils.isEmpty(defaultAdminEmail) && !StringUtils.isEmpty(defaultAdminPassword)) {
            final UserEntity userEntity = userRepository.findById(defaultAdminId).orElseThrow(() -> new UserNotFoundException("Admin user not found"));
            userEntity.setEmail(defaultAdminEmail);
            userEntity.setPassword(encoder.encode(defaultAdminPassword));
            userRepository.save(userEntity);
        } else {
            LOG.warn("No email and password for default admin user were set. Default credentials will be used");
        }
    }
}


package com.itextpdf.dito.manager.component.auth;

import com.itextpdf.dito.manager.repository.user.UserRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        return userRepository.findByEmailAndActiveTrue(username)
                .orElseThrow(
                        () -> new UsernameNotFoundException(
                                new StringBuilder("No user with email ").append(username).append(" was found")
                                        .toString()));
    }
}


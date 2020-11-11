package com.itextpdf.dito.manager.service;

import com.itextpdf.dito.manager.dto.UserCreateRequest;
import com.itextpdf.dito.manager.entity.User;
import com.itextpdf.dito.manager.exception.UserNotFoundException;
import com.itextpdf.dito.manager.repository.RoleRepository;
import com.itextpdf.dito.manager.repository.UserRepository;
import liquibase.util.BooleanUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;

import static java.lang.String.format;

@Service
@Log4j2
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final ManagerMapper managerMapper;

    public User create(UserCreateRequest request) {
        if (userRepository.findByEmailAndActiveTrue(request.getEmail()).isPresent()) {
            throw new UserNotFoundException(format("User with email %s already exists", request.getEmail()));
        }
        User user = managerMapper.fromRequest(request);
        user.setPassword(encoder.encode(request.getPassword()));
        //TODO generate temporal password and email log-in link
        //TODO implement adding roles when requirements are completed
        user.setRoles(Set.of(roleRepository.findByName("ROLE_GLOBAL_ADMINISTRATOR").orElseThrow()));
        return userRepository.save(user);
    }

    public List<User> getAll(String sortBy, Boolean desc) {
        List<User> result;
        Sort.Direction direction = BooleanUtils.isTrue(desc)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        if (!StringUtils.isEmpty(sortBy)) {
            result = userRepository.findAllByActiveTrue(Sort.by(direction, sortBy));
        } else {
            result = userRepository.findAllByActiveTrue();
        }
        return result;
    }

    public void delete(Long id) {
        User user = userRepository.findByIdAndActiveTrue(id).orElseThrow(() ->
                new UserNotFoundException(format("User with id=%s doesn't exists or inactive", id)));
        user.setActive(Boolean.FALSE);
        userRepository.save(user);
    }
}

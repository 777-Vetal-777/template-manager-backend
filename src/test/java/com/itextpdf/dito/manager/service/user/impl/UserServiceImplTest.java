package com.itextpdf.dito.manager.service.user.impl;

import com.itextpdf.dito.manager.entity.RoleEntity;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.exception.user.UserNotFoundOrNotActiveException;
import com.itextpdf.dito.manager.repository.role.RoleRepository;
import com.itextpdf.dito.manager.repository.user.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    UserRepository userRepository;

    @Mock
    RoleRepository roleRepository;

    @InjectMocks
    UserServiceImpl userService;

    UserEntity user;

    @BeforeEach
    void setup() {
        user = new UserEntity();
        user.setId(1L);
        user.setEmail("user@email.com");
        user.setPassword("unhashedPassword");
        user.setFirstName("Harry");
        user.setFirstName("Kane");
        user.setActive(true);
        user.setLocked(false);

        RoleEntity role = new RoleEntity();
        role.setId(1L);
        role.setName("GLOBAL_ADMINISTRATOR");
        user.setRoles(Set.of(role));
    }

    @Test
    void findByEmail_WhenUserExists_ThenReturnSpecifiedUser() {
        when(userRepository.findByEmailAndActiveTrue(user.getEmail())).thenReturn(Optional.of(user));

        UserEntity result = userService.findByEmail(user.getEmail());
        assertEquals(user, result);
    }

    @Test
    void findByEmail_WhenUserDoesNotExist_ThenExceptionIsThrown() {
        when(userRepository.findByEmailAndActiveTrue(user.getEmail())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundOrNotActiveException.class,
                () -> userService.findByEmail(user.getEmail()));
    }

    @Test
    void updateUser_WhenCorrectRequestsIsSent_ThenUserFieldsAreUpdated() {
        when(userRepository.findByEmailAndActiveTrue(user.getEmail())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        UserEntity updateRequest = new UserEntity();
        String newFirstName = "myNewFirstName";
        String newLastName = "myNewLastName";
        updateRequest.setFirstName(newFirstName);
        updateRequest.setLastName(newLastName);

        UserEntity result = userService.updateUser(updateRequest, user.getEmail());

        assertEquals(newFirstName, result.getFirstName());
        assertEquals(newLastName, result.getLastName());
    }

    @Test
    void updateUser_WhenUserDoesNotExists_ThenExceptionIsThrown() {
        when(userRepository.findByEmailAndActiveTrue(user.getEmail())).thenReturn(Optional.empty());
        UserEntity updateRequest = new UserEntity();
        updateRequest.setFirstName("");
        updateRequest.setLastName("");

        assertThrows(UserNotFoundOrNotActiveException.class,
                () -> userService.updateUser(updateRequest, user.getEmail()));
    }
}
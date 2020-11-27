package com.itextpdf.dito.manager.integration;

import com.itextpdf.dito.manager.controller.user.UserController;
import com.itextpdf.dito.manager.dto.user.UserDTO;
import com.itextpdf.dito.manager.dto.user.create.UserCreateRequestDTO;
import com.itextpdf.dito.manager.dto.user.update.UsersActivateRequestDTO;
import com.itextpdf.dito.manager.dto.user.update.UserUpdateRequestDTO;
import com.itextpdf.dito.manager.dto.user.unblock.UsersUnblockRequestDTO;
import com.itextpdf.dito.manager.entity.FailedLoginAttemptEntity;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.repository.login.FailedLoginRepository;
import com.itextpdf.dito.manager.repository.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.itextpdf.dito.manager.controller.user.UserController.CURRENT_USER;
import static com.itextpdf.dito.manager.controller.user.UserController.CURRENT_USER_INFO_ENDPOINT;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserFlowIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FailedLoginRepository failedLoginRepository;

    @Test
    public void testCreateUser() throws Exception {
        UserCreateRequestDTO request = objectMapper.readValue(new File("src/test/resources/test-data/users/user-create-request.json"), UserCreateRequestDTO.class);
        mockMvc.perform(post(UserController.BASE_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("createdUser.firstName").value("Harry"))
                .andExpect(jsonPath("createdUser.email").value("user@email.com"))
                .andExpect(jsonPath("createdUser.lastName").value("Kane"))
                .andExpect(jsonPath("createdUser.active").value("true"));

        assertTrue(userRepository.findByEmailAndActiveTrue("user@email.com").isPresent());

        mockMvc.perform(post(UserController.BASE_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    public void testGetAll() throws Exception {
        mockMvc.perform(get(UserController.BASE_NAME)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void deactivateUsers() throws Exception {
        UserEntity user1 = new UserEntity();
        user1.setEmail("user1@email.com");
        user1.setFirstName("user1");
        user1.setLastName("user1");
        user1.setPassword("password1");
        user1.setActive(Boolean.TRUE);

        UserEntity user2 = new UserEntity();
        user2.setEmail("user2@email.com");
        user2.setFirstName("user2");
        user2.setLastName("user2");
        user2.setPassword("password2");
        user2.setActive(Boolean.TRUE);

        userRepository.save(user1);
        userRepository.save(user2);

        UsersActivateRequestDTO activateRequestDTO = new UsersActivateRequestDTO();
        activateRequestDTO.setActivate(false);
        activateRequestDTO.setEmails(List.of(user1.getEmail(), user2.getEmail()));
        mockMvc.perform(patch(UserController.BASE_NAME)
                .content(objectMapper.writeValueAsString(activateRequestDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        Optional<UserEntity> persisted1 = userRepository.findByEmail(user1.getEmail());
        Optional<UserEntity> persisted2 = userRepository.findByEmail(user2.getEmail());

        assertTrue(persisted1.isPresent());
        assertTrue(persisted2.isPresent());
        assertFalse(persisted1.get().getActive());
        assertFalse(persisted2.get().getActive());
    }

    @Test
    public void activateUsers() throws Exception {
        UserEntity user1 = new UserEntity();
        user1.setEmail("user11@email.com");
        user1.setFirstName("user1");
        user1.setLastName("user1");
        user1.setPassword("password1");
        user1.setActive(Boolean.FALSE);

        UserEntity user2 = new UserEntity();
        user2.setEmail("user21@email.com");
        user2.setFirstName("user2");
        user2.setLastName("user2");
        user2.setPassword("password2");
        user2.setActive(Boolean.FALSE);

        userRepository.save(user1);
        userRepository.save(user2);

        UsersActivateRequestDTO activateRequestDTO = new UsersActivateRequestDTO();
        activateRequestDTO.setEmails(List.of(user1.getEmail(), user2.getEmail()));
        activateRequestDTO.setActivate(true);
        mockMvc.perform(patch(UserController.BASE_NAME)
                .content(objectMapper.writeValueAsString(activateRequestDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        Optional<UserEntity> persisted1 = userRepository.findByEmail(user1.getEmail());
        Optional<UserEntity> persisted2 = userRepository.findByEmail(user2.getEmail());

        assertTrue(persisted1.isPresent());
        assertTrue(persisted2.isPresent());
        assertTrue(persisted1.get().getActive());
        assertTrue(persisted2.get().getActive());
    }

    @Test
    public void deactivateUsersWhenUserNotFound() throws Exception {
        UsersActivateRequestDTO deleteRequest = new UsersActivateRequestDTO();
        deleteRequest.setEmails(List.of("unknown@email.com"));
        mockMvc.perform(patch(UserController.BASE_NAME)
                .content(objectMapper.writeValueAsString(deleteRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void currentUser() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get(UserController.BASE_NAME + CURRENT_USER_INFO_ENDPOINT)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        UserDTO result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserDTO.class);
        assertNotNull(result);
    }

    @Test
    void updateCurrentUser() throws Exception {
        UserUpdateRequestDTO request = objectMapper.readValue(new File("src/test/resources/test-data/users/user-update-request.json"), UserUpdateRequestDTO.class);
        MvcResult mvcResult = mockMvc.perform(patch(UserController.BASE_NAME + CURRENT_USER)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        UserDTO response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserDTO.class);

        assertEquals(request.getFirstName(), response.getFirstName());
        assertEquals(request.getLastName(), response.getLastName());
    }

    @Test
    public void testUnblockUser() throws Exception {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("blockeduser@email.com");
        userEntity.setFirstName("Harry");
        userEntity.setLastName("Kane");
        userEntity.setPassword("123");
        userEntity.setActive(Boolean.TRUE);
        userEntity.setLocked(Boolean.TRUE);

        FailedLoginAttemptEntity failedLoginAttemptEntity = new FailedLoginAttemptEntity();
        failedLoginAttemptEntity.setUser(userEntity);
        failedLoginAttemptEntity.setVersion(new Date());

        userRepository.save(userEntity);
        failedLoginRepository.save(failedLoginAttemptEntity);

        UsersUnblockRequestDTO request = objectMapper.readValue(new File("src/test/resources/test-data/users/user-unblock-request.json"), UsersUnblockRequestDTO.class);
        mockMvc.perform(patch(UserController.BASE_NAME + UserController.USERS_UNBLOCK_ENDPOINT)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].blocked").value("false"));
        UserEntity user = userRepository.findByEmail("blockeduser@email.com").orElseThrow();
        assertTrue(failedLoginRepository.findByUser(userEntity).isEmpty());
        assertFalse(user.getLocked());
    }
}

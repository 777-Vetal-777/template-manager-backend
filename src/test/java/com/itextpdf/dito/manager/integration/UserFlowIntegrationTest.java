package com.itextpdf.dito.manager.integration;

import com.itextpdf.dito.manager.controller.user.UserController;
import com.itextpdf.dito.manager.dto.user.UserDTO;
import com.itextpdf.dito.manager.dto.user.create.UserCreateRequestDTO;
import com.itextpdf.dito.manager.dto.user.create.UserUpdateRequest;
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

import static com.itextpdf.dito.manager.controller.user.UserController.CURRENT_USER;
import static com.itextpdf.dito.manager.controller.user.UserController.CURRENT_USER_INFO_ENDPOINT;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    public void testDeactivateUser() throws Exception {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("test@email.com");
        userEntity.setFirstName("Harry");
        userEntity.setLastName("Kane");
        userEntity.setPassword("123");
        userEntity.setActive(Boolean.TRUE);

        userRepository.save(userEntity);
        mockMvc.perform(delete(UserController.BASE_NAME + "/" + userEntity.getEmail()))
                .andExpect(status().isOk());
        UserEntity user = userRepository.findByEmail("test@email.com").orElseThrow();
        assertFalse(user.getActive());
    }

    @Test
    public void testDeactivateNotFoundUser() throws Exception {
        mockMvc.perform(delete(UserController.BASE_NAME + "/" + "unknown@email.com"))
                .andExpect(status().isNotFound());

    }

    @Test
    void currentUser() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get(UserController.BASE_NAME + CURRENT_USER + CURRENT_USER_INFO_ENDPOINT)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        UserDTO result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserDTO.class);
        assertNotNull(result);
    }

    @Test
    void updateCurrentUser() throws Exception {
        UserUpdateRequest request = objectMapper.readValue(new File("src/test/resources/test-data/users/user-update-request.json"), UserUpdateRequest.class);
        MvcResult mvcResult = mockMvc.perform(put(UserController.BASE_NAME + CURRENT_USER + CURRENT_USER_INFO_ENDPOINT)
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
        mockMvc.perform(post(UserController.BASE_NAME + UserController.USERS_UNBLOCK_ENDPOINT)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        UserEntity user = userRepository.findByEmail("blockeduser@email.com").orElseThrow();
        assertTrue(failedLoginRepository.findByUser(userEntity).isEmpty());
        assertFalse(user.getLocked());
    }
}

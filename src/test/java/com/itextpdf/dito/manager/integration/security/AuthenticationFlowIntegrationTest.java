package com.itextpdf.dito.manager.integration.security;

import com.itextpdf.dito.manager.controller.login.AuthenticationController;
import com.itextpdf.dito.manager.dto.auth.AuthenticationRequestDTO;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.integration.AbstractIntegrationTest;
import com.itextpdf.dito.manager.repository.login.FailedLoginRepository;
import com.itextpdf.dito.manager.repository.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthenticationFlowIntegrationTest extends AbstractIntegrationTest {
    @Value("${security.login.failure.max-attempts}")
    private Integer maximumFailedLoginAttempts;

    @Autowired
    private FailedLoginRepository failedLoginRepository;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    public void teardown() {
        UserEntity admin = userRepository.findByEmail("admin@email.com").orElseThrow();
        admin.setLocked(false);
        userRepository.save(admin);
        failedLoginRepository.deleteAll();
    }

    @Test
    public void testLoginSuccess() throws Exception {
        AuthenticationRequestDTO request = objectMapper.readValue(new File("src/test/resources/test-data/login/login-request.json"), AuthenticationRequestDTO.class);
        final MvcResult result = mockMvc.perform(post(AuthenticationController.BASE_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("accessToken").isNotEmpty())
                .andExpect(jsonPath("refreshToken").isNotEmpty()).andReturn();
        assertNotNull(result.getResponse());
    }

    @Test
    public void login_WhenLoginFailsMoreThanMaximumAllowed_ThenAccountShouldBeLocked() throws Exception {
        AuthenticationRequestDTO request = objectMapper.readValue(new File("src/test/resources/test-data/login/login-invalid-credentials-request.json"), AuthenticationRequestDTO.class);
        for (int i = 0; i < maximumFailedLoginAttempts; i++) {
            mockMvc.perform(post(AuthenticationController.BASE_NAME)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());
        }

        final MvcResult result = mockMvc.perform(post(AuthenticationController.BASE_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isLocked()).andReturn();
        assertNotNull(result.getResponse());
    }
}

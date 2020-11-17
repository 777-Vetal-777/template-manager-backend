package com.itextpdf.dito.manager.integration;

import com.itextpdf.dito.manager.controller.login.AuthenticationController;
import com.itextpdf.dito.manager.dto.auth.AuthenticationRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.io.File;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthenticationFlowIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void testLoginSuccess() throws Exception {
        AuthenticationRequestDTO request = objectMapper.readValue(new File("src/test/resources/test-data/login/login-request.json"), AuthenticationRequestDTO.class);
        mockMvc.perform(post(AuthenticationController.BASE_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("accessToken").isNotEmpty())
                .andExpect(jsonPath("refreshToken").isNotEmpty());
    }
}

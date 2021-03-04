package com.itextpdf.dito.manager.integration.security;

import com.itextpdf.dito.manager.controller.login.AuthenticationController;
import com.itextpdf.dito.manager.controller.token.TokenController;
import com.itextpdf.dito.manager.dto.auth.AuthenticationDTO;
import com.itextpdf.dito.manager.dto.auth.AuthenticationRequestDTO;
import com.itextpdf.dito.manager.dto.token.TokenDTO;
import com.itextpdf.dito.manager.dto.token.refresh.AccessTokenRefreshRequestDTO;
import com.itextpdf.dito.manager.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TokenFlowIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private AuthenticationController authenticationController;

    @Test
    public void testRefreshSuccess() throws Exception {
        AuthenticationRequestDTO authRequest = objectMapper
                .readValue(new File("src/test/resources/test-data/login/login-request.json"),
                        AuthenticationRequestDTO.class);
        AuthenticationDTO authenticationResponseDTO = authenticationController.login(authRequest).getBody();

        final MvcResult result = mockMvc.perform(post(TokenController.BASE_NAME + TokenController.REFRESH_ENDPOINT)
                .content(objectMapper.writeValueAsString(
                        new AccessTokenRefreshRequestDTO(new TokenDTO(authenticationResponseDTO.getRefreshToken()))))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("token").isNotEmpty()).andReturn();
        assertNotNull(result.getResponse());
    }

    @Test
    public void refreshSuccess_WhenRefreshTokenIsNotValid_ThenResponseIsUnauthorized() throws Exception {
        AccessTokenRefreshRequestDTO refreshTokenRequest = new AccessTokenRefreshRequestDTO(
                new TokenDTO("InvalidToken"));
        final MvcResult result = mockMvc.perform(post(TokenController.BASE_NAME + TokenController.REFRESH_ENDPOINT)
                .content(objectMapper.writeValueAsString(refreshTokenRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized()).andReturn();
        assertNotNull(result.getResponse());
    }
}

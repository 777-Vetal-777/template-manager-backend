package com.itextpdf.dito.manager.integration;

import com.itextpdf.dito.manager.controller.user.UserController;
import com.itextpdf.dito.manager.dto.user.update.UpdatePasswordRequestDTO;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.repository.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ChangePasswordFlowIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void success() throws Exception {
        UserEntity adminUser = userRepository.findByEmail("admin@email.com").orElseThrow();
        adminUser.setPassword(passwordEncoder.encode("adminnew@email.com"));
        userRepository.save(adminUser);

        UpdatePasswordRequestDTO request = objectMapper.readValue(new File("src/test/resources/test-data/users/user-update-password-request.json"), UpdatePasswordRequestDTO.class);
        mockMvc.perform(patch(UserController.BASE_NAME + UserController.CHANGE_PASSWORD_ENDPOINT)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void failed_newPasswordSameAsOld() throws Exception {
        UpdatePasswordRequestDTO request = new UpdatePasswordRequestDTO();
        request.setOldPassword("admin@email.com");
        request.setNewPassword("admin@email.com");
        mockMvc.perform(patch(UserController.BASE_NAME + UserController.CHANGE_PASSWORD_ENDPOINT)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    public void failed_InvalidPassword() throws Exception {
        UpdatePasswordRequestDTO request = new UpdatePasswordRequestDTO();
        request.setOldPassword("adminincorrect@email.com");
        mockMvc.perform(patch(UserController.BASE_NAME + UserController.CHANGE_PASSWORD_ENDPOINT)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}

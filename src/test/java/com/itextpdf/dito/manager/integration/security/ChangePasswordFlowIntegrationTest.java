package com.itextpdf.dito.manager.integration.security;

import com.itextpdf.dito.manager.controller.user.UserController;
import com.itextpdf.dito.manager.dto.user.update.PasswordChangeRequestDTO;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.integration.AbstractIntegrationTest;
import com.itextpdf.dito.manager.repository.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.File;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ChangePasswordFlowIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @AfterEach
    void after() {
        final UserEntity userEntity = userRepository.findByEmail("admin@email.com").orElseThrow();
        userEntity.setModifiedAt(null);
        userRepository.save(userEntity);
    }

    @Test
    public void success() throws Exception {
        UserEntity adminUser = userRepository.findByEmail("admin@email.com").orElseThrow();
        adminUser.setPassword(passwordEncoder.encode("adminnew@email.com"));
        userRepository.save(adminUser);

        PasswordChangeRequestDTO request = objectMapper.readValue(new File("src/test/resources/test-data/users/user-update-password-request.json"), PasswordChangeRequestDTO.class);
        mockMvc.perform(patch(UserController.BASE_NAME + UserController.CHANGE_PASSWORD_ENDPOINT)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void failed_newPasswordSameAsOld() throws Exception {
        PasswordChangeRequestDTO request = new PasswordChangeRequestDTO();
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
        PasswordChangeRequestDTO request = new PasswordChangeRequestDTO();
        request.setOldPassword("adminincorrect@email.com");
        mockMvc.perform(patch(UserController.BASE_NAME + UserController.CHANGE_PASSWORD_ENDPOINT)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}

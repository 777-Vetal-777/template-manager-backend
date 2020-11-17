package com.itextpdf.dito.manager.integration;

import com.itextpdf.dito.manager.dto.user.create.UserCreateRequestDTO;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.io.File;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserFlowIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void clearDb() {
        userRepository.deleteAll();
    }

    @Test
    public void testCreateUser() throws Exception {
        UserCreateRequestDTO request = objectMapper.readValue(new File("src/test/resources/test-data/users/user-create-request.json"), UserCreateRequestDTO.class);
        mockMvc.perform(post("/v1/users")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("firstName").value("Harry"))
                .andExpect(jsonPath("email").value("user@email.com"))
                .andExpect(jsonPath("lastName").value("Kane"))
                .andExpect(jsonPath("active").value("true"));

        assertTrue(userRepository.findByEmailAndActiveTrue("user@email.com").isPresent());
    }

    @Test
    public void testGetAll() throws Exception {
        mockMvc.perform(get("/v1/users")
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
        mockMvc.perform(delete("/v1/users/2"))
                .andExpect(status().isOk());
        UserEntity user = userRepository.findByEmail("test@email.com").orElseThrow();
        assertFalse(user.getActive());
    }
}

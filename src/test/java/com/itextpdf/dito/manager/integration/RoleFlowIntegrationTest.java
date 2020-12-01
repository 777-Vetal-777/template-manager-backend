package com.itextpdf.dito.manager.integration;

import com.itextpdf.dito.manager.controller.role.RoleController;
import com.itextpdf.dito.manager.dto.role.RoleCreateRequestDTO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.io.File;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RoleFlowIntegrationTest extends AbstractIntegrationTest {

    @Test
    @Disabled
    public void testCreateRole() throws Exception {
        RoleCreateRequestDTO request = objectMapper.readValue(new File("src/test/resources/test-data/roles/role-create-request.json"), RoleCreateRequestDTO.class);
        mockMvc.perform(post(RoleController.BASE_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void testGetAll() throws Exception {
        mockMvc.perform(get(RoleController.BASE_NAME)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateCustomRole() throws Exception {
        RoleCreateRequestDTO request = objectMapper.readValue(new File("src/test/resources/test-data/roles/role-update-request.json"), RoleCreateRequestDTO.class);
        mockMvc.perform(put(RoleController.BASE_NAME+"/my-custom-role")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteRole() throws Exception {
        mockMvc.perform(delete(RoleController.BASE_NAME + "/delete-role-name"))
                .andExpect(status().isOk());
    }

}

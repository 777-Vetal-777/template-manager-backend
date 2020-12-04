package com.itextpdf.dito.manager.integration;

import com.itextpdf.dito.manager.controller.role.RoleController;
import com.itextpdf.dito.manager.dto.role.RoleCreateRequestDTO;
import com.itextpdf.dito.manager.entity.RoleEntity;
import com.itextpdf.dito.manager.entity.RoleType;
import com.itextpdf.dito.manager.repository.role.RoleRepository;
import com.itextpdf.dito.manager.repository.role.RoleTypeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.io.File;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RoleFlowIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private RoleTypeRepository roleTypeRepository;

    @Test
    public void testCreateRole() throws Exception {
        RoleCreateRequestDTO request = objectMapper.readValue(new File("src/test/resources/test-data/roles/role-create-request.json"), RoleCreateRequestDTO.class);
        mockMvc.perform(post(RoleController.BASE_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void createRole_WhenPermissionDoesNotExist_ThenReturnNotFound() throws Exception {
        RoleCreateRequestDTO request = objectMapper.readValue(new File("src/test/resources/test-data/roles/role-create-request-with-non-existent-permission.json"), RoleCreateRequestDTO.class);
        mockMvc.perform(post(RoleController.BASE_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void createRole_WhenPermissionIsNotAvailable_ThenReturnBadRequest() throws Exception {
        RoleCreateRequestDTO request = objectMapper.readValue(new File("src/test/resources/test-data/roles/role-create-request-role-not-available-for-customer.json"), RoleCreateRequestDTO.class);
        mockMvc.perform(post(RoleController.BASE_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetAll() throws Exception {
        mockMvc.perform(get(RoleController.BASE_NAME)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getAll_WithSearchFilter() throws Exception {
        mockMvc.perform(get(RoleController.BASE_NAME)
                .param("search", "GLOBAL_ADMINISTRATOR")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateCustomRole() throws Exception {
        RoleCreateRequestDTO request = objectMapper.readValue(new File("src/test/resources/test-data/roles/role-update-request.json"), RoleCreateRequestDTO.class);
        mockMvc.perform(put(RoleController.BASE_NAME + "/my-custom-role")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void delete_success() throws Exception {
        final String roleToBeDeletedName = "delete-role-name";
        RoleEntity roleToBeDeleted = new RoleEntity();
        roleToBeDeleted.setName(roleToBeDeletedName);
        roleToBeDeleted.setType(roleTypeRepository.findByName(RoleType.CUSTOM));
        roleRepository.save(roleToBeDeleted);

        mockMvc.perform(delete(RoleController.BASE_NAME + "/" + roleToBeDeletedName))
                .andExpect(status().isOk());
    }

    @Test
    public void delete_failureForSystemRole() throws Exception {
        final String roleToBeDeletedName = "GLOBAL_ADMINISTRATOR";
        mockMvc.perform(delete(RoleController.BASE_NAME + "/" + roleToBeDeletedName))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void delete_failure_roleNotFound() throws Exception {
        mockMvc.perform(delete(RoleController.BASE_NAME + "/" + "unknown-role-name"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void delete_failure_userWithOnlyThisRole() throws Exception {
        final String roleToBeDeletedName = "GLOBAL_ADMINISTRATOR";

        mockMvc.perform(delete(RoleController.BASE_NAME + "/" + roleToBeDeletedName))
                .andExpect(status().isBadRequest());
    }

}

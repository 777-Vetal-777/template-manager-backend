package com.itextpdf.dito.manager.integration.crud;

import com.itextpdf.dito.manager.controller.role.RoleController;
import com.itextpdf.dito.manager.dto.role.create.RoleCreateRequestDTO;
import com.itextpdf.dito.manager.entity.RoleEntity;
import com.itextpdf.dito.manager.entity.RoleType;
import com.itextpdf.dito.manager.integration.AbstractIntegrationTest;
import com.itextpdf.dito.manager.repository.role.RoleRepository;
import com.itextpdf.dito.manager.repository.role.RoleTypeRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.io.File;
import java.util.Base64;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RoleFlowIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private RoleTypeRepository roleTypeRepository;

    private RoleEntity testRole;

    @AfterEach
    public void tearDown() {
        if (testRole != null) {
            roleRepository.delete(testRole);
        }
    }

    @Test
    public void testCreateRole() throws Exception {
        RoleCreateRequestDTO request = objectMapper.readValue(new File("src/test/resources/test-data/roles/role-create-request.json"), RoleCreateRequestDTO.class);
        mockMvc.perform(post(RoleController.BASE_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        assertTrue(roleRepository.findByName(request.getName()).isPresent());
        testRole = roleRepository.findByName(request.getName()).get();
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
    public void testUpdateCustomRole() throws Exception {
        final String roleToBeUpdatedName = "role-for-update";
        testRole = new RoleEntity();
        testRole.setName(roleToBeUpdatedName);
        testRole.setType(roleTypeRepository.findByName(RoleType.CUSTOM));
        roleRepository.save(testRole);

        RoleCreateRequestDTO request = objectMapper.readValue(new File("src/test/resources/test-data/roles/role-update-request.json"), RoleCreateRequestDTO.class);
        final String encodedRoleName = Base64.getEncoder().encodeToString(roleToBeUpdatedName.getBytes());

        mockMvc.perform(patch(RoleController.BASE_NAME + "/" + encodedRoleName)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value("edited-custom-role-name"));

        testRole.setPermissions(Collections.emptySet());
        roleRepository.save(testRole);
    }

    @Test
    public void delete_success() throws Exception {
        final String roleToBeDeletedName = "delete-role-name";
        testRole = new RoleEntity();
        testRole.setName(roleToBeDeletedName);
        testRole.setType(roleTypeRepository.findByName(RoleType.CUSTOM));
        roleRepository.save(testRole);

        mockMvc.perform(delete(RoleController.BASE_NAME + "/" + Base64.getEncoder().encodeToString(roleToBeDeletedName.getBytes())))
                .andExpect(status().isOk());
    }

    @Test
    public void delete_failureForSystemRole() throws Exception {
        final String roleToBeDeletedName = "GLOBAL_ADMINISTRATOR";
        mockMvc.perform(delete(RoleController.BASE_NAME + "/" + Base64.getEncoder().encodeToString(roleToBeDeletedName.getBytes())))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void delete_failure_roleNotFound() throws Exception {
        mockMvc.perform(delete(RoleController.BASE_NAME + "/" + Base64.getEncoder().encodeToString("unknown-role-name".getBytes())))
                .andExpect(status().isNotFound());
    }

    @Test
    public void delete_failure_userWithOnlyThisRole() throws Exception {
        final String roleToBeDeletedName = "GLOBAL_ADMINISTRATOR";

        mockMvc.perform(delete(RoleController.BASE_NAME + "/" + Base64.getEncoder().encodeToString(roleToBeDeletedName.getBytes())))
                .andExpect(status().isBadRequest());
    }

}

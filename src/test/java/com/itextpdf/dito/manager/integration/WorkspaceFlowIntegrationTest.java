package com.itextpdf.dito.manager.integration;

import com.itextpdf.dito.manager.controller.workspace.WorkspaceController;
import com.itextpdf.dito.manager.dto.workspace.WorkspaceDTO;
import com.itextpdf.dito.manager.dto.workspace.create.WorkspaceCreateRequestDTO;
import com.itextpdf.dito.manager.repository.workspace.WorkspaceRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class WorkspaceFlowIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private WorkspaceRepository workspaceRepository;

    @AfterEach
    public void teardown() {
        workspaceRepository.deleteAll();
    }

    @Test
    public void testCreateWorkspace() throws Exception {
        WorkspaceCreateRequestDTO request = objectMapper.readValue(new File("src/test/resources/test-data/workspaces/workspace-create-request.json"), WorkspaceCreateRequestDTO.class);
        mockMvc.perform(post(WorkspaceController.BASE_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("name").value("workspace-test"))
                .andExpect(jsonPath("language").value("ENG"))
                .andExpect(jsonPath("timezone").value("America/Sao_Paulo"));

        assertTrue(workspaceRepository.findById(1L).isPresent());
    }

    @Test
    public void testCreateWorkspaceWithExistingName() throws Exception {
        WorkspaceCreateRequestDTO request = objectMapper.readValue(new File("src/test/resources/test-data/workspaces/workspace-create-request.json"), WorkspaceCreateRequestDTO.class);
        mockMvc.perform(post(WorkspaceController.BASE_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(post(WorkspaceController.BASE_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    public void testUpdateWorkspace() throws Exception {
        WorkspaceCreateRequestDTO createRequest = objectMapper.readValue(new File("src/test/resources/test-data/workspaces/workspace-create-request.json"), WorkspaceCreateRequestDTO.class);
        mockMvc.perform(post(WorkspaceController.BASE_NAME)
                .content(objectMapper.writeValueAsString(createRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        WorkspaceDTO updateRequest = objectMapper.readValue(new File("src/test/resources/test-data/workspaces/workspace-create-request.json"), WorkspaceDTO.class);
        mockMvc.perform(put(WorkspaceController.BASE_NAME)
                .content(objectMapper.writeValueAsString(updateRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(updateRequest.getName()))
                .andExpect(jsonPath("language").value(updateRequest.getLanguage()))
                .andExpect(jsonPath("timezone").value(updateRequest.getTimezone()));
    }

    @Test
    public void testGetWorkspace() throws Exception {
        WorkspaceCreateRequestDTO request = objectMapper.readValue(new File("src/test/resources/test-data/workspaces/workspace-create-request.json"), WorkspaceCreateRequestDTO.class);
        mockMvc.perform(post(WorkspaceController.BASE_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        mockMvc.perform(get(WorkspaceController.BASE_NAME)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(request.getName()))
                .andExpect(jsonPath("language").value(request.getLanguage()))
                .andExpect(jsonPath("timezone").value(request.getTimezone()));
    }


    @Test
    public void testGetWorkspaceNotFound() throws Exception {
        mockMvc.perform(get(WorkspaceController.BASE_NAME)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}

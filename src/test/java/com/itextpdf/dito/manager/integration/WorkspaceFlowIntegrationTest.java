package com.itextpdf.dito.manager.integration;

import com.itextpdf.dito.manager.dto.workspace.create.WorkspaceCreateRequestDTO;
import com.itextpdf.dito.manager.repository.workspace.WorkspaceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class WorkspaceFlowIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private WorkspaceRepository workspaceRepository;

    @BeforeEach
    public void clearDb() {
        workspaceRepository.deleteAll();
    }

    @Test
    public void testCreateWorkspace() throws Exception {
        WorkspaceCreateRequestDTO request = objectMapper.readValue(new File("src/test/resources/test-data/workspaces/workspace-create-request.json"), WorkspaceCreateRequestDTO.class);
        mockMvc.perform(post("/v1/workspaces")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("name").value("workspace-test"))
                .andExpect(jsonPath("language").value("ENG"))
                .andExpect(jsonPath("timezone").value("America/Sao_Paulo"));

        assertTrue(workspaceRepository.findById(1L).isPresent());
    }
}

package com.itextpdf.dito.manager.integration.editor;

import com.itextpdf.dito.manager.integration.AbstractIntegrationTest;
import com.itextpdf.dito.manager.integration.editor.controller.workspace.WorkspaceManagementController;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class WorkspaceManagementFlowIntegrationTest extends AbstractIntegrationTest {

    @Test
    void shouldReturnWorkspaceInfoDescriptor() throws Exception {
        final String workspaceIdEncoded = "asdf-ghjk-lzxc-vbnm";
        final MvcResult result = mockMvc.perform(get(WorkspaceManagementController.WORKSPACE_INFO_URL, workspaceIdEncoded)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("displayName").value("workspace-test"))
                .andExpect(jsonPath("language").value("ENG"))
                .andExpect(jsonPath("timeZone").value("Europe/Brussels"))
                .andReturn();
        assertNotNull(result.getResponse());
    }

}

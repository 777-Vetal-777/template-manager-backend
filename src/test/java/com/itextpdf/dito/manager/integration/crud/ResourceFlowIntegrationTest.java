package com.itextpdf.dito.manager.integration.crud;

import com.itextpdf.dito.manager.controller.resource.ResourceController;
import com.itextpdf.dito.manager.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Base64;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ResourceFlowIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void test_list() throws Exception {
        mockMvc.perform(get(ResourceController.BASE_NAME)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @Disabled
    public void test_getByName() throws Exception {
        final String encodedResourceName = Base64.getEncoder().encodeToString("resource-name".getBytes());
        mockMvc.perform(get(ResourceController.BASE_NAME + "/" + encodedResourceName)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
        .andExpect(jsonPath("name").value("resource-name"))
        .andExpect(jsonPath("type").isEmpty())
        .andExpect(jsonPath("comment").isEmpty());
    }
}

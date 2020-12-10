package com.itextpdf.dito.manager.integration.filtering;

import com.itextpdf.dito.manager.controller.resource.ResourceController;
import com.itextpdf.dito.manager.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ResourceFilterAndSearchIntegrationTest extends AbstractIntegrationTest implements FilterAndSearchTest{

    @Test
    @Override
    public void test_search() throws Exception {
        //TODO implement within https://jira.itextsupport.com/browse/DTM-472
        mockMvc.perform(get(ResourceController.BASE_NAME)
                .param("search", "resource-name")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @Override
    public void test_filtering() throws Exception {
        //TODO implement within https://jira.itextsupport.com/browse/DTM-472
        mockMvc.perform(get(ResourceController.BASE_NAME)
                .param("name", "resource-name")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get(ResourceController.BASE_NAME)
                .param("type", "resource-type")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get(ResourceController.BASE_NAME)
                .param("comment", "resource-comment")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @Override
    public void test_searchAndFiltering() throws Exception {
        //TODO implement within https://jira.itextsupport.com/browse/DTM-472
        mockMvc.perform(get(ResourceController.BASE_NAME)
                .param("type", "resource-type")
                .param("search", "resource-name")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}

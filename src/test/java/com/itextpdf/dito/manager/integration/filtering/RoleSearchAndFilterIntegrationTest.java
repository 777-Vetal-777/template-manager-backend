package com.itextpdf.dito.manager.integration.filtering;

import com.itextpdf.dito.manager.controller.role.RoleController;
import com.itextpdf.dito.manager.entity.RoleEntity;
import com.itextpdf.dito.manager.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for filtering and search in {@link RoleEntity} table.
 */
public class RoleSearchAndFilterIntegrationTest extends AbstractIntegrationTest implements FilterAndSearchTest {

    @Override
    public void test_search() throws Exception {
        mockMvc.perform(get(RoleController.BASE_NAME)
                .param("search", "GLOBAL_ADMINISTRATOR")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Override
    @Test
    public void test_filtering() throws Exception {
        mockMvc.perform(get(RoleController.BASE_NAME)
                .param("name", "global_administrator")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("GLOBAL_ADMINISTRATOR")));

        mockMvc.perform(get(RoleController.BASE_NAME)
                .param("type", "CUSTOM")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));

        mockMvc.perform(get(RoleController.BASE_NAME)
                .param("type", "CUSTOM")
                .param("type", "SYSTEM")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)));
    }

    @Override
    @Test
    public void test_searchAndFiltering() throws Exception {
        mockMvc.perform(get(RoleController.BASE_NAME)
                .param("name", "GLOBAL_ADMINISTRATOR")
                .param("search", "admin")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("GLOBAL_ADMINISTRATOR")));

        mockMvc.perform(get(RoleController.BASE_NAME)
                .param("name", "GLOBAL_ADMINISTRATOR")
                .param("search", "not-existing-user")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }
}

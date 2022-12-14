package com.itextpdf.dito.manager.integration.filtering;

import com.itextpdf.dito.manager.controller.role.RoleController;
import com.itextpdf.dito.manager.entity.RoleEntity;
import com.itextpdf.dito.manager.integration.AbstractIntegrationTest;
import com.itextpdf.dito.manager.repository.role.RoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for filtering and search in {@link RoleEntity} table.
 */
class RoleSearchAndFilterIntegrationTest extends AbstractIntegrationTest implements FilterAndSearchTest {

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

        final MvcResult result =
                mockMvc.perform(get(RoleController.BASE_NAME)
                        .param("type", "SYSTEM")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.content", hasSize(3)))
                        .andReturn();

        assertNotNull(result.getResponse());
    }

    @Override
    @Test
    public void test_searchAndFiltering() throws Exception {
        mockMvc.perform(get(RoleController.BASE_NAME)
                .param("name", "GLOBAL_ADMINISTRATOR")
                .param("search", "syst"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("GLOBAL_ADMINISTRATOR")));

        final MvcResult result =
                mockMvc.perform(get(RoleController.BASE_NAME)
                        .param("name", "GLOBAL_ADMINISTRATOR")
                        .param("search", "not-existing-user"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.content", hasSize(0)))
                        .andReturn();

        assertNotNull(result.getResponse());
    }

    @Override
    @Test
    public void test_sortWithSearch() throws Exception {
        for (String field : RoleRepository.SUPPORTED_SORT_FIELDS) {
            final MvcResult result =
                    mockMvc.perform(get(RoleController.BASE_NAME)
                            .param("sort", field)
                            .param("search", "not-existing-user")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isOk())
                            .andReturn();

            assertNotNull(result.getResponse());
        }
    }

    @Override
    @Test
    public void test_sortWithFiltering() throws Exception {
        for (String field : RoleRepository.SUPPORTED_SORT_FIELDS) {
            final MvcResult result =
                    mockMvc.perform(get(RoleController.BASE_NAME)
                            .param("sort", field)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isOk())
                            .andReturn();

            assertNotNull(result.getResponse());
        }
    }
}

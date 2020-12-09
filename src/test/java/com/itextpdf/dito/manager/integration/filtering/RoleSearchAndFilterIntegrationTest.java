package com.itextpdf.dito.manager.integration.filtering;

import com.itextpdf.dito.manager.controller.role.RoleController;
import com.itextpdf.dito.manager.entity.RoleEntity;
import com.itextpdf.dito.manager.integration.AbstractIntegrationTest;
import com.itextpdf.dito.manager.repository.role.RoleRepository;
import com.itextpdf.dito.manager.repository.role.RoleTypeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for filtering and search in {@link RoleEntity} table.
 */
public class RoleSearchAndFilterIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private RoleTypeRepository roleTypeRepository;

    @Test
    public void getAll_WithSearchFilter() throws Exception {
        mockMvc.perform(get(RoleController.BASE_NAME)
                .param("search", "GLOBAL_ADMINISTRATOR")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}

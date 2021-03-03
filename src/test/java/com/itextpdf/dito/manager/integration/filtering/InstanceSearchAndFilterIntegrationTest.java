package com.itextpdf.dito.manager.integration.filtering;

import com.itextpdf.dito.manager.controller.instance.InstanceController;
import com.itextpdf.dito.manager.integration.AbstractIntegrationTest;
import com.itextpdf.dito.manager.repository.instance.InstanceRepository;
import com.itextpdf.dito.manager.repository.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class InstanceSearchAndFilterIntegrationTest extends AbstractIntegrationTest implements FilterAndSearchTest {

    @Autowired
    private InstanceRepository instanceRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Test
    public void test_filtering() throws Exception {
        assertEquals(1, instanceRepository.findAll().size());

        mockMvc.perform(get(InstanceController.BASE_NAME + InstanceController.PAGEABLE_ENDPOINT)
                .param("name", "default"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is(defaultInstanceEntity.getName())));

        mockMvc.perform(get(InstanceController.BASE_NAME + InstanceController.PAGEABLE_ENDPOINT)
                .param("socket", "Socket"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is(defaultInstanceEntity.getName())));

        mockMvc.perform(get(InstanceController.BASE_NAME + InstanceController.PAGEABLE_ENDPOINT)
                .param("createdOn", "01/01/1970")
                .param("createdOn", "01/01/1980"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    @Override
    @Test
    public void test_searchAndFiltering() throws Exception {
        assertEquals(1, instanceRepository.findAll().size());

        mockMvc.perform(get(InstanceController.BASE_NAME + InstanceController.PAGEABLE_ENDPOINT)
                .param("name", "instance")
                .param("search", "default"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is(defaultInstanceEntity.getName())));

        mockMvc.perform(get(InstanceController.BASE_NAME + InstanceController.PAGEABLE_ENDPOINT)
                .param("name", "instance")
                .param("search", "not-existing-user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    @Override
    @Test
    public void test_sortWithSearch() throws Exception {
        assertEquals(1, instanceRepository.findAll().size());

        for (String field : InstanceRepository.SUPPORTED_SORT_FIELDS) {
            mockMvc.perform(get(InstanceController.BASE_NAME)
                    .param("sort", field)
                    .param("search", "not-existing-user"))
                    .andExpect(status().isOk());
        }
    }

    @Override
    @Test
    public void test_sortWithFiltering() throws Exception {
        assertEquals(1, instanceRepository.findAll().size());

        for (String field : InstanceRepository.SUPPORTED_SORT_FIELDS) {
            mockMvc.perform(get(InstanceController.BASE_NAME)
                    .param("sort", field)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }
    }
}

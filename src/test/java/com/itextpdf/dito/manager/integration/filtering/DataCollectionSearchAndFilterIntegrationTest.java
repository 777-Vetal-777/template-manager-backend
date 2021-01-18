package com.itextpdf.dito.manager.integration.filtering;

import com.itextpdf.dito.manager.controller.datacollection.DataCollectionController;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionType;
import com.itextpdf.dito.manager.integration.AbstractIntegrationTest;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionRepository;
import com.itextpdf.dito.manager.service.datacollection.DataCollectionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DataCollectionSearchAndFilterIntegrationTest extends AbstractIntegrationTest implements FilterAndSearchTest {
    private static final String NAME = "test-data-collection";
    private static final String TYPE = "JSON";
    @Autowired
    private DataCollectionService dataCollectionService;
    @Autowired
    private DataCollectionRepository dataCollectionRepository;

    @BeforeEach
    public void init() throws Exception {
        dataCollectionService.create("data-collection-search-test", DataCollectionType.JSON, "{\"file\":\"data\"}".getBytes(), "datacollection.json", "admin@email.com");

    }

    @AfterEach
    public void clearDb() {
        dataCollectionService.delete("data-collection-search-test", "admin@email.com");
    }

    @Test
    public void getAll_WhenSortedByUnsupportedField_ThenResponseIsBadRequest() throws Exception {
        mockMvc.perform(get(DataCollectionController.BASE_NAME)
                .param("sort", "unsupportedField")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Override
    @Test
    public void test_filtering() throws Exception {
        mockMvc.perform(get(DataCollectionController.BASE_NAME)
                .param("name", "data-COLLECTION-search-test"))
                .andExpect(jsonPath("$.content", hasSize(1)));
        mockMvc.perform(get(DataCollectionController.BASE_NAME)
                .param("modifiedOn", "01/01/1970")
                .param("modifiedOn", "01/01/1980"))
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    @Override
    @Test
    public void test_searchAndFiltering() throws Exception {
        mockMvc.perform(get(DataCollectionController.BASE_NAME)
                .param("search", "data-COLLECTION-search-test"))
                .andExpect(jsonPath("$.content", hasSize(1)));

        mockMvc.perform(get(DataCollectionController.BASE_NAME)
                .param("search", "admin")
                .param("name", "unknown-template"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    @Override
    @Test
    public void test_sortWithSearch() throws Exception {
        for (String field : DataCollectionRepository.SUPPORTED_SORT_FIELDS) {
            mockMvc.perform(get(DataCollectionController.BASE_NAME)
                    .param("sort", field)
                    .param("search", "template"))
                    .andExpect(status().isOk());
        }
    }

    @Override
    @Test
    public void test_sortWithFiltering() throws Exception {
        for (String field : DataCollectionRepository.SUPPORTED_SORT_FIELDS) {
            mockMvc.perform(get(DataCollectionController.BASE_NAME)
                    .param("sort", field))
                    .andExpect(status().isOk());
        }
    }
}

package com.itextpdf.dito.manager.integration.filtering;

import com.itextpdf.dito.manager.controller.datacollection.DataCollectionController;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionType;
import com.itextpdf.dito.manager.integration.AbstractIntegrationTest;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionFileRepository;
import com.itextpdf.dito.manager.service.datacollection.DataCollectionService;
import com.itextpdf.kernel.xmp.impl.Base64;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DataCollectionVersionSearchAndFilterIntegrationTest extends AbstractIntegrationTest implements FilterAndSearchTest {
    private static final String DATACOLLECTION_NAME = "data-collection-test";
    private static final String DATACOLLECTION_BASE64_ENCODED_NAME = Base64.encode(DATACOLLECTION_NAME);
    private static final String TYPE = "JSON";
    private static final String VERSIONS_URN = DataCollectionController.BASE_NAME + DataCollectionController.DATA_COLLECTION_VERSIONS_ENDPOINT_WITH_PATH_VARIABLE;
    @Autowired
    private DataCollectionService dataCollectionService;

    @BeforeEach
    public void init() {
        dataCollectionService.create(DATACOLLECTION_NAME, DataCollectionType.valueOf(TYPE), "{\"file\":\"data\"}".getBytes(), "datacollection.json", "admin@email.com");
        createDatacollectionVersion("first comment");
        createDatacollectionVersion("second comment");
    }

    @AfterEach
    public void clearDb() {
        dataCollectionService.delete(DATACOLLECTION_NAME);
    }

    @Test
    public void getAll_WhenSortedByUnsupportedField_ThenResponseIsBadRequest() throws Exception {
        mockMvc.perform(get(VERSIONS_URN, DATACOLLECTION_BASE64_ENCODED_NAME)
                .param("sort", "unsupportedField")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Override
    @Test
    public void test_filtering() throws Exception {
        mockMvc.perform(get(VERSIONS_URN, DATACOLLECTION_BASE64_ENCODED_NAME)
                .param("comment", "first"))
                .andExpect(jsonPath("$.content", hasSize(1)));
        mockMvc.perform(get(VERSIONS_URN, DATACOLLECTION_BASE64_ENCODED_NAME)
                .param("comment", "comment"))
                .andExpect(jsonPath("$.content", hasSize(2)));
    }

    @Override
    @Test
    public void test_searchAndFiltering() throws Exception {
        mockMvc.perform(get(VERSIONS_URN, DATACOLLECTION_BASE64_ENCODED_NAME)
                .param("search", "first"))
                .andExpect(jsonPath("$.content", hasSize(1)));

        mockMvc.perform(get(VERSIONS_URN, DATACOLLECTION_BASE64_ENCODED_NAME)
                .param("search", "admin")
                .param("modifiedBy", "unknown"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    @Override
    @Test
    public void test_sortWithSearch() throws Exception {
        for (String field : DataCollectionFileRepository.SUPPORTED_VERSION_SORT_FIELDS) {
            mockMvc.perform(get(VERSIONS_URN, DATACOLLECTION_BASE64_ENCODED_NAME)
                    .param("sort", field)
                    .param("search", "comment"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(2)));
        }
    }

    @Override
    @Test
    public void test_sortWithFiltering() throws Exception {
        for (String field : DataCollectionFileRepository.SUPPORTED_VERSION_SORT_FIELDS) {
            mockMvc.perform(get(VERSIONS_URN, DATACOLLECTION_BASE64_ENCODED_NAME)
                    .param("sort", field)
                    .param("deployed", String.valueOf(false)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(3)));
        }
    }

    private void createDatacollectionVersion(String  comment) {
        dataCollectionService.createNewVersion(DATACOLLECTION_NAME, DataCollectionType.valueOf(TYPE), "{\"file\":\"modified data\"}".getBytes(), "datacollection.json", "admin@email.com", comment);
    }
}

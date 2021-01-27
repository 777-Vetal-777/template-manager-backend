package com.itextpdf.dito.manager.integration.filtering;

import com.itextpdf.dito.manager.controller.datacollection.DataCollectionController;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionType;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.integration.AbstractIntegrationTest;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionRepository;
import com.itextpdf.dito.manager.repository.datasample.DataSampleRepository;
import com.itextpdf.dito.manager.repository.template.TemplateRepository;
import com.itextpdf.dito.manager.service.datacollection.DataCollectionService;
import com.itextpdf.dito.manager.service.datasample.DataSampleService;
import com.itextpdf.kernel.xmp.impl.Base64;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static com.itextpdf.dito.manager.controller.datacollection.DataCollectionController.DATA_SAMPLE_ENDPOINT;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DataSampleSearchAndFilterIntegrationTest extends AbstractIntegrationTest implements FilterAndSearchTest {
    private static final String DATACOLLECTION_NAME = "data-collection-search-test";
    private static final String DATACOLLECTION_BASE64_ENCODED_NAME = Base64.encode(DATACOLLECTION_NAME);

    private static final String TYPE = "JSON";
    @Autowired
    private DataCollectionService dataCollectionService;
    @Autowired
    private DataSampleService dataSampleService;
    @Autowired
    private DataSampleRepository dataSampleRepository;
    @Autowired
    private DataCollectionRepository dataCollectionRepository;

    @Autowired
    private TemplateRepository templateRepository;
    
    @BeforeEach
    public void init() {
        DataCollectionEntity dataCollectionEntity = dataCollectionService.create(DATACOLLECTION_NAME, DataCollectionType.valueOf(TYPE), "{\"file\":\"data\"}".getBytes(), "datacollection.json", "admin@email.com");
        dataSampleService.create(dataCollectionEntity, "name1", "fileName2", "{\"file\":\"data2\"}", "comment2", "admin@email.com");
        dataSampleService.create(dataCollectionEntity, "name2", "fileName3", "{\"file\":\"data3\"}", "comment3", "admin@email.com");
        dataSampleService.create(dataCollectionEntity, "name3", "fileName4", "{\"file\":\"data4\"}", "comment3", "admin@email.com");

    }

    @AfterEach
    public void clearDb() {	
        dataSampleRepository.deleteAll();
        templateRepository.deleteAll();
        dataCollectionRepository.deleteAll();

    }

    @Override
    @Test
    public void test_filtering() throws Exception {
        mockMvc.perform(get(DataCollectionController.BASE_NAME + "/" + DATACOLLECTION_BASE64_ENCODED_NAME + DATA_SAMPLE_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(3)));

        mockMvc.perform(get(DataCollectionController.BASE_NAME + "/" + DATACOLLECTION_BASE64_ENCODED_NAME + DATA_SAMPLE_ENDPOINT)
                .param("name", "name2")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)));

        mockMvc.perform(get(DataCollectionController.BASE_NAME + "/" + DATACOLLECTION_BASE64_ENCODED_NAME + DATA_SAMPLE_ENDPOINT)
                .param("name", "name2")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)));

    }

    @Override
    @Test
    public void test_searchAndFiltering() throws Exception {
        mockMvc.perform(get(DataCollectionController.BASE_NAME + "/" + DATACOLLECTION_BASE64_ENCODED_NAME + DATA_SAMPLE_ENDPOINT)
                .param("search", "name")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(3)));
        mockMvc.perform(get(DataCollectionController.BASE_NAME + "/" + DATACOLLECTION_BASE64_ENCODED_NAME + DATA_SAMPLE_ENDPOINT)
                .param("search", "name")
                .param("name", "name2")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)));


    }

    @Override
    @Test
    public void test_sortWithSearch() throws Exception {
        for (String field : DataSampleRepository.SUPPORTED_SORT_FIELDS) {
            mockMvc.perform(get(DataCollectionController.BASE_NAME + "/" + DATACOLLECTION_BASE64_ENCODED_NAME + DATA_SAMPLE_ENDPOINT)
                    .param("sort", field)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }
    }


    @Override
    @Test
    public void test_sortWithFiltering() throws Exception {
        for (String field : DataSampleRepository.SUPPORTED_SORT_FIELDS) {
            mockMvc.perform(get(DataCollectionController.BASE_NAME + "/" + DATACOLLECTION_BASE64_ENCODED_NAME + DATA_SAMPLE_ENDPOINT)
                    .param("name", "name2")
                    .param("sort", field)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }
    }
}

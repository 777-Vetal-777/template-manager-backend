package com.itextpdf.dito.manager.integration.filtering;

import com.itextpdf.dito.manager.controller.datacollection.DataCollectionController;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionType;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.datasample.DataSampleEntity;
import com.itextpdf.dito.manager.integration.AbstractIntegrationTest;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionRepository;
import com.itextpdf.dito.manager.repository.datasample.DataSampleFileRepository;
import com.itextpdf.dito.manager.repository.datasample.DataSampleRepository;
import com.itextpdf.dito.manager.service.datacollection.DataCollectionService;
import com.itextpdf.dito.manager.service.datasample.DataSampleService;
import com.itextpdf.kernel.xmp.impl.Base64;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;


import static com.itextpdf.dito.manager.controller.datacollection.DataCollectionController.DATA_SAMPLE_ENDPOINT;
import static com.itextpdf.dito.manager.controller.datacollection.DataCollectionController.VERSIONS_ENDPOINT;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DataSampleVersionSearchAndFilterIntegrationTest extends AbstractIntegrationTest implements FilterAndSearchTest {

    @Autowired
    private DataCollectionService dataCollectionService;
    @Autowired
    private DataSampleRepository dataSampleRepository;
    @Autowired
    private DataCollectionRepository dataCollectionRepository;
    @Autowired
    private DataSampleService dataSampleService;
    private static final String DATACOLLECTION_NAME = "data-collection-search-test";
    private static final String DATASAMPLE_BASE64_ENCODED_NAME = Base64.encode("name");
    private static final String DATACOLLECTION_BASE64_ENCODED_NAME = Base64.encode(DATACOLLECTION_NAME);

    private static final String TYPE = "JSON";

    @BeforeEach
    public void init() {
        DataCollectionEntity dataCollectionEntity = dataCollectionService.create(DATACOLLECTION_NAME, DataCollectionType.valueOf(TYPE), "{\"file\":\"data\"}".getBytes(), "datacollection.json", "admin@email.com");
        DataSampleEntity dataSampleEntity = dataSampleService.create(dataCollectionEntity, "name", "fileName", "{\"file\":\"data2\"}", "comment", "admin@email.com");
        dataSampleService.createNewVersion(dataSampleEntity.getName(), "{\"file\":\"data3\"}", "fileName3", "admin@email.com", "comment3");
        dataSampleService.createNewVersion(dataSampleEntity.getName(), "{\"file\":\"data4\"}", "fileName4", "admin@email.com", "comment4");
        dataSampleService.createNewVersion(dataSampleEntity.getName(), "{\"file\":\"data5\"}", "fileName5", "admin@email.com", "comment5");
        dataSampleService.createNewVersion(dataSampleEntity.getName(), "{\"file\":\"data6\"}", "fileName6", "admin@email.com", "comment6");
        dataSampleService.createNewVersion(dataSampleEntity.getName(), "{\"file\":\"data7\"}", "fileName7", "admin@email.com", "comment7");
        dataSampleService.createNewVersion(dataSampleEntity.getName(), "{\"file\":\"data8\"}", "fileName8", "admin@email.com", "comment7");
    }

    @AfterEach
    public void clearDb() {
        dataSampleRepository.deleteAll();
        dataCollectionRepository.deleteAll();
    }

    @Override
    @Test
    public void test_filtering() throws Exception {

        mockMvc.perform(get(DataCollectionController.BASE_NAME + "/" + DATACOLLECTION_BASE64_ENCODED_NAME + DATA_SAMPLE_ENDPOINT + "/" + DATASAMPLE_BASE64_ENCODED_NAME + VERSIONS_ENDPOINT)
                .param("version", "2")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)));
        mockMvc.perform(get(DataCollectionController.BASE_NAME + "/" + DATACOLLECTION_BASE64_ENCODED_NAME + DATA_SAMPLE_ENDPOINT + "/" + DATASAMPLE_BASE64_ENCODED_NAME + VERSIONS_ENDPOINT)
                .param("comment", "comment3")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)));
        mockMvc.perform(get(DataCollectionController.BASE_NAME + "/" + DATACOLLECTION_BASE64_ENCODED_NAME + DATA_SAMPLE_ENDPOINT + "/" + DATASAMPLE_BASE64_ENCODED_NAME + VERSIONS_ENDPOINT)
                .param("comment", "comment7")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)));
        mockMvc.perform(get(DataCollectionController.BASE_NAME + "/" + DATACOLLECTION_BASE64_ENCODED_NAME + DATA_SAMPLE_ENDPOINT + "/" + DATASAMPLE_BASE64_ENCODED_NAME + VERSIONS_ENDPOINT)
                .param("comment", "comment7")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[*].version", containsInAnyOrder(6, 7)));

    }

    @Override
    @Test
    public void test_searchAndFiltering() throws Exception {
        mockMvc.perform(get(DataCollectionController.BASE_NAME + "/" + DATACOLLECTION_BASE64_ENCODED_NAME + DATA_SAMPLE_ENDPOINT + "/" + DATASAMPLE_BASE64_ENCODED_NAME + VERSIONS_ENDPOINT)
                .param("search", "comment7")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[*].version", containsInAnyOrder(6, 7)));

        mockMvc.perform(get(DataCollectionController.BASE_NAME + "/" + DATACOLLECTION_BASE64_ENCODED_NAME + DATA_SAMPLE_ENDPOINT + "/" + DATASAMPLE_BASE64_ENCODED_NAME + VERSIONS_ENDPOINT)
                .param("search", "comment")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(7)));
    }

    @Override
    @Test
    public void test_sortWithSearch() throws Exception {
        for (String field : DataSampleFileRepository.SUPPORTED_SORT_FIELDS) {
            mockMvc.perform(get(DataCollectionController.BASE_NAME + "/" + DATACOLLECTION_BASE64_ENCODED_NAME + DATA_SAMPLE_ENDPOINT + "/" + DATASAMPLE_BASE64_ENCODED_NAME + VERSIONS_ENDPOINT)
                    .param("sort", field)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }
    }

    @Override
    @Test
    public void test_sortWithFiltering() throws Exception {
        for (String field : DataSampleFileRepository.SUPPORTED_SORT_FIELDS) {
            mockMvc.perform(get(DataCollectionController.BASE_NAME + "/" + DATACOLLECTION_BASE64_ENCODED_NAME + DATA_SAMPLE_ENDPOINT + "/" + DATASAMPLE_BASE64_ENCODED_NAME + VERSIONS_ENDPOINT)
                    .param("version", "2")
                    .param("sort", field)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }
    }
}

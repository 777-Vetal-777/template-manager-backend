package com.itextpdf.dito.manager.integration;

import com.itextpdf.dito.manager.controller.datacollection.DataCollectionController;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionCreateRequestDTO;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DataCollectionFlowIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private DataCollectionRepository dataCollectionRepository;

    @BeforeEach
    public void clearDb() {
        dataCollectionRepository.deleteAll();
    }

    @Test
    public void testCreateDataCollection() throws Exception {
        DataCollectionCreateRequestDTO request = objectMapper.readValue(new File("src/test/resources/test-data/datacollections/data-collection-create-request.json"), DataCollectionCreateRequestDTO.class);
        mockMvc.perform(post(DataCollectionController.BASE_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value("1"))
                .andExpect(jsonPath("name").value("test-data-collection"))
                .andExpect(jsonPath("type").value("JSON"))
                .andExpect(jsonPath("modifiedOn").isNotEmpty());

        assertTrue(dataCollectionRepository.findByName("test-data-collection").isPresent());
    }

    @Test
    public void testGetList() throws Exception {
        mockMvc.perform(get(DataCollectionController.BASE_NAME)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}

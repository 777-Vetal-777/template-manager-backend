package com.itextpdf.dito.manager.integration;

import com.itextpdf.dito.manager.controller.datacollection.DataCollectionController;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionCreateRequestDTO;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
        final MockMultipartFile file = new MockMultipartFile("attachment", "any-name.json", "text/plain", "file data".getBytes());
        final DataCollectionCreateRequestDTO requestDto = objectMapper.readValue(new File("src/test/resources/test-data/datacollections/data-collection-create-request.json"), DataCollectionCreateRequestDTO.class);
        final MockMultipartFile multipartRequestDto = new MockMultipartFile("requestDTO", "", "application/json", objectMapper.writeValueAsString(requestDto).getBytes());
        performPostFilesInteraction(URI.create(DataCollectionController.BASE_NAME), file, multipartRequestDto).andExpect(status().isCreated())
                .andExpect(jsonPath("id").value("1"))
                .andExpect(jsonPath("name").value(requestDto.getName()))
                .andExpect(jsonPath("type").value("JSON"))
                .andExpect(jsonPath("modifiedOn").isNotEmpty());
        assertTrue(dataCollectionRepository.existsByName(requestDto.getName()));
    }

    @Test
    public void shouldDropFileTypeNotSupportedException() throws Exception {
        final MockMultipartFile file = new MockMultipartFile("attachment", "any-name.pdf", "text/plain", "file data".getBytes());
        final DataCollectionCreateRequestDTO requestDto = objectMapper.readValue(new File("src/test/resources/test-data/datacollections/data-collection-create-request.json"), DataCollectionCreateRequestDTO.class);
        final MockMultipartFile multipartRequestDto = new MockMultipartFile("requestDTO", "", "application/json", objectMapper.writeValueAsString(requestDto).getBytes());
        performPostFilesInteraction(URI.create(DataCollectionController.BASE_NAME), file, multipartRequestDto).andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value("File type is not supported"))
                .andExpect(jsonPath("details").value("File type: pdf"));
    }

    @Test
    public void testGetList() throws Exception {
        mockMvc.perform(get(DataCollectionController.BASE_NAME)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}

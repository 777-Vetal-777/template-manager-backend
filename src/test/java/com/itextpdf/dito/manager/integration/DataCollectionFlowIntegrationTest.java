package com.itextpdf.dito.manager.integration;

import com.itextpdf.dito.manager.controller.datacollection.DataCollectionController;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionCreateRequestDTO;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DataCollectionFlowIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private DataCollectionRepository dataCollectionRepository;

    @AfterEach
    public void clearDb() {
        dataCollectionRepository.deleteAll();
    }

    @Test
    public void test_success_createAndGet() throws Exception {
        //CREATE
        final MockMultipartFile file = new MockMultipartFile("attachment", "any-name.json", "text/plain", "file data".getBytes());
        final DataCollectionCreateRequestDTO requestDto = objectMapper.readValue(new File("src/test/resources/test-data/datacollections/data-collection-create-request.json"), DataCollectionCreateRequestDTO.class);
        final URI uri = UriComponentsBuilder.fromUriString(DataCollectionController.BASE_NAME)
                .queryParam("name", requestDto.getName())
                .queryParam("type", requestDto.getType())
                .build().encode().toUri();


        performPostFilesInteraction(uri, file).andExpect(status().isCreated())
                .andExpect(jsonPath("id").value("1"))
                .andExpect(jsonPath("name").value(requestDto.getName()))
                .andExpect(jsonPath("type").value("JSON"))
                .andExpect(jsonPath("modifiedOn").isNotEmpty())
                .andExpect(jsonPath("fileName").value(file.getOriginalFilename()))
                .andExpect(jsonPath("createdOn").isNotEmpty());
        assertTrue(dataCollectionRepository.existsByName(requestDto.getName()));

        //GET by name
        mockMvc.perform(get(DataCollectionController.BASE_NAME + "/" + requestDto.getName())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //UPDATE by name
        final String collectionName = requestDto.getName();
        final String newCollectionName = "new-collection-name";
        requestDto.setName(newCollectionName);
        mockMvc.perform(patch(DataCollectionController.BASE_NAME + "/" + collectionName)
                .content(objectMapper.writeValueAsString(requestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value("1"))
                .andExpect(jsonPath("name").value(newCollectionName))
                .andExpect(jsonPath("createdOn").isNotEmpty())
                .andExpect(jsonPath("type").value("JSON"))
                .andExpect(jsonPath("modifiedOn").isNotEmpty());

        //DELETE by name
        mockMvc.perform(delete(DataCollectionController.BASE_NAME + "/" + newCollectionName))
                .andExpect(status().isOk());
        assertFalse(dataCollectionRepository.existsByName(requestDto.getName()));
    }

    @Test
    public void create_WhenCollectionsWithSameNameAlreadyExists_ThenResponseIsBadRequest() throws Exception {
        final MockMultipartFile file = new MockMultipartFile("attachment", "any-name.json", "text/plain", "file data".getBytes());
        final DataCollectionCreateRequestDTO requestDto = objectMapper.readValue(new File("src/test/resources/test-data/datacollections/data-collection-create-request.json"), DataCollectionCreateRequestDTO.class);
        final URI uri = UriComponentsBuilder.fromUriString(DataCollectionController.BASE_NAME)
                .queryParam("name", requestDto.getName())
                .queryParam("type", requestDto.getType())
                .build().encode().toUri();

        performPostFilesInteraction(uri, file).andExpect(status().isCreated());
        performPostFilesInteraction(uri, file).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldDropFileTypeNotSupportedException() throws Exception {
        final DataCollectionCreateRequestDTO requestDto = objectMapper.readValue(new File("src/test/resources/test-data/datacollections/data-collection-create-request.json"), DataCollectionCreateRequestDTO.class);
        final MockMultipartFile file = new MockMultipartFile("attachment", "any-name.pdf", "text/plain", "file data".getBytes());

        final URI uri = UriComponentsBuilder.fromUriString(DataCollectionController.BASE_NAME)
                .queryParam("name", requestDto.getName())
                .queryParam("type", requestDto.getType())
                .build().encode().toUri();

        performPostFilesInteraction(uri, file).andExpect(status().isBadRequest())
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

    @Test
    public void test_failure_get() throws Exception {
        final String notExistingCollectionName = "unknown-collection";
        mockMvc.perform(get(DataCollectionController.BASE_NAME + "/" + notExistingCollectionName)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
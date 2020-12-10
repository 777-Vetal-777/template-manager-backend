package com.itextpdf.dito.manager.integration.crud;

import com.itextpdf.dito.manager.controller.datacollection.DataCollectionController;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionType;
import com.itextpdf.dito.manager.dto.datacollection.update.DataCollectionUpdateRequestDTO;
import com.itextpdf.dito.manager.integration.AbstractIntegrationTest;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionLogRepository;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionRepository;

import java.net.URI;
import java.util.Base64;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DataCollectionFlowIntegrationTest extends AbstractIntegrationTest {

    private static final String NAME = "test-data-collection";
    private static final String TYPE = "JSON";
    @Autowired
    private DataCollectionRepository dataCollectionRepository;
    @Autowired
    private DataCollectionLogRepository dataCollectionLogRepository;

    @AfterEach
    public void clearDb() {
        dataCollectionRepository.deleteAll();
    }

    @Test
    public void test_success_createAndGet() throws Exception {
        //CREATE
        final MockMultipartFile file = new MockMultipartFile("attachment", "any-name.json", "text/plain",
                "{\"file\":\"data\"}".getBytes());
        final URI uri = UriComponentsBuilder.fromUriString(DataCollectionController.BASE_NAME)
                .queryParam("name", NAME)
                .queryParam("type", TYPE)
                .build().encode().toUri();

        performPostFilesInteraction(uri, file).andExpect(status().isCreated())
                .andExpect(jsonPath("name").value(NAME))
                .andExpect(jsonPath("type").value("JSON"))
                .andExpect(jsonPath("modifiedOn").isNotEmpty())
                .andExpect(jsonPath("description").isEmpty())
                .andExpect(jsonPath("fileName").value(file.getOriginalFilename()))
                .andExpect(jsonPath("createdOn").isNotEmpty());
        assertTrue(dataCollectionRepository.existsByName(NAME));

        //GET by name
        mockMvc.perform(get(DataCollectionController.BASE_NAME + "/" + Base64.getEncoder().encodeToString(NAME.getBytes()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //UPDATE by name
        final String newCollectionName = "new collectionName";

        final String encodedCollectionName = Base64.getEncoder().encodeToString(NAME.getBytes());
        final DataCollectionUpdateRequestDTO collectionUpdateRequestDTO = new DataCollectionUpdateRequestDTO();
        collectionUpdateRequestDTO.setType(DataCollectionType.valueOf(TYPE));
        collectionUpdateRequestDTO.setName(newCollectionName);
        collectionUpdateRequestDTO.setDescription("new description");

        mockMvc.perform(patch(DataCollectionController.BASE_NAME + "/" + encodedCollectionName)
                .content(objectMapper.writeValueAsString(collectionUpdateRequestDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("description").value(collectionUpdateRequestDTO.getDescription()))
                .andExpect(jsonPath("name").value(collectionUpdateRequestDTO.getName()))
                .andExpect(jsonPath("createdOn").isNotEmpty())
                .andExpect(jsonPath("type").value("JSON"))
                .andExpect(jsonPath("modifiedOn").isNotEmpty());
        assertFalse(dataCollectionLogRepository.findAll().isEmpty());

        //DELETE by name
        mockMvc.perform(delete(DataCollectionController.BASE_NAME + "/" + Base64.getEncoder().encodeToString(collectionUpdateRequestDTO.getName().getBytes())))
                .andExpect(status().isOk());
        assertFalse(dataCollectionRepository.existsByName(NAME));
        assertTrue(dataCollectionLogRepository.findAll().isEmpty());

    }

    @Test
    public void create_WhenCollectionsWithSameNameAlreadyExists_ThenResponseIsBadRequest() throws Exception {
        final MockMultipartFile file = new MockMultipartFile("attachment", "any-name.json", "text/plain",
                "{\"file\":\"data\"}".getBytes());
        final URI uri = UriComponentsBuilder.fromUriString(DataCollectionController.BASE_NAME)
                .queryParam("name", Base64.getEncoder().encodeToString(NAME.getBytes()))
                .queryParam("type", TYPE)
                .build().encode().toUri();

        performPostFilesInteraction(uri, file).andExpect(status().isCreated());
        performPostFilesInteraction(uri, file).andExpect(status().isConflict());
    }

    @Test
    public void shouldDropFileTypeNotSupportedException() throws Exception {
        final MockMultipartFile file = new MockMultipartFile("attachment", "any-name.pdf", "text/plain",
                "file data".getBytes());

        final URI uri = UriComponentsBuilder.fromUriString(DataCollectionController.BASE_NAME)
                .queryParam("name", Base64.getEncoder().encodeToString(NAME.getBytes()))
                .queryParam("type", TYPE)
                .build().encode().toUri();

        performPostFilesInteraction(uri, file).andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value("Data collection is not valid."));
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
        mockMvc.perform(get(DataCollectionController.BASE_NAME + "/" + Base64.getEncoder().encodeToString(notExistingCollectionName.getBytes()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
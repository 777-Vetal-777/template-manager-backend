package com.itextpdf.dito.manager.integration.crud;

import com.itextpdf.dito.manager.controller.datacollection.DataCollectionController;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionType;
import com.itextpdf.dito.manager.dto.datacollection.update.DataCollectionUpdateRequestDTO;
import com.itextpdf.dito.manager.integration.AbstractIntegrationTest;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionFileRepository;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionLogRepository;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Base64;

import static com.itextpdf.dito.manager.controller.datacollection.DataCollectionController.DATA_COLLECTION_VERSIONS;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Disabled
public class DataCollectionFlowIntegrationTest extends AbstractIntegrationTest {

    private static final String NAME = "test-data-collection";
    private static final String TYPE = "JSON";
    @Autowired
    private DataCollectionRepository dataCollectionRepository;
    @Autowired
    private DataCollectionLogRepository dataCollectionLogRepository;
    @Autowired
    private DataCollectionFileRepository dataCollectionFileRepository;

    @AfterEach
    public void clearDb() {
        dataCollectionRepository.deleteAll();
    }

    @Test
    public void shouldCreateNewVersionOfDataCollection() throws Exception {
        //CREATE NEW DATA COLLECTION
        final MockMultipartFile file = new MockMultipartFile("attachment", "any-name.json", "text/plain", "{\"file\":\"data\"}".getBytes());
        final MockMultipartFile name = new MockMultipartFile("name", "name", "text/plain", NAME.getBytes());
        final MockMultipartFile type = new MockMultipartFile("type", "type", "text/plain", TYPE.getBytes());
        final URI uri = UriComponentsBuilder.fromUriString(DataCollectionController.BASE_NAME).build().encode().toUri();
        final URI newVersionURI = UriComponentsBuilder.fromUriString(DataCollectionController.BASE_NAME+DATA_COLLECTION_VERSIONS).build().encode().toUri();
        mockMvc.perform(MockMvcRequestBuilders.multipart(uri)
                .file(file)
                .file(name)
                .file(type)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("name").value(NAME))
                .andExpect(jsonPath("version").value(1))
                .andExpect(jsonPath("type").value("JSON"))
                .andExpect(jsonPath("modifiedOn").isNotEmpty())
                .andExpect(jsonPath("description").isEmpty())
                .andExpect(jsonPath("fileName").value(file.getOriginalFilename()))
                .andExpect(jsonPath("createdOn").isNotEmpty());

        assertTrue(dataCollectionRepository.existsByName(NAME));
        //CREATE NEW VERSION
        mockMvc.perform(MockMvcRequestBuilders.multipart(newVersionURI)
                .file(name)
                .file(type)
                .file(file)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("name").value(NAME))
                .andExpect(jsonPath("version").value(2))
                .andExpect(jsonPath("type").value("JSON"))
                .andExpect(jsonPath("modifiedOn").isNotEmpty())
                .andExpect(jsonPath("description").isEmpty())
                .andExpect(jsonPath("fileName").value(file.getOriginalFilename()))
                .andExpect(jsonPath("createdOn").isNotEmpty());
    }

    @Test
    public void test_success_createAndGet() throws Exception {
        //CREATE
        final MockMultipartFile file = new MockMultipartFile("attachment", "any-name.json", "text/plain", "{\"file\":\"data\"}".getBytes());
        final MockMultipartFile name = new MockMultipartFile("name", "name", "text/plain", NAME.getBytes());
        final MockMultipartFile type = new MockMultipartFile("type", "type", "text/plain", TYPE.getBytes());
        final URI uri = UriComponentsBuilder.fromUriString(DataCollectionController.BASE_NAME).build().encode().toUri();

        mockMvc.perform(MockMvcRequestBuilders.multipart(uri)
                .file(file)
                .file(name)
                .file(type)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
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
    public void shouldDropNoSuchDataCollectionTypeWhenTypeUnknown() throws Exception {
        final MockMultipartFile file = new MockMultipartFile("attachment", "any-name.json", "text/plain", "{\"file\":\"data\"}".getBytes());
        final MockMultipartFile name = new MockMultipartFile("name", "name", "text/plain", NAME.getBytes());
        final MockMultipartFile type = new MockMultipartFile("type", "type", "text/plain", "WRONG".getBytes());
        final URI uri = UriComponentsBuilder.fromUriString(DataCollectionController.BASE_NAME).build().encode().toUri();

        mockMvc.perform(MockMvcRequestBuilders.multipart(uri)
                .file(file)
                .file(name)
                .file(type)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void create_WhenCollectionsWithSameNameAlreadyExists_ThenResponseIsBadRequest() throws Exception {
        final MockMultipartFile file = new MockMultipartFile("attachment", "any-name.json", "text/plain", "{\"file\":\"data\"}".getBytes());
        final MockMultipartFile name = new MockMultipartFile("name", "name", "text/plain", NAME.getBytes());
        final MockMultipartFile type = new MockMultipartFile("type", "type", "text/plain", TYPE.getBytes());
        final URI uri = UriComponentsBuilder.fromUriString(DataCollectionController.BASE_NAME).build().encode().toUri();

        mockMvc.perform(MockMvcRequestBuilders.multipart(uri)
                .file(file)
                .file(name)
                .file(type)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated());

        mockMvc.perform(MockMvcRequestBuilders.multipart(uri)
                .file(file)
                .file(name)
                .file(type)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(jsonPath("message").value(String.format("Data collection with id %s already exists.",NAME)));
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
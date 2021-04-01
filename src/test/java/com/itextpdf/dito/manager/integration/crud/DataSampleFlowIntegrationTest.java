package com.itextpdf.dito.manager.integration.crud;

import com.itextpdf.dito.manager.controller.datacollection.DataCollectionController;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionType;
import com.itextpdf.dito.manager.dto.datasample.create.DataSampleCreateRequestDTO;
import com.itextpdf.dito.manager.dto.datasample.update.DataSampleUpdateRequestDTO;
import com.itextpdf.dito.manager.integration.AbstractIntegrationTest;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionRepository;
import com.itextpdf.dito.manager.repository.datasample.DataSampleRepository;
import com.itextpdf.dito.manager.service.datacollection.DataCollectionService;
import com.itextpdf.kernel.xmp.impl.Base64;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

class DataSampleFlowIntegrationTest extends AbstractIntegrationTest {
    private static final String DATACOLLECTION_NAME = "data-collection-test";
    private static final String DATASAMPLE_NAME = "name";
    private static final String DATASAMPLE_NAME2 = "name2";
    private static final String DATASAMPLE_BASE64_ENCODED_NAME = Base64.encode(DATASAMPLE_NAME);
    private static final String DATASAMPLE2_BASE64_ENCODED_NAME = Base64.encode(DATASAMPLE_NAME2);
    private static final String DATACOLLECTION_BASE64_ENCODED_NAME = Base64.encode(DATACOLLECTION_NAME);
    private static final String TYPE = "JSON";

    @Autowired
    private DataCollectionService dataCollectionService;
    @Autowired
    private DataCollectionRepository dataCollectionRepository;
    @Autowired
    private DataSampleRepository dataSampleRepository;

    @BeforeEach
    void init() throws IOException {
        dataCollectionService.create(DATACOLLECTION_NAME, DataCollectionType.valueOf(TYPE), "{\"file\":\"data\"}".getBytes(), "datacollection.json", "admin@email.com");
    }

    @AfterEach
    void clearDb() {
        dataSampleRepository.deleteAll();
        dataCollectionRepository.deleteAll();
    }

    @Test
    void shouldThrowInvalidJson() throws Exception{
        DataSampleCreateRequestDTO request = new DataSampleCreateRequestDTO();
        request.setName("test");
        request.setComment("comment");
        request.setSample("{{}[}");
        request.setFileName("bad.json");

        mockMvc.perform(post(DataCollectionController.BASE_NAME + DataCollectionController.DATA_COLLECTION_DATA_SAMPLES_WITH_PATH_VARIABLE, DATACOLLECTION_BASE64_ENCODED_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void test_create() throws Exception {
        final DataSampleCreateRequestDTO request = objectMapper.readValue(new File("src/test/resources/test-data/datasamples/data-sample-create-request.json"), DataSampleCreateRequestDTO.class);

        //Create dataSample
        mockMvc.perform(post(DataCollectionController.BASE_NAME + DataCollectionController.DATA_COLLECTION_DATA_SAMPLES_WITH_PATH_VARIABLE, DATACOLLECTION_BASE64_ENCODED_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(post(DataCollectionController.BASE_NAME + DataCollectionController.DATA_COLLECTION_DATA_SAMPLES_WITH_PATH_VARIABLE, DATACOLLECTION_BASE64_ENCODED_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());

        //Create new version
        request.setSample("{\"file\":\"data2\"}");
        request.setFileName("fileName2");
        mockMvc.perform(post(DataCollectionController.BASE_NAME + DataCollectionController.DATA_SAMPLE_VERSIONS_WITH_PATH_VARIABLE, DATACOLLECTION_BASE64_ENCODED_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("version").value("2"));

        //Get by name
        mockMvc.perform(get(DataCollectionController.BASE_NAME + DataCollectionController.DATA_COLLECTION_DATA_SAMPLE_WITH_PATH_VARIABLE, DATACOLLECTION_BASE64_ENCODED_NAME, DATASAMPLE_BASE64_ENCODED_NAME)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("fileName").value("fileName2"))
                .andExpect(jsonPath("isDefault").value("true"));
        assertTrue(dataSampleRepository.findByName(DATASAMPLE_NAME).isPresent());

        request.setSample("{}");
        request.setName("data-sample1");
        mockMvc.perform(post(DataCollectionController.BASE_NAME + DataCollectionController.DATA_COLLECTION_DATA_SAMPLES_WITH_PATH_VARIABLE, DATACOLLECTION_BASE64_ENCODED_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        request.setName("data-sample2");
        request.setSample("{123A");
        mockMvc.perform(post(DataCollectionController.BASE_NAME + DataCollectionController.DATA_COLLECTION_DATA_SAMPLES_WITH_PATH_VARIABLE, DATACOLLECTION_BASE64_ENCODED_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

    @Test
    void test_setAsDefault() throws Exception {
        final DataSampleCreateRequestDTO request = objectMapper.readValue(new File("src/test/resources/test-data/datasamples/data-sample-create-request.json"), DataSampleCreateRequestDTO.class);

        //Create dataSample
        mockMvc.perform(post(DataCollectionController.BASE_NAME + DataCollectionController.DATA_COLLECTION_DATA_SAMPLES_WITH_PATH_VARIABLE, DATACOLLECTION_BASE64_ENCODED_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        request.setName(DATASAMPLE_NAME2);

        mockMvc.perform(post(DataCollectionController.BASE_NAME + DataCollectionController.DATA_COLLECTION_DATA_SAMPLES_WITH_PATH_VARIABLE, DATACOLLECTION_BASE64_ENCODED_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        //Set as default
        mockMvc.perform(put(DataCollectionController.BASE_NAME + DataCollectionController.DATA_COLLECTION_DATA_SAMPLE_WITH_PATH_VARIABLE_SET_AS_DEFAULT, DATACOLLECTION_BASE64_ENCODED_NAME,  DATASAMPLE2_BASE64_ENCODED_NAME)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("isDefault").value("true"));

        //get
        mockMvc.perform(get(DataCollectionController.BASE_NAME + DataCollectionController.DATA_COLLECTION_DATA_SAMPLE_WITH_PATH_VARIABLE, DATACOLLECTION_BASE64_ENCODED_NAME, DATASAMPLE_BASE64_ENCODED_NAME)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("isDefault").value("false"));
        
        assertTrue(dataSampleRepository.findByName(DATASAMPLE_NAME).isPresent());
    }

    @Test
    void test_update() throws Exception {
        final DataSampleCreateRequestDTO request = objectMapper.readValue(new File("src/test/resources/test-data/datasamples/data-sample-create-request.json"), DataSampleCreateRequestDTO.class);

        //Create dataSample
        mockMvc.perform(post(DataCollectionController.BASE_NAME + DataCollectionController.DATA_COLLECTION_DATA_SAMPLES_WITH_PATH_VARIABLE, DATACOLLECTION_BASE64_ENCODED_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        final DataSampleUpdateRequestDTO updateRequestDTO = objectMapper.readValue(new File("src/test/resources/test-data/datasamples/data-sample-update-request.json"), DataSampleUpdateRequestDTO.class);
        //Update dataSample
        mockMvc.perform(patch(DataCollectionController.BASE_NAME + DataCollectionController.DATA_COLLECTION_DATA_SAMPLE_WITH_PATH_VARIABLE, DATACOLLECTION_BASE64_ENCODED_NAME, DATASAMPLE_BASE64_ENCODED_NAME)
                .content(objectMapper.writeValueAsString(updateRequestDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("description").value("description2"));
        
        assertTrue(dataSampleRepository.findByName(DATASAMPLE_NAME).isPresent());

    }
    
    @Test
    void test_delete_list() throws Exception {
        final DataSampleCreateRequestDTO request = objectMapper.readValue(new File("src/test/resources/test-data/datasamples/data-sample-create-request.json"), DataSampleCreateRequestDTO.class);

        //Create dataSample
        mockMvc.perform(post(DataCollectionController.BASE_NAME + DataCollectionController.DATA_COLLECTION_DATA_SAMPLES_WITH_PATH_VARIABLE, DATACOLLECTION_BASE64_ENCODED_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
     
        mockMvc.perform(get(DataCollectionController.BASE_NAME + DataCollectionController.DATA_COLLECTION_DATA_SAMPLE_WITH_PATH_VARIABLE, DATACOLLECTION_BASE64_ENCODED_NAME, DATASAMPLE_BASE64_ENCODED_NAME)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("fileName").value("fileName"))
                .andExpect(jsonPath("isDefault").value("true"));
        
        assertTrue(dataSampleRepository.existsByName(DATASAMPLE_NAME));
        
        //delete by name
        List<String> requestToDelete = new ArrayList<>();
        requestToDelete.add(DATASAMPLE_NAME);
        mockMvc.perform(
        		 delete(DataCollectionController.BASE_NAME + DataCollectionController.DATA_COLLECTION_DATA_SAMPLES_WITH_PATH_VARIABLE, DATACOLLECTION_BASE64_ENCODED_NAME)
        		.content(objectMapper.writeValueAsString(requestToDelete))
        		.contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertFalse(dataSampleRepository.existsByName(DATASAMPLE_NAME));

    }
    
    @Test
    void test_delete_all() throws Exception {
        final DataSampleCreateRequestDTO request = objectMapper.readValue(new File("src/test/resources/test-data/datasamples/data-sample-create-request.json"), DataSampleCreateRequestDTO.class);

        //Create dataSample
        mockMvc.perform(post(DataCollectionController.BASE_NAME + DataCollectionController.DATA_COLLECTION_DATA_SAMPLES_WITH_PATH_VARIABLE, DATACOLLECTION_BASE64_ENCODED_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
     
        mockMvc.perform(get(DataCollectionController.BASE_NAME + DataCollectionController.DATA_COLLECTION_DATA_SAMPLE_WITH_PATH_VARIABLE, DATACOLLECTION_BASE64_ENCODED_NAME, DATASAMPLE_BASE64_ENCODED_NAME)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("fileName").value("fileName"))
                .andExpect(jsonPath("isDefault").value("true"));
        
        assertTrue(dataSampleRepository.existsByName(DATASAMPLE_NAME));
        
        //delete all samples of collection
        mockMvc.perform(
        		 delete(DataCollectionController.BASE_NAME + DataCollectionController.DATA_COLLECTION_DATA_SAMPLES_ALL_WITH_PATH_VARIABLE, DATACOLLECTION_BASE64_ENCODED_NAME)
        		.contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertFalse(dataSampleRepository.existsByName(DATASAMPLE_NAME));

    }
}

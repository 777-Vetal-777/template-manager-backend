package com.itextpdf.dito.manager.integration.filtering;

import com.itextpdf.dito.manager.controller.template.TemplateController;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionType;
import com.itextpdf.dito.manager.dto.template.create.TemplateCreateRequestDTO;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.integration.AbstractIntegrationTest;
import com.itextpdf.dito.manager.repository.template.TemplateFileRepository;
import com.itextpdf.dito.manager.repository.template.TemplateRepository;
import com.itextpdf.dito.manager.service.datacollection.DataCollectionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.io.File;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for filtering and search in {@link TemplateEntity} table.
 */
public class TemplateSearchAndFilterIntegrationTest extends AbstractIntegrationTest implements FilterAndSearchTest {
    @Autowired
    private TemplateRepository templateRepository;
    @Autowired
    private TemplateFileRepository templateFileRepository;
    @Autowired
    private DataCollectionService dataCollectionService;

    private TemplateCreateRequestDTO request;

    @BeforeEach
    public void init() throws Exception {
        dataCollectionService.create("data-collection-test", DataCollectionType.JSON, "{\"file\":\"data\"}".getBytes(), "datacollection.json","admin@email.com");

        request = objectMapper.readValue(new File("src/test/resources/test-data/templates/template-create-request-with-data-collection.json"), TemplateCreateRequestDTO.class);
        request.setDataCollectionName("data-collection-test");
        mockMvc.perform(post(TemplateController.BASE_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
    }

    @AfterEach
    public void clearDb() {
        templateRepository.deleteAll();
        templateFileRepository.deleteAll();
        dataCollectionService.delete("data-collection-test", "admin@email.com");
    }

    @Test
    public void getAll_WhenSortedByUnsupportedField_ThenResponseIsBadRequest() throws Exception {
        mockMvc.perform(get(TemplateController.BASE_NAME)
                .param("sort", "unsupportedField")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Override
    @Test
    public void test_filtering() throws Exception {
        mockMvc.perform(get(TemplateController.BASE_NAME)
                .param("name", request.getName()))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is(request.getName())));
        mockMvc.perform(get(TemplateController.BASE_NAME)
                .param("editedOn", "01/01/1970")
                .param("editedOn", "01/01/1980"))
                .andExpect(jsonPath("$.content", hasSize(0)));
        mockMvc.perform(get(TemplateController.BASE_NAME)
                .param("type", "STANDARD"))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is(request.getName())));
        /*mockMvc.perform(get(TemplateController.BASE_NAME)
                .param("dataCollection", request.getDataCollectionName()))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is(request.getName())));*/
    }

    @Override
    @Test
    public void test_searchAndFiltering() throws Exception {
        mockMvc.perform(get(TemplateController.BASE_NAME)
                .param("search", "admin")
                .param("name", "unknown-template"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));

        mockMvc.perform(get(TemplateController.BASE_NAME)
                .param("search", request.getName()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is(request.getName())));
    }

    @Override
    @Test
    public void test_sortWithSearch() throws Exception {
        for (String field : TemplateRepository.SUPPORTED_SORT_FIELDS) {
            mockMvc.perform(get(TemplateController.BASE_NAME)
                    .param("sort", field)
                    .param("search", "template"))
                    .andExpect(status().isOk());
        }
    }

    @Override
    @Test
    public void test_sortWithFiltering() throws Exception {
        for (String field : TemplateRepository.SUPPORTED_SORT_FIELDS) {
            mockMvc.perform(get(TemplateController.BASE_NAME)
                    .param("sort", field))
                    .andExpect(status().isOk());
        }
    }
}

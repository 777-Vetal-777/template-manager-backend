package com.itextpdf.dito.manager.integration.filtering;

import com.itextpdf.dito.manager.component.encoder.Encoder;
import com.itextpdf.dito.manager.controller.template.TemplateController;
import com.itextpdf.dito.manager.integration.AbstractIntegrationTest;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionRepository;
import com.itextpdf.dito.manager.repository.datasample.DataSampleRepository;
import com.itextpdf.dito.manager.repository.resource.ResourceRepository;
import com.itextpdf.dito.manager.repository.template.TemplateRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TemplateDependenciesSearchAndFilterIntegrationTest extends AbstractIntegrationTest implements FilterAndSearchTest {
    @Autowired
    private TemplateRepository templateRepository;
    @Autowired
    private DataSampleRepository dataSampleRepository;
    @Autowired
    private DataCollectionRepository dataCollectionRepository;
    @Autowired
    private ResourceRepository resourceRepository;
    @Autowired
    private Encoder encoder;

    @AfterEach
    void clearDb() {
        templateRepository.deleteAll();
        dataSampleRepository.deleteAll();
        dataCollectionRepository.deleteAll();
        resourceRepository.deleteAll();
    }

    @BeforeEach
    void tearUp() throws Exception {
        final MockMultipartFile ditoFile = new MockMultipartFile("template", "template-with-different-resource-types.dito", "text/plain", readFileBytes("src/test/resources/test-data/templates/import/template-with-different-resource-types.dito"));

        mockMvc.perform(MockMvcRequestBuilders.multipart(TemplateController.BASE_NAME + TemplateController.TEMPLATE_IMPORT_DITO_ENDPOINT)
                .file(ditoFile)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());
    }

    @Test
    @Override
    public void test_filtering() throws Exception {
        final String templateNameEncoded = encoder.encode("Standard");

        final MvcResult mvcResult = mockMvc.perform(get(TemplateController.BASE_NAME + TemplateController.TEMPLATE_DEPENDENCIES_PAGEABLE_ENDPOINT_WITH_PATH_VARIABLE, templateNameEncoded)
                .param("stage", "DEV")
                .queryParam("dependencyType", "IMAGE", "DATA_COLLECTION"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andReturn();

        assertNotNull(mvcResult.getResponse());
    }

    @Test
    @Override
    public void test_searchAndFiltering() throws Exception {
        final String templateNameEncoded = encoder.encode("Standard");

        final MvcResult mvcResult = mockMvc.perform(get(TemplateController.BASE_NAME + TemplateController.TEMPLATE_DEPENDENCIES_PAGEABLE_ENDPOINT_WITH_PATH_VARIABLE, templateNameEncoded)
                .param("stage", "DEV")
                .queryParam("dependencyType", "IMAGE", "DATA_COLLECTION")
                .param("search", "ev"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andReturn();

        assertNotNull(mvcResult.getResponse());
    }

    @Override
    public void test_sortWithSearch() throws Exception {
        final String templateNameEncoded = encoder.encode("Standard");

        for (String field : TemplateRepository.SUPPORTED_DEPENDENCY_SORT_FIELDS) {
            final MvcResult mvcResult = mockMvc.perform(get(TemplateController.BASE_NAME + TemplateController.TEMPLATE_DEPENDENCIES_PAGEABLE_ENDPOINT_WITH_PATH_VARIABLE, templateNameEncoded)
                    .param("stage", "DEV")
                    .queryParam("dependencyType", "IMAGE", "DATA_COLLECTION")
                    .param("search", "ev")
                    .param("sort", field))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(3)))
                    .andReturn();
            assertNotNull(mvcResult.getResponse());
        }
    }

    @Test
    @Override
    public void test_sortWithFiltering() throws Exception {
        final String templateNameEncoded = encoder.encode("Standard");

        for (String field : TemplateRepository.SUPPORTED_DEPENDENCY_SORT_FIELDS) {
            final MvcResult result = mockMvc.perform(get(TemplateController.BASE_NAME + TemplateController.TEMPLATE_DEPENDENCIES_PAGEABLE_ENDPOINT_WITH_PATH_VARIABLE, templateNameEncoded)
                    .param("stage", "DEV")
                    .queryParam("dependencyType", "IMAGE", "DATA_COLLECTION")
                    .param("sort", field))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(3))).andReturn();
            assertNotNull(result.getResponse());
        }
    }
}

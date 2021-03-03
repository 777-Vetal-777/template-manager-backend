package com.itextpdf.dito.manager.integration.filtering;

import com.itextpdf.dito.manager.controller.resource.ResourceController;
import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.integration.AbstractIntegrationTest;
import com.itextpdf.dito.manager.repository.resource.ResourceFileRepository;
import com.itextpdf.dito.manager.repository.resource.ResourceRepository;
import com.itextpdf.dito.manager.service.resource.ResourceService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ResourceFilterAndSearchIntegrationTest extends AbstractIntegrationTest implements FilterAndSearchTest {

    public static final String NAME = "TestName";
    private final String RESOURCE_VERSIONS_URI = ResourceController.BASE_NAME + ResourceController.RESOURCE_VERSION_ENDPOINT_WITH_PATH_VARIABLE;

    @Autowired
    private ResourceRepository resourceRepository;
    @Autowired
    private ResourceService resourceService;

    @BeforeEach
    public void init() throws IOException {
        resourceService.create(NAME, ResourceTypeEnum.IMAGE, Files.readAllBytes(Path.of("src/test/resources/test-data/resources/random.png")), "random.png", "admin@email.com");
    }

    @AfterEach
    public void cleanUp() {
        resourceRepository.deleteAll();
    }

    @Test
    @Override
    public void test_filtering() throws Exception {
        assertEquals(1, resourceRepository.findAll().size());

        mockMvc.perform(get(ResourceController.BASE_NAME)
                .param("name", NAME))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", Matchers.hasSize(1)));

        mockMvc.perform(get(ResourceController.BASE_NAME)
                .param("type", "IMAGE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", Matchers.hasSize(1)));

        mockMvc.perform(get(ResourceController.BASE_NAME)
                .param("comment", "resource-comment"))
                .andExpect(status().isOk());
    }

    @Test
    @Override
    public void test_searchAndFiltering() throws Exception {
        assertEquals(1, resourceRepository.findAll().size());

        mockMvc.perform(get(ResourceController.BASE_NAME)
                .param("type", "IMAGE")
                .param("search", "resource-name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", Matchers.hasSize(0)));

        mockMvc.perform(get(ResourceController.BASE_NAME)
                .param("type", "IMAGE")
                .param("search", NAME))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", Matchers.hasSize(1)));
    }

    @Override
    @Test
    public void test_sortWithSearch() throws Exception {
        assertEquals(1, resourceRepository.findAll().size());

        for (String field : ResourceRepository.SUPPORTED_SORT_FIELDS) {
            mockMvc.perform(get(ResourceController.BASE_NAME)
                    .param("sort", field)
                    .param("search", "not-existing-user"))
                    .andExpect(status().isOk());
        }
    }

    @Override
    @Test
    public void test_sortWithFiltering() throws Exception {
        assertEquals(1, resourceRepository.findAll().size());

        for (String field : ResourceRepository.SUPPORTED_SORT_FIELDS) {
            mockMvc.perform(get(ResourceController.BASE_NAME)
                    .param("sort", field))
                    .andExpect(status().isOk());
        }

    }

    @Test
    public void test_sortVersionsWithFiltering() throws Exception {
        assertEquals(1, resourceRepository.findAll().size());

        for (String field : ResourceFileRepository.SUPPORTED_SORT_FIELDS) {
            mockMvc.perform(get(RESOURCE_VERSIONS_URI, "images", encodeStringToBase64(NAME))
                    .param("type", "IMAGE")
                    .param("sort", field))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", Matchers.hasSize(1)));
        }
    }

    @Test
    public void test_searchVersionsWithFiltering() throws Exception {
        assertEquals(1, resourceRepository.findAll().size());

        mockMvc.perform(get(RESOURCE_VERSIONS_URI, "images", encodeStringToBase64(NAME))
                .param("type", "IMAGE")
                .param("search", "not-existing-user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", Matchers.hasSize(0)));

        mockMvc.perform(get(RESOURCE_VERSIONS_URI, "images", encodeStringToBase64(NAME))
                .param("type", "IMAGE")
                .param("search", "admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", Matchers.hasSize(1)));
    }
}

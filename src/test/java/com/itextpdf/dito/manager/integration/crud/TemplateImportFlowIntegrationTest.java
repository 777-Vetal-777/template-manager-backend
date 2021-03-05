package com.itextpdf.dito.manager.integration.crud;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.dito.manager.controller.template.TemplateController;
import com.itextpdf.dito.manager.dto.template.setting.SettingType;
import com.itextpdf.dito.manager.dto.template.setting.TemplateImportSettingDTO;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TemplateImportFlowIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private TemplateRepository templateRepository;
    @Autowired
    private DataSampleRepository dataSampleRepository;
    @Autowired
    private DataCollectionRepository dataCollectionRepository;
    @Autowired
    private ResourceRepository resourceRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    @BeforeEach
    void clearDb() {
        templateRepository.deleteAll();
        dataSampleRepository.deleteAll();
        dataCollectionRepository.deleteAll();
        resourceRepository.deleteAll();
    }

    @Test
    void shouldImportTemplateWithDataCollectionAndResources() throws Exception {
        final MockMultipartFile ditoFile = new MockMultipartFile("template", "template-with-data-collection.dito", "text/plain", readFileBytes("src/test/resources/test-data/templates/import/template-with-data-collection.dito"));

        mockMvc.perform(MockMvcRequestBuilders.multipart(TemplateController.BASE_NAME + TemplateController.TEMPLATE_IMPORT_ENDPOINT)
                .file(ditoFile)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value("Standard"))
                .andExpect(jsonPath("dataCollection").isNotEmpty())
                .andExpect(jsonPath("version").value(1));

        assertEquals(1, templateRepository.findAll().size());
        assertEquals(1, dataCollectionRepository.findAll().size());
        assertEquals(2, dataSampleRepository.findAll().size());
        assertEquals(2, resourceRepository.findAll().size());
    }

    @Test
    void shouldImportTemplateWithoutDataCollectionAndResources() throws Exception {
        final MockMultipartFile ditoFile = new MockMultipartFile("template", "template-without-data-collection.dito", "text/plain", readFileBytes("src/test/resources/test-data/templates/import/template-without-data-collection.dito"));

        mockMvc.perform(MockMvcRequestBuilders.multipart(TemplateController.BASE_NAME + TemplateController.TEMPLATE_IMPORT_ENDPOINT)
                .file(ditoFile)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value("Standard"))
                .andExpect(jsonPath("dataCollection").isEmpty())
                .andExpect(jsonPath("version").value(1));

        assertEquals(1, templateRepository.findAll().size());
        assertEquals(0, dataCollectionRepository.findAll().size());
        assertEquals(0, dataSampleRepository.findAll().size());
        assertEquals(2, resourceRepository.findAll().size());
    }

    @Test
    void shouldNotRepeatedlyImportTemplate() throws Exception {
        final MockMultipartFile ditoFile = new MockMultipartFile("template", "template-with-data-collection.dito", "text/plain", readFileBytes("src/test/resources/test-data/templates/import/template-with-data-collection.dito"));
        final MockMultipartFile settings = new MockMultipartFile("settings", "settings.json", "application/json", "[ {\"name\": \"datasample.json\",\"type\": \"DATA_COLLECTION\",\"new_version\": false}]".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart(TemplateController.BASE_NAME + TemplateController.TEMPLATE_IMPORT_ENDPOINT)
                .file(ditoFile)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value("Standard"))
                .andExpect(jsonPath("dataCollection").isNotEmpty())
                .andExpect(jsonPath("version").value(1));

        assertEquals(1, templateRepository.findAll().size());
        assertEquals(1, dataCollectionRepository.findAll().size());
        assertEquals(2, dataSampleRepository.findAll().size());
        assertEquals(2, resourceRepository.findAll().size());

        mockMvc.perform(MockMvcRequestBuilders.multipart(TemplateController.BASE_NAME + TemplateController.TEMPLATE_IMPORT_ENDPOINT)
                .file(ditoFile)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("message").value("Template file got duplicates"))
                .andExpect(jsonPath("$.duplicates[*].type", containsInAnyOrder("DATA_COLLECTION", "TEMPLATE")))
                .andExpect(jsonPath("$.duplicates[*].name", hasItems("Standard")));

        mockMvc.perform(MockMvcRequestBuilders.multipart(TemplateController.BASE_NAME + TemplateController.TEMPLATE_IMPORT_ENDPOINT)
                .file(ditoFile)
                .file(settings)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("message").value("Template file got duplicates"))
                .andExpect(jsonPath("$.duplicates[*].type", containsInAnyOrder("TEMPLATE")))
                .andExpect(jsonPath("$.duplicates[*].name", containsInAnyOrder("Standard")));

        assertEquals(1, templateRepository.findAll().size());
        assertEquals(1, dataCollectionRepository.findAll().size());
        assertEquals(2, dataSampleRepository.findAll().size());
        assertEquals(2, resourceRepository.findAll().size());

    }

    @Test
    void shouldCreateNewVersionForTemplateAndDataCollection() throws Exception {
        final MockMultipartFile ditoFile = new MockMultipartFile("template", "template-with-data-collection.dito", "text/plain", readFileBytes("src/test/resources/test-data/templates/import/template-with-data-collection.dito"));
        final MockMultipartFile settings = new MockMultipartFile("settings", "settings.json", "application/json", readFileBytes("src/test/resources/test-data/templates/import/settings-create-new-version.json"));

        mockMvc.perform(MockMvcRequestBuilders.multipart(TemplateController.BASE_NAME + TemplateController.TEMPLATE_IMPORT_ENDPOINT)
                .file(ditoFile)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value("Standard"))
                .andExpect(jsonPath("dataCollection").isNotEmpty())
                .andExpect(jsonPath("version").value(1));

        assertEquals(1, templateRepository.findAll().size());
        assertEquals(1, dataCollectionRepository.findAll().size());
        assertEquals(2, dataSampleRepository.findAll().size());
        assertEquals(2, resourceRepository.findAll().size());

        mockMvc.perform(MockMvcRequestBuilders.multipart(TemplateController.BASE_NAME + TemplateController.TEMPLATE_IMPORT_ENDPOINT)
                .file(ditoFile)
                .file(settings)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value("Standard"))
                .andExpect(jsonPath("dataCollection").isNotEmpty())
                .andExpect(jsonPath("version").value(3));

        assertEquals(1, templateRepository.findAll().size());
        assertEquals(1, dataCollectionRepository.findAll().size());
        assertEquals(4, dataSampleRepository.findAll().size());
        assertEquals(4, resourceRepository.findAll().size());

    }

    @Test
    void shouldCreateNewTemplateAndDataCollection() throws Exception {
        final MockMultipartFile ditoFile = new MockMultipartFile("template", "template-with-data-collection.dito", "text/plain", readFileBytes("src/test/resources/test-data/templates/import/template-with-data-collection.dito"));
        final List<TemplateImportSettingDTO> templateImportSettings = objectMapper.readValue(new File("src/test/resources/test-data/templates/import/settings-create-new-version.json"), objectMapper.getTypeFactory().constructCollectionLikeType(ArrayList.class, TemplateImportSettingDTO.class));
        templateImportSettings.forEach(setting -> setting.setAllowedNewVersion(false));
        final MockMultipartFile settings = new MockMultipartFile("settings", "settings.json", "application/json", objectMapper.writeValueAsBytes(templateImportSettings));

        mockMvc.perform(MockMvcRequestBuilders.multipart(TemplateController.BASE_NAME + TemplateController.TEMPLATE_IMPORT_ENDPOINT)
                .file(ditoFile)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value("Standard"))
                .andExpect(jsonPath("dataCollection").isNotEmpty())
                .andExpect(jsonPath("version").value(1));

        assertEquals(1, templateRepository.findAll().size());
        assertEquals(1, dataCollectionRepository.findAll().size());
        assertEquals(2, dataSampleRepository.findAll().size());
        assertEquals(2, resourceRepository.findAll().size());

        mockMvc.perform(MockMvcRequestBuilders.multipart(TemplateController.BASE_NAME + TemplateController.TEMPLATE_IMPORT_ENDPOINT)
                .file(ditoFile)
                .file(settings)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value("template-with-data-collection-import(1)"))
                .andExpect(jsonPath("dataCollection").value("template-with-data-collection-import(1)"))
                .andExpect(jsonPath("version").value(1));

        assertEquals(2, templateRepository.findAll().size());
        assertEquals(2, dataCollectionRepository.findAll().size());
        assertEquals(4, dataSampleRepository.findAll().size());
        assertEquals(4, resourceRepository.findAll().size());

        templateImportSettings.forEach(setting -> {
            if (SettingType.TEMPLATE.equals(setting.getType()))
                setting.setAllowedNewVersion(true);
        });
        final MockMultipartFile anotherSettings = new MockMultipartFile("settings", "settings.json", "application/json", objectMapper.writeValueAsBytes(templateImportSettings));

        mockMvc.perform(MockMvcRequestBuilders.multipart(TemplateController.BASE_NAME + TemplateController.TEMPLATE_IMPORT_ENDPOINT)
                .file(ditoFile)
                .file(anotherSettings)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value("Standard"))
                .andExpect(jsonPath("dataCollection").isNotEmpty())
                .andExpect(jsonPath("version").value(2));

        assertEquals(2, templateRepository.findAll().size());
        assertEquals(3, dataCollectionRepository.findAll().size());
        assertEquals(6, dataSampleRepository.findAll().size());
        assertEquals(6, resourceRepository.findAll().size());
    }

    @Test
    void shouldImportTemplateWithDifferentTypesOfResources() throws Exception {
        final MockMultipartFile ditoFile = new MockMultipartFile("template", "template-with-different-resource-types.dito", "text/plain", readFileBytes("src/test/resources/test-data/templates/import/template-with-different-resource-types.dito"));

        mockMvc.perform(MockMvcRequestBuilders.multipart(TemplateController.BASE_NAME + TemplateController.TEMPLATE_IMPORT_ENDPOINT)
                .file(ditoFile)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value("Standard"))
                .andExpect(jsonPath("dataCollection").isNotEmpty())
                .andExpect(jsonPath("version").value(1));

        assertEquals(1, templateRepository.findAll().size());
        assertEquals(1, dataCollectionRepository.findAll().size());
        assertEquals(2, dataSampleRepository.findAll().size());
        assertEquals(3, resourceRepository.findAll().size());
    }

}

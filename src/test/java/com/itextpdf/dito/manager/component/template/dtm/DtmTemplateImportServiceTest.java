package com.itextpdf.dito.manager.component.template.dtm;

import com.itextpdf.dito.manager.controller.template.TemplateController;
import com.itextpdf.dito.manager.dto.template.setting.SettingType;
import com.itextpdf.dito.manager.dto.template.setting.TemplateImportSettingDTO;
import com.itextpdf.dito.manager.integration.AbstractIntegrationTest;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionRepository;
import com.itextpdf.dito.manager.repository.datasample.DataSampleRepository;
import com.itextpdf.dito.manager.repository.resource.ResourceRepository;
import com.itextpdf.dito.manager.repository.template.TemplateRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DtmTemplateImportServiceTest extends AbstractIntegrationTest {

    @Autowired
    private TemplateRepository templateRepository;
    @Autowired
    private DataCollectionRepository dataCollectionRepository;
    @Autowired
    private DataSampleRepository dataSampleRepository;
    @Autowired
    private ResourceRepository resourceRepository;

    @AfterEach
    void clearDb() {
        templateRepository.deleteAll();
        dataCollectionRepository.deleteAll();
        resourceRepository.deleteAll();
    }

    @Test
    void shouldImportStandardTemplate() throws Exception {
        assertEquals(0, templateRepository.count());

        final MockMultipartFile ditoFile = new MockMultipartFile("template", "template.dito", "text/plain", readFileBytes("src/test/resources/test-data/templates/import/dtm/template-to-import.dito"));

        mockMvc.perform(MockMvcRequestBuilders.multipart(TemplateController.BASE_NAME + TemplateController.TEMPLATE_IMPORT_ENDPOINT)
                .file(ditoFile)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value("new-template"))
                .andExpect(jsonPath("version").value(1));

        assertEquals(1, templateRepository.count());
        assertEquals(0, dataCollectionRepository.count());
        assertEquals(3, resourceRepository.count());
    }

    @Test
    void shouldImportCompositionTemplate() throws Exception {
        assertEquals(0, templateRepository.count());

        final MockMultipartFile ditoFile = new MockMultipartFile("template", "template.dito", "text/plain", readFileBytes("src/test/resources/test-data/templates/import/dtm/composition-template-to-import.dito"));

        mockMvc.perform(MockMvcRequestBuilders.multipart(TemplateController.BASE_NAME + TemplateController.TEMPLATE_IMPORT_ENDPOINT)
                .file(ditoFile)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("version").value(1));

        assertEquals(5, templateRepository.count());
        assertEquals(1, dataCollectionRepository.count());
        assertEquals(1, dataSampleRepository.count());
        assertEquals(1, resourceRepository.count());
    }

    @Test
    void shouldNotImportTemplateTwice() throws Exception {
        assertEquals(0, templateRepository.count());

        final MockMultipartFile ditoFile = new MockMultipartFile("template", "template.dito", "text/plain", readFileBytes("src/test/resources/test-data/templates/import/dtm/template-to-import.dito"));

        mockMvc.perform(MockMvcRequestBuilders.multipart(TemplateController.BASE_NAME + TemplateController.TEMPLATE_IMPORT_ENDPOINT)
                .file(ditoFile)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value("new-template"))
                .andExpect(jsonPath("version").value(1));

        assertEquals(1, templateRepository.count());
        assertEquals(0, dataCollectionRepository.count());
        assertEquals(3, resourceRepository.count());

        mockMvc.perform(MockMvcRequestBuilders.multipart(TemplateController.BASE_NAME + TemplateController.TEMPLATE_IMPORT_ENDPOINT)
                .file(ditoFile)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("message").value("Template file got duplicates"));
    }

    @Test
    void shouldImportTemplateAsNewVersion() throws Exception {
        assertEquals(0, templateRepository.count());

        final MockMultipartFile ditoFile = new MockMultipartFile("template", "template.dito", "text/plain", readFileBytes("src/test/resources/test-data/templates/import/dtm/template-to-import.dito"));

        mockMvc.perform(MockMvcRequestBuilders.multipart(TemplateController.BASE_NAME + TemplateController.TEMPLATE_IMPORT_ENDPOINT)
                .file(ditoFile)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value("new-template"))
                .andExpect(jsonPath("version").value(1));

        assertEquals(1, templateRepository.findAll().size());
        assertEquals(0, dataCollectionRepository.findAll().size());
        assertEquals(3, resourceRepository.findAll().size());

        final List<TemplateImportSettingDTO> templateImportSettings = objectMapper.readValue(new File("src/test/resources/test-data/templates/import/dtm/settings-create-new-version.json"), objectMapper.getTypeFactory().constructCollectionLikeType(ArrayList.class, TemplateImportSettingDTO.class));
        final MockMultipartFile settings = new MockMultipartFile("settings", "settings.json", "application/json", objectMapper.writeValueAsBytes(templateImportSettings));
        mockMvc.perform(MockMvcRequestBuilders.multipart(TemplateController.BASE_NAME + TemplateController.TEMPLATE_IMPORT_ENDPOINT)
                .file(ditoFile)
                .file(settings)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value("new-template"))
                .andExpect(jsonPath("version").value(4));

        assertEquals(1, templateRepository.findAll().size());
        assertEquals(0, dataCollectionRepository.findAll().size());
        assertEquals(3, resourceRepository.findAll().size());
    }

    @Test
    void shouldImportOnlyTemplateAsNewVersion() throws Exception {
        assertEquals(0, templateRepository.count());

        final MockMultipartFile ditoFile = new MockMultipartFile("template", "template.dito", "text/plain", readFileBytes("src/test/resources/test-data/templates/import/dtm/template-to-import.dito"));

        mockMvc.perform(MockMvcRequestBuilders.multipart(TemplateController.BASE_NAME + TemplateController.TEMPLATE_IMPORT_ENDPOINT)
                .file(ditoFile)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value("new-template"))
                .andExpect(jsonPath("version").value(1));

        assertEquals(1, templateRepository.findAll().size());
        assertEquals(0, dataCollectionRepository.findAll().size());
        assertEquals(3, resourceRepository.findAll().size());

        final List<TemplateImportSettingDTO> templateImportSettings = objectMapper.readValue(new File("src/test/resources/test-data/templates/import/dtm/settings-create-new-version.json"), objectMapper.getTypeFactory().constructCollectionLikeType(ArrayList.class, TemplateImportSettingDTO.class));
        templateImportSettings.forEach(setting -> {
            if (!setting.getType().equals(SettingType.TEMPLATE))
                setting.setAllowedNewVersion(false);
        });
        final MockMultipartFile settings = new MockMultipartFile("settings", "settings.json", "application/json", objectMapper.writeValueAsBytes(templateImportSettings));
        mockMvc.perform(MockMvcRequestBuilders.multipart(TemplateController.BASE_NAME + TemplateController.TEMPLATE_IMPORT_ENDPOINT)
                .file(ditoFile)
                .file(settings)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value("new-template"))
                .andExpect(jsonPath("version").value(2));

        assertEquals(1, templateRepository.findAll().size());
        assertEquals(0, dataCollectionRepository.findAll().size());
        assertEquals(6, resourceRepository.findAll().size());
    }

    @Test
    void shouldImportTemplateAsNewCopies() throws Exception {
        assertEquals(0, templateRepository.count());

        final MockMultipartFile ditoFile = new MockMultipartFile("template", "template.dito", "text/plain", readFileBytes("src/test/resources/test-data/templates/import/dtm/template-to-import.dito"));

        mockMvc.perform(MockMvcRequestBuilders.multipart(TemplateController.BASE_NAME + TemplateController.TEMPLATE_IMPORT_ENDPOINT)
                .file(ditoFile)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value("new-template"))
                .andExpect(jsonPath("version").value(1));

        assertEquals(1, templateRepository.findAll().size());
        assertEquals(0, dataCollectionRepository.findAll().size());
        assertEquals(3, resourceRepository.findAll().size());

        final List<TemplateImportSettingDTO> templateImportSettings = objectMapper.readValue(new File("src/test/resources/test-data/templates/import/dtm/settings-create-new-version.json"), objectMapper.getTypeFactory().constructCollectionLikeType(ArrayList.class, TemplateImportSettingDTO.class));
        templateImportSettings.forEach(setting -> setting.setAllowedNewVersion(false));
        final MockMultipartFile settings = new MockMultipartFile("settings", "settings.json", "application/json", objectMapper.writeValueAsBytes(templateImportSettings));
        mockMvc.perform(MockMvcRequestBuilders.multipart(TemplateController.BASE_NAME + TemplateController.TEMPLATE_IMPORT_ENDPOINT)
                .file(ditoFile)
                .file(settings)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value("template-import(1)"))
                .andExpect(jsonPath("version").value(1));

        assertEquals(2, templateRepository.findAll().size());
        assertEquals(0, dataCollectionRepository.findAll().size());
        assertEquals(6, resourceRepository.findAll().size());
    }

    @Test
    void shouldNotImportCompositionTemplateTwice() throws Exception {
        assertEquals(0, templateRepository.count());

        final MockMultipartFile ditoFile = new MockMultipartFile("template", "template.dito", "text/plain", readFileBytes("src/test/resources/test-data/templates/import/dtm/composition-template-to-import.dito"));

        mockMvc.perform(MockMvcRequestBuilders.multipart(TemplateController.BASE_NAME + TemplateController.TEMPLATE_IMPORT_ENDPOINT)
                .file(ditoFile)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("version").value(1));

        assertEquals(5, templateRepository.count());
        assertEquals(1, dataCollectionRepository.count());
        assertEquals(1, dataSampleRepository.count());
        assertEquals(1, resourceRepository.count());

        mockMvc.perform(MockMvcRequestBuilders.multipart(TemplateController.BASE_NAME + TemplateController.TEMPLATE_IMPORT_ENDPOINT)
                .file(ditoFile)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("message").value("Template file got duplicates"));
    }

    @Test
    void shouldImportCompositionTemplateAsNewVersion() throws Exception {
        assertEquals(0, templateRepository.count());

        final MockMultipartFile ditoFile = new MockMultipartFile("template", "template.dito", "text/plain", readFileBytes("src/test/resources/test-data/templates/import/dtm/composition-template-to-import.dito"));

        mockMvc.perform(MockMvcRequestBuilders.multipart(TemplateController.BASE_NAME + TemplateController.TEMPLATE_IMPORT_ENDPOINT)
                .file(ditoFile)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("version").value(1));

        assertEquals(5, templateRepository.count());
        assertEquals(1, dataCollectionRepository.count());
        assertEquals(1, dataSampleRepository.count());
        assertEquals(1, resourceRepository.count());

        final List<TemplateImportSettingDTO> templateImportSettings = objectMapper.readValue(new File("src/test/resources/test-data/templates/import/dtm/settings-create-new-version.json"), objectMapper.getTypeFactory().constructCollectionLikeType(ArrayList.class, TemplateImportSettingDTO.class));
        final MockMultipartFile settings = new MockMultipartFile("settings", "settings.json", "application/json", objectMapper.writeValueAsBytes(templateImportSettings));
        mockMvc.perform(MockMvcRequestBuilders.multipart(TemplateController.BASE_NAME + TemplateController.TEMPLATE_IMPORT_ENDPOINT)
                .file(ditoFile)
                .file(settings)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());

        assertEquals(5, templateRepository.count());
        assertEquals(1, dataCollectionRepository.count());
        assertEquals(1, dataSampleRepository.count());
        assertEquals(1, resourceRepository.count());
    }

    @Test
    void shouldImportCompositionTemplateAsNewCopies() throws Exception {
        assertEquals(0, templateRepository.count());

        final MockMultipartFile ditoFile = new MockMultipartFile("template", "template.dito", "text/plain", readFileBytes("src/test/resources/test-data/templates/import/dtm/composition-template-to-import.dito"));

        mockMvc.perform(MockMvcRequestBuilders.multipart(TemplateController.BASE_NAME + TemplateController.TEMPLATE_IMPORT_ENDPOINT)
                .file(ditoFile)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("version").value(1));

        assertEquals(5, templateRepository.count());
        assertEquals(1, dataCollectionRepository.count());
        assertEquals(1, dataSampleRepository.count());
        assertEquals(1, resourceRepository.count());

        final List<TemplateImportSettingDTO> templateImportSettings = objectMapper.readValue(new File("src/test/resources/test-data/templates/import/dtm/settings-create-new-version.json"), objectMapper.getTypeFactory().constructCollectionLikeType(ArrayList.class, TemplateImportSettingDTO.class));
        templateImportSettings.forEach(setting -> setting.setAllowedNewVersion(false));
        final MockMultipartFile settings = new MockMultipartFile("settings", "settings.json", "application/json", objectMapper.writeValueAsBytes(templateImportSettings));
        mockMvc.perform(MockMvcRequestBuilders.multipart(TemplateController.BASE_NAME + TemplateController.TEMPLATE_IMPORT_ENDPOINT)
                .file(ditoFile)
                .file(settings)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());

        assertEquals(10, templateRepository.count());
        assertEquals(2, dataCollectionRepository.count());
        assertEquals(2, dataSampleRepository.count());
        assertEquals(2, resourceRepository.count());
    }
}

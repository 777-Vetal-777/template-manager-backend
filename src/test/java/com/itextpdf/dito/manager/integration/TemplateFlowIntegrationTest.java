package com.itextpdf.dito.manager.integration;

import com.itextpdf.dito.manager.controller.template.TemplateController;
import com.itextpdf.dito.manager.controller.template.TemplateTypeController;
import com.itextpdf.dito.manager.dto.template.create.TemplateCreateRequestDTO;
import com.itextpdf.dito.manager.repository.template.TemplateFileRepository;
import com.itextpdf.dito.manager.repository.template.TemplateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class TemplateFlowIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private TemplateRepository templateRepository;
    @Autowired
    private TemplateFileRepository templateFileRepository;

    @BeforeEach
    public void clearDb() {
        templateRepository.deleteAll();
        templateFileRepository.deleteAll();
    }

    @Test
    public void testCreateTemplate() throws Exception {
        TemplateCreateRequestDTO request = objectMapper.readValue(new File("src/test/resources/test-data/templates/template-create-request.json"), TemplateCreateRequestDTO.class);
        mockMvc.perform(post(TemplateController.BASE_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        assertTrue(templateRepository.findByName(request.getName()).isPresent());
    }

    @Test
    public void testGetAll() throws Exception {
        mockMvc.perform(get(TemplateController.BASE_NAME)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetAllTemplateTypes() throws Exception {
        mockMvc.perform(get(TemplateTypeController.BASE_NAME)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}

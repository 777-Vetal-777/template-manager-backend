package com.itextpdf.dito.manager.component.uuid;

import com.itextpdf.dito.manager.component.encoder.Encoder;
import com.itextpdf.dito.manager.controller.template.TemplateController;
import com.itextpdf.dito.manager.dto.template.create.TemplateCreateRequestDTO;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.integration.AbstractIntegrationTest;
import com.itextpdf.dito.manager.repository.template.TemplateRepository;
import com.itextpdf.dito.manager.service.template.TemplateService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TemplateUuidModifierTest extends AbstractIntegrationTest {

    private static final String STANDARD_TEMPLATE_NAME = "some-template";
    private static final String COMPOSITION_TEMPLATE_NAME = "composite-template";

    @Autowired
    @Qualifier("templateUuidModifier")
    private UuidModifier uuidModifier;
    @Autowired
    private TemplateRepository templateRepository;
    @Autowired
    private TemplateService templateService;
    @Autowired
    private Encoder encoder;

    @BeforeEach
    void onInit() throws Exception {
        performCreateTemplateRequest("src/test/resources/test-data/templates/template-create-request.json");
        performCreateTemplateRequest("src/test/resources/test-data/templates/template-create-request-header.json");
        performCreateTemplateRequest("src/test/resources/test-data/templates/template-create-request-footer2.json");

        final TemplateCreateRequestDTO request = objectMapper.readValue(new File("src/test/resources/test-data/templates/template-create-request-composition.json"), TemplateCreateRequestDTO.class);
        request.getTemplateParts().removeIf(part -> "some-footer-template".equals(part.getName()) || "some-template-with-data-collection".equals(part.getName()));
        mockMvc.perform(post(TemplateController.BASE_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        final Consumer<TemplateEntity> templateEntityConsumer = templateEntity -> {
            templateEntity.setUuid(null);
            templateRepository.save(templateEntity);
        };

        templateRepository.findByName(COMPOSITION_TEMPLATE_NAME).ifPresent(templateEntity -> {
            templateRepository.findByName(STANDARD_TEMPLATE_NAME).ifPresent(standardEntity -> {
                templateEntity.getFiles().forEach(templateFileEntity -> {
                    templateFileEntity.setData(new String(templateFileEntity.getData(), StandardCharsets.UTF_8).replace(standardEntity.getUuid(), encoder.encode(standardEntity.getName())).getBytes(StandardCharsets.UTF_8));
                });
            });
            templateEntityConsumer.accept(templateEntity);
        });
        templateRepository.findByName(STANDARD_TEMPLATE_NAME).ifPresent(templateEntityConsumer);
    }

    @AfterEach
    void clearDb() {
        templateRepository.deleteAll();
    }

    private void performCreateTemplateRequest(final String pathname) throws Exception {
        final TemplateCreateRequestDTO request = objectMapper.readValue(new File(pathname), TemplateCreateRequestDTO.class);

        templateService.create(request.getName(), request.getType(), request.getDataCollectionName(), "admin@email.com", null);
    }

    @Test
    void shouldUpdateLinksInTemplate() throws Exception {
        final TemplateEntity standardEntity = templateRepository.findByName(STANDARD_TEMPLATE_NAME).orElseThrow();
        TemplateEntity compositionEntity = templateRepository.findByName(COMPOSITION_TEMPLATE_NAME).orElseThrow();

        assertNull(standardEntity.getUuid());
        assertNull(compositionEntity.getUuid());
        compositionEntity.getFiles().forEach(
                templateFileEntity -> {
                    assertTrue(new String(templateFileEntity.getData(), StandardCharsets.UTF_8).contains(encoder.encode(standardEntity.getName())));
                });

        uuidModifier.updateEmptyUuid();

        final TemplateEntity updatedEntity = templateRepository.findByName(STANDARD_TEMPLATE_NAME).orElseThrow();
        compositionEntity = templateRepository.findByName(COMPOSITION_TEMPLATE_NAME).orElseThrow();

        assertNotNull(updatedEntity.getUuid());
        assertNotNull(compositionEntity.getUuid());

        compositionEntity.getFiles().forEach(
                templateFileEntity -> {
                    assertTrue(new String(templateFileEntity.getData(), StandardCharsets.UTF_8).contains(updatedEntity.getUuid()));
                    assertFalse(new String(templateFileEntity.getData(), StandardCharsets.UTF_8).contains(encoder.encode(updatedEntity.getName())));
                });
    }
}

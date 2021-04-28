package com.itextpdf.dito.manager.component.uuid;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.itextpdf.dito.manager.component.encoder.Encoder;
import com.itextpdf.dito.manager.controller.resource.ResourceController;
import com.itextpdf.dito.manager.dto.resource.ResourceIdDTO;
import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.dto.template.create.TemplateCreateRequestDTO;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceFileEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.integration.AbstractIntegrationTest;
import com.itextpdf.dito.manager.integration.editor.service.template.TemplateManagementService;
import com.itextpdf.dito.manager.repository.resource.ResourceRepository;
import com.itextpdf.dito.manager.repository.template.TemplateRepository;
import com.itextpdf.dito.manager.service.resource.ResourceService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ResourceUuidModifierTest extends AbstractIntegrationTest {

    private static final String IMAGE_NAME = "test-image";
    private static final String IMAGE_TYPE = "IMAGE";
    private static final String IMAGE_FILE_NAME = "any-name.png";
    private static final MockMultipartFile IMAGE_FILE_PART = new MockMultipartFile("resource", IMAGE_FILE_NAME, "text/plain", readFileBytes("src/test/resources/test-data/resources/random.png"));
    private static final MockMultipartFile IMAGE_TYPE_PART = new MockMultipartFile("type", "type", "text/plain", IMAGE_TYPE.getBytes());
    private static final MockMultipartFile NAME_PART = new MockMultipartFile("name", "name", "text/plain", IMAGE_NAME.getBytes());

    private static final String STYLESHEET_NAME = "test-stylesheet";
    private static final String STYLESHEET_TYPE = ResourceTypeEnum.STYLESHEET.toString();
    private static final MockMultipartFile STYLESHEET_TYPE_PART = new MockMultipartFile("type", "type", "text/plain", STYLESHEET_TYPE.getBytes());
    private static final MockMultipartFile STYLESHEET_NAME_PART = new MockMultipartFile("name", "name", "text/plain", STYLESHEET_NAME.getBytes());
    private static final MockMultipartFile STYLESHEET_FILE_PART = new MockMultipartFile("resource", "any_name.css", "text/plain", ".h1 {\n \tfont-style: Helvetica\n }".getBytes());

    private static final String FONT_NAME = "test-font";
    private static final String FONT_TYPE = "FONT";
    private static final String REGULAR_FILE_NAME = "regular.ttf";
    private static final String BOLD_FILE_NAME = "bold.ttf";
    private static final String ITALIC_FILE_NAME = "italic.ttf";
    private static final String BOLD_ITALIC_FILE_NAME = "bold_italic.ttf";

    private static final MockMultipartFile FONT_NAME_PART = new MockMultipartFile("name", FONT_NAME, "text/plain", FONT_NAME.getBytes());
    private static final MockMultipartFile FONT_TYPE_PART = new MockMultipartFile("type", FONT_TYPE, "text/plain", FONT_TYPE.getBytes());
    private static final MockMultipartFile REGULAR_FONT_FILE_PART = new MockMultipartFile("regular", REGULAR_FILE_NAME, "text/plain", readFileBytes("src/test/resources/test-data/resources/random-regular.ttf"));
    private static final MockMultipartFile BOLD_FONT_FILE_PART = new MockMultipartFile("bold", BOLD_FILE_NAME, "text/plain", readFileBytes("src/test/resources/test-data/resources/random-regular.ttf"));
    private static final MockMultipartFile ITALIC_FONT_FILE_PART = new MockMultipartFile("italic", ITALIC_FILE_NAME, "text/plain", readFileBytes("src/test/resources/test-data/resources/random-regular.ttf"));
    private static final MockMultipartFile BOLD_ITALIC_FILE_PART = new MockMultipartFile("bold_italic", BOLD_ITALIC_FILE_NAME, "text/plain", readFileBytes("src/test/resources/test-data/resources/random-regular.ttf"));

    @Autowired
    @Qualifier("resourceUuidModifier")
    private UuidModifier uuidModifier;
    @Autowired
    private Encoder encoder;
    @Autowired
    private ResourceRepository resourceRepository;
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private TemplateManagementService templateManagementService;
    @Autowired
    private TemplateRepository templateRepository;

    @BeforeEach
    void init() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.multipart(ResourceController.BASE_NAME + ResourceController.FONTS_ENDPOINT)
                .file(FONT_NAME_PART)
                .file(FONT_TYPE_PART)
                .file(REGULAR_FONT_FILE_PART)
                .file(BOLD_FONT_FILE_PART)
                .file(ITALIC_FONT_FILE_PART)
                .file(BOLD_ITALIC_FILE_PART)
                .file(IMAGE_TYPE_PART)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.multipart(ResourceController.BASE_NAME)
                .file(IMAGE_FILE_PART)
                .file(NAME_PART)
                .file(IMAGE_TYPE_PART)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated());

        mockMvc.perform(MockMvcRequestBuilders.multipart(ResourceController.BASE_NAME)
                .file(STYLESHEET_FILE_PART)
                .file(STYLESHEET_NAME_PART)
                .file(STYLESHEET_TYPE_PART)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated());

        final ResourceEntity imageEntity = resourceRepository.findByName(IMAGE_NAME);
        final ResourceEntity stylesheetEntity = resourceRepository.findByName(STYLESHEET_NAME);
        final ResourceEntity fontEntity = resourceRepository.findByName(FONT_NAME);

        final String templateData = Files.readString(Path.of("src/test/resources/test-data/templates/template-create-request-with-resources.html"))
                .replace("imageuuid", imageEntity.getUuid())
                .replace("cssuuid", stylesheetEntity.getUuid())
                .replace("fontuuid", fontEntity.getUuid())
                .replace("fontnormaluuid", fontEntity.getLatestFile().get(0).getUuid())
                .replace("fontitalicuuid", fontEntity.getLatestFile().get(1).getUuid());

        final TemplateCreateRequestDTO request = objectMapper.readValue(new File("src/test/resources/test-data/templates/template-create-request.json"), TemplateCreateRequestDTO.class);

        templateManagementService.create(request.getName(), templateData.getBytes(StandardCharsets.UTF_8), request.getDataCollectionName(), "admin@email.com");

        final String updatedData =
        templateData.replace(imageEntity.getUuid(), encode(imageEntity))
                .replace(stylesheetEntity.getUuid(), encode(stylesheetEntity))
                .replace(fontEntity.getUuid(), encode(fontEntity))
                .replace(fontEntity.getLatestFile().get(1).getUuid(), encode(fontEntity, fontEntity.getLatestFile().get(1)))
                .replace(fontEntity.getLatestFile().get(0).getUuid(), encode(fontEntity, fontEntity.getLatestFile().get(0)));

        templateRepository.findByName(request.getName()).ifPresent(templateEntity -> {
            templateEntity.getFiles().forEach(templateFileEntity -> templateFileEntity.setData(updatedData.getBytes(StandardCharsets.UTF_8)));
            templateRepository.save(templateEntity);
        });

        final Consumer<ResourceEntity> resourceEntityConsumer = resourceEntity -> {
            resourceEntity.setUuid(null);
            resourceRepository.save(resourceEntity);
        };

        resourceRepository.findByNameAndType(IMAGE_NAME, ResourceTypeEnum.IMAGE).ifPresent(resourceEntityConsumer);
        resourceRepository.findByNameAndType(STYLESHEET_NAME, ResourceTypeEnum.STYLESHEET).ifPresent(resourceEntityConsumer);
        resourceRepository.findByNameAndType(FONT_NAME, ResourceTypeEnum.FONT).ifPresent(resourceEntityConsumer);
    }

    @AfterEach
    void clearDB() {
        templateRepository.deleteAll();
        resourceRepository.deleteAll();
    }

    @Test
    void shouldUpdateResourceUuid() {
        final var imageEntity = resourceRepository.findByNameAndType(IMAGE_NAME, ResourceTypeEnum.IMAGE).orElseThrow();
        final var stylesheetEntity = resourceRepository.findByNameAndType(STYLESHEET_NAME, ResourceTypeEnum.STYLESHEET).orElseThrow();
        final var fontEntity = resourceRepository.findByNameAndType(FONT_NAME, ResourceTypeEnum.FONT).orElseThrow();
        assertNull(imageEntity.getUuid());
        assertNull(stylesheetEntity.getUuid());
        assertNull(fontEntity.getUuid());

        uuidModifier.updateEmptyUuid();

        final var updatedImageEntity = resourceRepository.findByNameAndType(IMAGE_NAME, ResourceTypeEnum.IMAGE).orElseThrow();
        final var updatedStylesheetEntity = resourceRepository.findByNameAndType(STYLESHEET_NAME, ResourceTypeEnum.STYLESHEET).orElseThrow();
        final var updatedFontEntity = resourceRepository.findByNameAndType(FONT_NAME, ResourceTypeEnum.FONT).orElseThrow();

        assertNotNull(updatedImageEntity.getUuid());
        assertNotNull(updatedStylesheetEntity.getUuid());
        assertNotNull(updatedFontEntity.getUuid());

        final var templateEntity = templateRepository.findByName("some-template").orElseThrow();

        assertEquals(1, templateEntity.getFiles().size());

        templateEntity.getFiles().forEach(
                templateFileEntity -> {
                    final var templateData = new String(templateFileEntity.getData(), StandardCharsets.UTF_8);
                    assertTrue(templateData.contains(updatedImageEntity.getUuid()));
                    assertTrue(templateData.contains(updatedStylesheetEntity.getUuid()));
                    assertTrue(templateData.contains(updatedFontEntity.getUuid()));
                    assertTrue(templateData.contains(updatedFontEntity.getLatestFile().get(0).getUuid()));
                    assertTrue(templateData.contains(updatedFontEntity.getLatestFile().get(1).getUuid()));
                });

    }

    private String encode(final String name, final ResourceTypeEnum type, final String fontFace) throws JsonProcessingException {
        final ResourceIdDTO dto = new ResourceIdDTO();
        dto.setName(name);
        dto.setType(type);
        dto.setSubName(fontFace);
        return encoder.encode(objectMapper.writeValueAsString(dto));
    }

    private String encode(final ResourceEntity entity) throws JsonProcessingException {
        return encode(entity.getName(), entity.getType(), null);
    }

    private String encode(final ResourceEntity entity, final ResourceFileEntity resourceFileEntity) throws JsonProcessingException {
        return encode(entity.getName(), entity.getType(), resourceFileEntity.getFontName());
    }
}

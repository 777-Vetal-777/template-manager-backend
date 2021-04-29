package com.itextpdf.dito.manager.component.template.dtm;

import com.itextpdf.dito.editor.server.common.core.descriptor.TemplateAddDescriptor;
import com.itextpdf.dito.editor.server.common.core.descriptor.TemplateFragmentType;
import com.itextpdf.dito.manager.component.template.dtm.model.DtmModelExtractor;
import com.itextpdf.dito.manager.controller.datacollection.DataCollectionController;
import com.itextpdf.dito.manager.controller.resource.ResourceController;
import com.itextpdf.dito.manager.controller.template.TemplateController;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionType;
import com.itextpdf.dito.manager.dto.datasample.create.DataSampleCreateRequestDTO;
import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.dto.template.create.TemplateCreateRequestDTO;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.integration.AbstractIntegrationTest;
import com.itextpdf.dito.manager.integration.editor.controller.template.TemplateManagementController;
import com.itextpdf.dito.manager.integration.editor.service.template.TemplateManagementService;
import com.itextpdf.dito.manager.repository.resource.ResourceRepository;
import com.itextpdf.dito.manager.repository.template.TemplateRepository;
import com.itextpdf.dito.manager.service.datacollection.DataCollectionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

abstract class AbstractDtmExtractorTest extends AbstractIntegrationTest {
    private static final String WORKSPACE_ID = "c29tZS10ZW1wbGF0ZQ==";
    @Autowired
    private TemplateRepository templateRepository;
    @Autowired
    private DtmModelExtractor extractor;
    @Autowired
    private DataCollectionService dataCollectionService;
    @Autowired
    private ResourceRepository resourceRepository;
    @Autowired
    private TemplateManagementService templateManagementService;

    @BeforeEach
    void setUp() throws Exception {
        dataCollectionService.create("new-data-collection", DataCollectionType.JSON, "{\"file\":\"data\"}".getBytes(), "datacollection.json", "admin@email.com");
        final String encodedDataCollectionName = encodeStringToBase64("new-data-collection");
        final DataSampleCreateRequestDTO dataSampleRequest = objectMapper.readValue(new File("src/test/resources/test-data/datasamples/data-sample-create-request.json"), DataSampleCreateRequestDTO.class);

        //Create dataSample
        mockMvc.perform(post(DataCollectionController.BASE_NAME + DataCollectionController.DATA_COLLECTION_DATA_SAMPLES_WITH_PATH_VARIABLE, encodedDataCollectionName)
                .content(objectMapper.writeValueAsString(dataSampleRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        //Create new version
        dataSampleRequest.setSample("{\"file\":\"data2\"}");
        dataSampleRequest.setFileName("fileName2");
        mockMvc.perform(post(DataCollectionController.BASE_NAME + DataCollectionController.DATA_SAMPLE_VERSIONS_WITH_PATH_VARIABLE, encodedDataCollectionName)
                .content(objectMapper.writeValueAsString(dataSampleRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("version").value("2"));

        performCreateTemplateRequest("src/test/resources/test-data/templates/template-create-request.json");
        performCreateTemplateRequest("src/test/resources/test-data/templates/template-create-request-header.json");
        performCreateTemplateRequest("src/test/resources/test-data/templates/template-create-request-footer.json");
        performCreateTemplateRequest("src/test/resources/test-data/templates/template-create-request-footer2.json");
        performCreateTemplateRequest("src/test/resources/test-data/templates/template-create-request-with-data-collection2.json");

        final TemplateCreateRequestDTO request = objectMapper.readValue(new File("src/test/resources/test-data/templates/template-create-request-composition.json"), TemplateCreateRequestDTO.class);
        request.getTemplateParts().removeIf(part -> "some-footer-template".equals(part.getName()) || "some-template-with-data-collection".equals(part.getName()));
        mockMvc.perform(post(TemplateController.BASE_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        //Create resource
        final String imageName = "pictureName.png";
        mockMvc.perform(MockMvcRequestBuilders.multipart(ResourceController.BASE_NAME)
                .file(new MockMultipartFile("resource", imageName, "text/plain", readFileBytes("src/test/resources/test-data/resources/random.png")))
                .file(new MockMultipartFile("type", "type", "text/plain", ResourceTypeEnum.IMAGE.toString().getBytes()))
                .file(new MockMultipartFile("name", "name", "text/plain", imageName.getBytes()))
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated());
        final ResourceEntity createdResource = resourceRepository.findByNameAndType(imageName, ResourceTypeEnum.IMAGE).orElseThrow();
        assertNotNull(createdResource.getUuid());
        final byte[] uploadData = Files.readString(Path.of("src/test/resources/test-data/templates/template-create-request-with-picture.html")).replace("replaceThis", createdResource.getUuid()).getBytes(StandardCharsets.UTF_8);

        // CREATE
        final String templateName = "template-example";
        final TemplateAddDescriptor templateAddDescriptor = new TemplateAddDescriptor(templateName, TemplateFragmentType.STANDARD);
        final MockMultipartFile descriptor = new MockMultipartFile("descriptor", "descriptor", "application/json", objectMapper.writeValueAsString(templateAddDescriptor).getBytes());
        final MockMultipartFile data = new MockMultipartFile("data", "data", "application/json", uploadData);
        mockMvc.perform(MockMvcRequestBuilders.multipart(TemplateManagementController.CREATE_TEMPLATE_URL, WORKSPACE_ID)
                .file(descriptor)
                .file(data)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //Create resource
        final String b02ImageName = "b02.jpg";
        mockMvc.perform(MockMvcRequestBuilders.multipart(ResourceController.BASE_NAME)
                .file(new MockMultipartFile("resource", b02ImageName, "text/plain", readFileBytes("src/test/resources/test-data/resources/random.png")))
                .file(new MockMultipartFile("type", "type", "text/plain", ResourceTypeEnum.IMAGE.toString().getBytes()))
                .file(new MockMultipartFile("name", "name", "text/plain", b02ImageName.getBytes()))
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated());

        final TemplateEntity partTemplateEntity = templateRepository.findByName("some-template").orElseThrow();

        final ResourceEntity resourceEntity = resourceRepository.findByNameAndType(b02ImageName, ResourceTypeEnum.IMAGE).orElseThrow();
        assertNotNull(resourceEntity.getUuid());
        final byte[] updatedTemplateBody = Files.readString(Path.of("src/test/resources/test-data/templates/template-update-request-data.html"))
                .replace("replaceThis", resourceEntity.getUuid())
                .getBytes(StandardCharsets.UTF_8);

        //Create new version
        final MockMultipartFile file = new MockMultipartFile("data", "template.html", "text/plain", updatedTemplateBody);
        mockMvc.perform(MockMvcRequestBuilders.multipart(TemplateManagementController.TEMPLATE_URL, partTemplateEntity.getUuid())
                .file(file))
                .andExpect(status().isOk());

        //Create composite template
        final TemplateCreateRequestDTO compositionTemplateRequest = objectMapper.readValue(new File("src/test/resources/test-data/templates/template-create-request-composition-for-export.json"), TemplateCreateRequestDTO.class);
        compositionTemplateRequest.setName("composition_template2");
        mockMvc.perform(post(TemplateController.BASE_NAME)
                .content(objectMapper.writeValueAsString(compositionTemplateRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        final String fontName = "test-font";
        final String fontType = ResourceTypeEnum.FONT.toString();
        final String regularFileName = "regular.ttf";
        final String boldFileName = "bold.ttf";
        final String italicFileName = "italic.ttf";
        final String bodItalicFileName = "bold_italic.ttf";
        mockMvc.perform(MockMvcRequestBuilders.multipart(ResourceController.BASE_NAME + ResourceController.FONTS_ENDPOINT)
                .file(new MockMultipartFile("name", fontName, "text/plain", fontName.getBytes()))
                .file(new MockMultipartFile("type", fontType, "text/plain", fontType.getBytes()))
                .file(new MockMultipartFile("regular", regularFileName, "text/plain", readFileBytes("src/test/resources/test-data/resources/random-regular.ttf")))
                .file(new MockMultipartFile("bold", boldFileName, "text/plain", readFileBytes("src/test/resources/test-data/resources/random-regular.ttf")))
                .file(new MockMultipartFile("italic", italicFileName, "text/plain", readFileBytes("src/test/resources/test-data/resources/random-regular.ttf")))
                .file(new MockMultipartFile("bold_italic", bodItalicFileName, "text/plain", readFileBytes("src/test/resources/test-data/resources/random-regular.ttf")))
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());

        final String newImageName = "test-image";
        final String imageFileName = "any-name.png";
        mockMvc.perform(MockMvcRequestBuilders.multipart(ResourceController.BASE_NAME)
                .file(new MockMultipartFile("resource", imageFileName, "text/plain", readFileBytes("src/test/resources/test-data/resources/random.png")))
                .file(new MockMultipartFile("name", "name", "text/plain", newImageName.getBytes()))
                .file(new MockMultipartFile("type", "type", "text/plain", ResourceTypeEnum.IMAGE.toString().getBytes()))
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated());

        final String stylesheetName = "test-stylesheet";
        mockMvc.perform(MockMvcRequestBuilders.multipart(ResourceController.BASE_NAME)
                .file(new MockMultipartFile("type", "type", "text/plain", ResourceTypeEnum.STYLESHEET.toString().getBytes()))
                .file(new MockMultipartFile("name", "name", "text/plain", stylesheetName.getBytes()))
                .file(new MockMultipartFile("resource", "any_name.css", "text/plain", ".h1 {\n \tfont-style: Helvetica\n }".getBytes()))
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated());

        final ResourceEntity imageEntity = resourceRepository.findByName(newImageName);
        final ResourceEntity stylesheetEntity = resourceRepository.findByName(stylesheetName);
        final ResourceEntity fontEntity = resourceRepository.findByName(fontName);

        final String templateData = Files.readString(Path.of("src/test/resources/test-data/templates/template-create-request-with-resources.html"))
                .replace("imageuuid", imageEntity.getUuid())
                .replace("cssuuid", stylesheetEntity.getUuid())
                .replace("fontuuid", fontEntity.getUuid())
                .replace("fontnormaluuid", fontEntity.getLatestFile().get(0).getUuid())
                .replace("fontitalicuuid", fontEntity.getLatestFile().get(1).getUuid());

        final TemplateCreateRequestDTO templateCreateRequest = objectMapper.readValue(new File("src/test/resources/test-data/templates/template-create-request.json"), TemplateCreateRequestDTO.class);
        templateCreateRequest.setName("new-template");
        templateManagementService.create(templateCreateRequest.getName(), templateData.getBytes(StandardCharsets.UTF_8), templateCreateRequest.getDataCollectionName(), "admin@email.com");
    }

    @AfterEach
    void clearDb() {
        templateRepository.deleteAll();
        dataCollectionService.delete("new-data-collection", "admin@email.com");
        resourceRepository.deleteAll();
    }

    private void performCreateTemplateRequest(final String pathname) throws Exception {
        final TemplateCreateRequestDTO request = objectMapper.readValue(new File(pathname), TemplateCreateRequestDTO.class);

        mockMvc.perform(post(TemplateController.BASE_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }
}

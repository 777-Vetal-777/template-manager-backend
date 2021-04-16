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
import com.itextpdf.dito.manager.entity.TemplateTypeEnum;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.integration.AbstractIntegrationTest;
import com.itextpdf.dito.manager.integration.editor.controller.template.TemplateManagementController;
import com.itextpdf.dito.manager.model.template.dtm.DtmFileDescriptorModel;
import com.itextpdf.dito.manager.model.template.dtm.context.DtmFileExportContext;
import com.itextpdf.dito.manager.repository.resource.ResourceRepository;
import com.itextpdf.dito.manager.repository.template.TemplateRepository;
import com.itextpdf.dito.manager.service.datacollection.DataCollectionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DtmTemplateExtractorTest extends AbstractIntegrationTest {
    private static final String WORKSPACE_ID = "c29tZS10ZW1wbGF0ZQ==";

    @Autowired
    private DataCollectionService dataCollectionService;
    @Autowired
    private TemplateRepository templateRepository;
    @Autowired
    private ResourceRepository resourceRepository;
    @Autowired
    private DtmModelExtractor extractor;

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
        final byte[] updatedTemplateBody = Files.readString(Path.of("src/test/resources/test-data/templates/template-update-request-data.html")).replace("replaceThis", resourceEntity.getUuid()).getBytes(StandardCharsets.UTF_8);

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

    }

    @AfterEach
    void clearDb() {
        templateRepository.deleteAll();
        dataCollectionService.delete("new-data-collection", "admin@email.com");
        resourceRepository.deleteAll();
    }

    @ParameterizedTest
    @CsvSource({
            "some-template",
            "another-footer-template",
            "some-template-with-data-collection"
    })
    void shouldExtractStandardTemplate(String templateName) throws Exception {
        final TemplateEntity templateEntity = templateRepository.findByName(templateName).orElseThrow();
        final DtmFileExportContext context = new DtmFileExportContext(templateEntity, true);
        final DtmFileDescriptorModel model = new DtmFileDescriptorModel("DTM2", "test-version");
        extractor.extract(context, model);
        assertEquals(1, model.getTemplates().size());
        model.getTemplates().forEach(dtmTemplateDescriptorModel -> {
            assertEquals(1, dtmTemplateDescriptorModel.getVersions().size());
            assertEquals(0, dtmTemplateDescriptorModel.getVersions().get(0).getUsedIn().size());
        });

        System.out.println("----------------");
        System.out.println(objectMapper.writeValueAsString(model));
        System.out.println("----------------");
    }

    @Test
    void shouldExtractCompositionTemplate() throws Exception {
        final TemplateEntity templateEntity = templateRepository.findByName("composite-template").orElseThrow();
        final DtmFileExportContext context = new DtmFileExportContext(templateEntity, true);
        final DtmFileDescriptorModel model = new DtmFileDescriptorModel("DTM2", "test-version");
        extractor.extract(context, model);
        assertEquals(4, model.getTemplates().size());
        model.getTemplates().forEach(dtmTemplateDescriptorModel -> {
            assertEquals(1, dtmTemplateDescriptorModel.getVersions().size());
            final int expected = TemplateTypeEnum.COMPOSITION.equals(dtmTemplateDescriptorModel.getType()) ? 0 : 1;
            assertEquals(expected, dtmTemplateDescriptorModel.getVersions().get(0).getUsedIn().size());
        });

        System.out.println("----------------");
        System.out.println(objectMapper.writeValueAsString(model));
        System.out.println("----------------");
    }

    private void performCreateTemplateRequest(final String pathname) throws Exception {
        final TemplateCreateRequestDTO request = objectMapper.readValue(new File(pathname), TemplateCreateRequestDTO.class);

        mockMvc.perform(post(TemplateController.BASE_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldExtractTemplateWithResources() throws Exception {
        final TemplateEntity templateEntity = templateRepository.findByName("template-example").orElseThrow();
        final DtmFileExportContext context = new DtmFileExportContext(templateEntity, true);
        final DtmFileDescriptorModel model = new DtmFileDescriptorModel("DTM2", "test-version");
        extractor.extract(context, model);
        assertEquals(1, model.getTemplates().size());
        model.getTemplates().forEach(dtmTemplateDescriptorModel -> {
            assertEquals(1, dtmTemplateDescriptorModel.getVersions().size());
            assertEquals(0, dtmTemplateDescriptorModel.getVersions().get(0).getUsedIn().size());
        });
        assertEquals(1, model.getResources().size());
        model.getResources().forEach(dtmResourceDescriptorModel -> {
            assertEquals(1, dtmResourceDescriptorModel.getVersions().size());
            assertEquals(1, dtmResourceDescriptorModel.getVersions().get(0).getUsedIn().size());
        });

        System.out.println("----------------");
        System.out.println(objectMapper.writeValueAsString(model));
        System.out.println("----------------");
    }

    @Test
    void shouldExtractCompositionTemplateWithResources() throws Exception {
        final TemplateEntity templateEntity = templateRepository.findByName("composition_template2").orElseThrow();
        final DtmFileExportContext context = new DtmFileExportContext(templateEntity, true);
        final DtmFileDescriptorModel model = new DtmFileDescriptorModel("DTM2", "test-version");
        extractor.extract(context, model);
        assertEquals(1, model.getDataSamples().size());
        model.getDataSamples().forEach(dataSampleDescriptorModel -> {
            assertEquals(2, dataSampleDescriptorModel.getVersions().size());
        });
        assertEquals(1, model.getDataCollections().size());
        model.getDataCollections().forEach(dataCollectionDescriptorModel -> {
            assertEquals(1, dataCollectionDescriptorModel.getVersions().size());
            assertEquals(1, dataCollectionDescriptorModel.getSamples().size());
            assertEquals(2, dataCollectionDescriptorModel.getVersions().get(0).getUsedIn().size());
        });
        assertEquals(5, model.getTemplates().size());
        model.getTemplates().forEach(dtmTemplateDescriptorModel -> {
            assertEquals(1, dtmTemplateDescriptorModel.getVersions().size());
            final int expected = TemplateTypeEnum.COMPOSITION.equals(dtmTemplateDescriptorModel.getType()) ? 0 : 1;
            assertEquals(expected, dtmTemplateDescriptorModel.getVersions().get(0).getUsedIn().size());
        });
        assertEquals(1, model.getResources().size());
        model.getResources().forEach(dtmResourceDescriptorModel -> {
            assertEquals(1, dtmResourceDescriptorModel.getVersions().size());
            assertEquals(1, dtmResourceDescriptorModel.getVersions().get(0).getUsedIn().size());
        });

        System.out.println("----------------");
        System.out.println(objectMapper.writeValueAsString(model));
        System.out.println("----------------");
    }

}

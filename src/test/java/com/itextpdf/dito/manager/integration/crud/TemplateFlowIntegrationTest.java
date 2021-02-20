package com.itextpdf.dito.manager.integration.crud;

import com.itextpdf.dito.manager.controller.resource.ResourceController;
import com.itextpdf.dito.manager.controller.template.TemplateController;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionType;
import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.dto.template.create.TemplateCreateRequestDTO;
import com.itextpdf.dito.manager.dto.template.update.TemplateUpdateRequestDTO;
import com.itextpdf.dito.manager.entity.InstanceEntity;
import com.itextpdf.dito.manager.entity.StageEntity;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;
import com.itextpdf.dito.manager.integration.AbstractIntegrationTest;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionRepository;
import com.itextpdf.dito.manager.repository.datasample.DataSampleRepository;
import com.itextpdf.dito.manager.repository.instance.InstanceRepository;
import com.itextpdf.dito.manager.repository.resource.ResourceRepository;
import com.itextpdf.dito.manager.repository.stage.StageRepository;
import com.itextpdf.dito.manager.repository.template.TemplateFileRepository;
import com.itextpdf.dito.manager.repository.template.TemplateRepository;
import com.itextpdf.dito.manager.repository.workspace.WorkspaceRepository;
import com.itextpdf.dito.manager.service.datacollection.DataCollectionService;
import com.itextpdf.dito.manager.service.datasample.DataSampleService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.itextpdf.dito.manager.controller.template.TemplateController.TEMPLATE_DEPENDENCIES_PAGEABLE_ENDPOINT_WITH_PATH_VARIABLE;
import static com.itextpdf.dito.manager.controller.template.TemplateController.TEMPLATE_EXPORT_ENDPOINT_WITH_PATH_VARIABLE;
import static com.itextpdf.dito.manager.controller.template.TemplateController.TEMPLATE_PROMOTE_ENDPOINT_WITH_PATH_VARIABLE;
import static com.itextpdf.dito.manager.controller.template.TemplateController.TEMPLATE_ROLLBACK_ENDPOINT_WITH_PATH_VARIABLE;
import static com.itextpdf.dito.manager.controller.template.TemplateController.TEMPLATE_UNDEPLOY_ENDPOINT_WITH_PATH_VARIABLE;
import static com.itextpdf.dito.manager.controller.template.TemplateController.TEMPLATE_VERSION_ENDPOINT;
import static com.itextpdf.dito.manager.controller.template.TemplateController.TEMPLATE_VERSION_ENDPOINT_WITH_PATH_VARIABLE;
import static com.itextpdf.dito.manager.integration.editor.controller.template.TemplateManagementController.TEMPLATE_URL;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class TemplateFlowIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private TemplateRepository templateRepository;
    @Autowired
    private TemplateFileRepository templateFileRepository;
    @Autowired
    private DataCollectionRepository dataCollectionRepository;
    @Autowired
    private DataCollectionService dataCollectionService;
    @Autowired
    private DataSampleRepository dataSampleRepository;
    @Autowired
    private WorkspaceRepository workspaceRepository;
    @Autowired
    private InstanceRepository instanceRepository;
    @Autowired
    private StageRepository stageRepository;
    @Autowired
    private DataSampleService dataSampleService;
    @Autowired
    private ResourceRepository resourceRepository;

    @AfterEach
    public void clearDb() {
        templateRepository.deleteAll();
        templateFileRepository.deleteAll();
        resourceRepository.deleteAll();
        dataSampleRepository.deleteAll();
        dataCollectionRepository.deleteAll();
    }

    @Test
    public void testCreateTemplateWithoutData() throws Exception {
        addStage();

        TemplateCreateRequestDTO request = objectMapper.readValue(new File("src/test/resources/test-data/templates/template-create-request.json"), TemplateCreateRequestDTO.class);
        mockMvc.perform(post(TemplateController.BASE_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        assertTrue(templateRepository.findByName(request.getName()).isPresent());

        mockMvc.perform(post(TemplateController.BASE_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());

        //Create new version
        final MockMultipartFile file = new MockMultipartFile("template", "template.html", "text/plain", Files.readAllBytes(Path.of("src/test/resources/test-data/resources/random.png")));
        final MockMultipartFile description = new MockMultipartFile("description", "description", "text/plain", "test description".getBytes());
        final MockMultipartFile name = new MockMultipartFile("name", "name", "text/plain", request.getName().getBytes());
        final URI uri = UriComponentsBuilder.fromUriString(TemplateController.BASE_NAME + TEMPLATE_VERSION_ENDPOINT).build().encode().toUri();
        mockMvc.perform(MockMvcRequestBuilders.multipart(uri)
                .file(file)
                .file(name)
                .file(description)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("version").value("2"))
                .andExpect(jsonPath("modifiedOn").isNotEmpty())
                .andExpect(jsonPath("modifiedBy").isNotEmpty());

        //get versions
        String encodedTemplateName = Base64.getEncoder().encodeToString(request.getName().getBytes());
        mockMvc.perform(get(TemplateController.BASE_NAME + TEMPLATE_VERSION_ENDPOINT_WITH_PATH_VARIABLE, encodedTemplateName))
                .andExpect(status().isOk());

        final Long currentVersion = 2L;
        //Promote
        mockMvc.perform(put(TemplateController.BASE_NAME + TEMPLATE_PROMOTE_ENDPOINT_WITH_PATH_VARIABLE, encodedTemplateName, currentVersion))
                .andExpect(status().isOk())
                .andExpect(jsonPath("version").value("2"))
                .andExpect(jsonPath("stageName").value("STAGE"))
                .andExpect(jsonPath("deployed").value("true"));

        //Undeploy
        mockMvc.perform(put(TemplateController.BASE_NAME + TEMPLATE_UNDEPLOY_ENDPOINT_WITH_PATH_VARIABLE, encodedTemplateName, currentVersion))
                .andExpect(status().isOk())
                .andExpect(jsonPath("version").value("2"))
                .andExpect(jsonPath("stageName").value("DEV"))
                .andExpect(jsonPath("deployed").value("false"));

        //Rollback version
        mockMvc.perform(post(TemplateController.BASE_NAME + TEMPLATE_ROLLBACK_ENDPOINT_WITH_PATH_VARIABLE, encodedTemplateName, currentVersion))
                .andExpect(status().isOk())
                .andExpect(jsonPath("version").value("3"))
                .andExpect(jsonPath("modifiedOn").isNotEmpty())
                .andExpect(jsonPath("modifiedBy").isNotEmpty());

        //Export
        mockMvc.perform(get(TemplateController.BASE_NAME + TEMPLATE_EXPORT_ENDPOINT_WITH_PATH_VARIABLE, encodedTemplateName))
                .andExpect(status().isOk())
                .andExpect(matchZipEntries("templates\\some-template"));

    }

    @Test
    public void testCreateTemplateWithData() throws Exception {
        final String userEmail = "admin@email.com";
        final DataCollectionEntity dataCollectionEntity = dataCollectionService.create("data-collection", DataCollectionType.JSON, "{\"file\":\"data\"}".getBytes(), "datacollection.json", userEmail);
        dataSampleService.create(dataCollectionEntity, "ds1", "ds1.json", "{\"file\":\"file1.json\"}", "comment1", userEmail);

        TemplateCreateRequestDTO request = objectMapper.readValue(new File("src/test/resources/test-data/templates/template-create-request.json"), TemplateCreateRequestDTO.class);
        request.setDataCollectionName("data-collection");
        mockMvc.perform(post(TemplateController.BASE_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("dataCollection").value("data-collection"));
        Optional<TemplateEntity> template = templateRepository.findByName(request.getName());
        assertTrue(template.isPresent());

        String encodedTemplateName = Base64.getEncoder().encodeToString(request.getName().getBytes());
        mockMvc.perform(get(TemplateController.BASE_NAME + TemplateController.TEMPLATE_ENDPOINT_WITH_PATH_VARIABLE, encodedTemplateName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").isNotEmpty())
                .andExpect(jsonPath("dataCollection").isNotEmpty())
                .andExpect(jsonPath("createdBy").isNotEmpty())
                .andExpect(jsonPath("createdOn").isNotEmpty())
                .andExpect(jsonPath("modifiedBy").isNotEmpty())
                .andExpect(jsonPath("modifiedOn").isNotEmpty())
                .andExpect(jsonPath("description").isEmpty());

        //get dependencies
        mockMvc.perform(get(TemplateController.BASE_NAME + TemplateController.TEMPLATE_DEPENDENCIES_ENDPOINT_WITH_PATH_VARIABLE, encodedTemplateName))
                .andExpect(status().isOk());

        //get preview
        mockMvc.perform(get(TemplateController.BASE_NAME + TemplateController.TEMPLATE_PREVIEW_ENDPOINT_WITH_PATH_VARIABLE, encodedTemplateName))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));

        TemplateUpdateRequestDTO updateRequestDTO = objectMapper.readValue(new File("src/test/resources/test-data/templates/template-update-request.json"), TemplateUpdateRequestDTO.class);
        mockMvc.perform(patch(TemplateController.BASE_NAME + TemplateController.TEMPLATE_ENDPOINT_WITH_PATH_VARIABLE, encodedTemplateName)
                .content(objectMapper.writeValueAsString(updateRequestDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").isNotEmpty())
                .andExpect(jsonPath("dataCollection").isNotEmpty())
                .andExpect(jsonPath("createdBy").isNotEmpty())
                .andExpect(jsonPath("createdOn").isNotEmpty())
                .andExpect(jsonPath("modifiedBy").isNotEmpty())
                .andExpect(jsonPath("modifiedOn").isNotEmpty())
                .andExpect(jsonPath("description").isNotEmpty());

        //check export
        final String encodedUpdatedTemplateName = Base64.getEncoder().encodeToString(updateRequestDTO.getName().getBytes());
        mockMvc.perform(get(TemplateController.BASE_NAME + TEMPLATE_EXPORT_ENDPOINT_WITH_PATH_VARIABLE, encodedUpdatedTemplateName))
                .andExpect(status().isOk())
                .andExpect(matchZipEntries("templates\\updated-template-name", "data\\ds1.json"));
    }

    @Test
    public void createTemplate_WhenTemplateWithSameNameExists_ThenResponseIsBadRequest() throws Exception {
        TemplateCreateRequestDTO request = objectMapper.readValue(new File("src/test/resources/test-data/templates/template-create-request.json"), TemplateCreateRequestDTO.class);
        mockMvc.perform(post(TemplateController.BASE_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(post(TemplateController.BASE_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
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
        mockMvc.perform(get(TemplateController.BASE_NAME + TemplateController.TEMPLATE_TYPES_ENDPOINT))
                .andExpect(status().isOk());
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
    void testCreateCompositionTemplateWithoutDataCollection() throws Exception {
        dataCollectionService.create("new-data-collection", DataCollectionType.JSON, "{\"file\":\"data\"}".getBytes(), "datacollection.json", "admin@email.com");

        performCreateTemplateRequest("src/test/resources/test-data/templates/template-create-request.json");
        performCreateTemplateRequest("src/test/resources/test-data/templates/template-create-request-header.json");
        performCreateTemplateRequest("src/test/resources/test-data/templates/template-create-request-footer.json");
        performCreateTemplateRequest("src/test/resources/test-data/templates/template-create-request-footer2.json");
        performCreateTemplateRequest("src/test/resources/test-data/templates/template-create-request-with-data-collection2.json");

        final TemplateCreateRequestDTO request = objectMapper.readValue(new File("src/test/resources/test-data/templates/template-create-request-composition.json"), TemplateCreateRequestDTO.class);
        mockMvc.perform(post(TemplateController.BASE_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        request.getTemplateParts().removeIf(part -> "some-footer-template".equals(part.getName()));
        mockMvc.perform(post(TemplateController.BASE_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        request.getTemplateParts().removeIf(part -> "some-template-with-data-collection".equals(part.getName()));
        mockMvc.perform(post(TemplateController.BASE_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        final MockMultipartFile templateParts = new MockMultipartFile("templateParts", "templateParts", "application/json", objectMapper.writeValueAsString(request.getTemplateParts()).getBytes());
        final MockMultipartFile comment = new MockMultipartFile("comment", "description", "text/plain", "test comment".getBytes());
        final MockMultipartFile name = new MockMultipartFile("name", "name", "text/plain", request.getName().getBytes());
        final URI uri = UriComponentsBuilder.fromUriString(TemplateController.BASE_NAME + TEMPLATE_VERSION_ENDPOINT).build().encode().toUri();
        mockMvc.perform(MockMvcRequestBuilders.multipart(uri)
                .file(name)
                .file(comment)
                .file(templateParts)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("version").value("2"))
                .andExpect(jsonPath("modifiedOn").isNotEmpty())
                .andExpect(jsonPath("modifiedBy").isNotEmpty());


        final List<TemplateFileEntity> files = new ArrayList<>();
        files.add(templateRepository.findByName("some-template").get().getLatestFile());
        files.add(templateRepository.findByName("some-header-template").get().getLatestFile());
        files.add(templateRepository.findByName("some-footer-template").get().getLatestFile());
        files.add(templateRepository.findByName("another-footer-template").get().getLatestFile());
        files.add(templateRepository.findByName("some-template-with-data-collection").get().getLatestFile());
        files.add(templateRepository.findByName("composite-template").get().getLatestFile());
        generateStageEntity(files);

        //check dependencies
        final String encodedTemplateName = encodeStringToBase64(request.getName());
        mockMvc.perform(get(TemplateController.BASE_NAME + TEMPLATE_DEPENDENCIES_PAGEABLE_ENDPOINT_WITH_PATH_VARIABLE, encodedTemplateName).queryParam("dependencyType", "TEMPLATE", "DATA_COLLECTION")
                .param("stage", "STAGE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.content[*].directionType", containsInAnyOrder("SOFT", "SOFT", "SOFT")))
                .andExpect(jsonPath("$.content[*].dependencyType", containsInAnyOrder("TEMPLATE", "TEMPLATE", "TEMPLATE")))
                .andExpect(jsonPath("$.content[*].name", containsInAnyOrder("some-template", "some-header-template", "another-footer-template")));

        //check export
        mockMvc.perform(get(TemplateController.BASE_NAME + TEMPLATE_EXPORT_ENDPOINT_WITH_PATH_VARIABLE, encodedTemplateName))
                .andExpect(status().isOk())
                .andExpect(matchZipEntries("templates\\composite-template"));
    }

    @Test
    @Disabled
    void testCreateCompositionTemplateWithDataCollection() throws Exception {
        dataCollectionService.create("new-data-collection", DataCollectionType.JSON, "{\"file\":\"data\"}".getBytes(), "datacollection.json", "admin@email.com");

        performCreateTemplateRequest("src/test/resources/test-data/templates/template-create-request.json");
        performCreateTemplateRequest("src/test/resources/test-data/templates/template-create-request-header.json");
        performCreateTemplateRequest("src/test/resources/test-data/templates/template-create-request-footer.json");
        performCreateTemplateRequest("src/test/resources/test-data/templates/template-create-request-footer2.json");
        performCreateTemplateRequest("src/test/resources/test-data/templates/template-create-request-with-data-collection2.json");

        final TemplateCreateRequestDTO request = objectMapper.readValue(new File("src/test/resources/test-data/templates/template-create-request-composition-with-data-collection.json"), TemplateCreateRequestDTO.class);
        mockMvc.perform(post(TemplateController.BASE_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        request.getTemplateParts().removeIf(part -> "some-footer-template".equals(part.getName()));
        mockMvc.perform(post(TemplateController.BASE_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        final String encodedTemplateName = encodeStringToBase64(request.getName());

        //get template
        mockMvc.perform(get(TemplateController.BASE_NAME + TemplateController.TEMPLATE_ENDPOINT_WITH_PATH_VARIABLE, encodedTemplateName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(request.getName()))
                .andExpect(jsonPath("dataCollection").isNotEmpty())
                .andExpect(jsonPath("createdBy").isNotEmpty())
                .andExpect(jsonPath("createdOn").isNotEmpty())
                .andExpect(jsonPath("modifiedBy").isNotEmpty())
                .andExpect(jsonPath("modifiedOn").isNotEmpty())
                .andExpect(jsonPath("description").isEmpty());

        //Update existing composition template
        final TemplateUpdateRequestDTO updateRequestDTO = objectMapper.readValue(new File("src/test/resources/test-data/templates/template-update-request.json"), TemplateUpdateRequestDTO.class);
        mockMvc.perform(patch(TemplateController.BASE_NAME + TemplateController.TEMPLATE_ENDPOINT_WITH_PATH_VARIABLE, encodedTemplateName)
                .content(objectMapper.writeValueAsString(updateRequestDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(updateRequestDTO.getName()))
                .andExpect(jsonPath("type").value("COMPOSITION"))
                .andExpect(jsonPath("dataCollection").isNotEmpty())
                .andExpect(jsonPath("createdBy").isNotEmpty())
                .andExpect(jsonPath("createdOn").isNotEmpty())
                .andExpect(jsonPath("modifiedBy").isNotEmpty())
                .andExpect(jsonPath("modifiedOn").isNotEmpty())
                .andExpect(jsonPath("description").value(updateRequestDTO.getDescription()));

        //Create new version
        request.getTemplateParts().removeIf(part -> "another-footer-template".equals(part.getName()));
        final MockMultipartFile templateParts = new MockMultipartFile("templateParts", "templateParts", "application/json", objectMapper.writeValueAsString(request.getTemplateParts()).getBytes());
        final MockMultipartFile comment = new MockMultipartFile("comment", "description", "text/plain", "test comment".getBytes());
        final MockMultipartFile name = new MockMultipartFile("name", "name", "text/plain", updateRequestDTO.getName().getBytes());
        final URI uri = UriComponentsBuilder.fromUriString(TemplateController.BASE_NAME + TEMPLATE_VERSION_ENDPOINT).build().encode().toUri();
        mockMvc.perform(MockMvcRequestBuilders.multipart(uri)
                .file(name)
                .file(comment)
                .file(templateParts)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("version").value("2"))
                .andExpect(jsonPath("modifiedOn").isNotEmpty())
                .andExpect(jsonPath("modifiedBy").isNotEmpty());

        //get versions
        final String encTemplateName = Base64.getEncoder().encodeToString(updateRequestDTO.getName().getBytes());
        mockMvc.perform(get(TemplateController.BASE_NAME + TEMPLATE_VERSION_ENDPOINT_WITH_PATH_VARIABLE, encTemplateName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[*].version", containsInAnyOrder(1, 2)))
                .andExpect(jsonPath("$.content[*].comment", containsInAnyOrder(null, "test comment")));

        final List<TemplateFileEntity> files = new ArrayList<>();
        files.add(templateRepository.findByName("some-template").get().getLatestFile());
        files.add(templateRepository.findByName("some-header-template").get().getLatestFile());
        files.add(templateRepository.findByName("some-footer-template").get().getLatestFile());
        files.add(templateRepository.findByName("another-footer-template").get().getLatestFile());
        files.add(templateRepository.findByName("some-template-with-data-collection").get().getLatestFile());
        files.add(templateRepository.findByName("updated-template-name").get().getLatestFile());
        generateStageEntity(files);
        //check dependencies
        mockMvc.perform(get(TemplateController.BASE_NAME + TEMPLATE_DEPENDENCIES_PAGEABLE_ENDPOINT_WITH_PATH_VARIABLE, encTemplateName).queryParam("dependencyType", "TEMPLATE", "DATA_COLLECTION")
                .param("stage", "STAGE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(4)))
                .andExpect(jsonPath("$.content[*].directionType", containsInAnyOrder("SOFT", "SOFT", "SOFT", "SOFT")))
                .andExpect(jsonPath("$.content[*].dependencyType", containsInAnyOrder("DATA_COLLECTION", "TEMPLATE", "TEMPLATE", "TEMPLATE")))
                .andExpect(jsonPath("$.content[*].name", containsInAnyOrder("new-data-collection", "some-template", "some-header-template", "some-template-with-data-collection")));

        final String encodedStandardTemplateName = Base64.getEncoder().encodeToString("some-template-with-data-collection".getBytes());
        mockMvc.perform(get(TemplateController.BASE_NAME + TEMPLATE_DEPENDENCIES_PAGEABLE_ENDPOINT_WITH_PATH_VARIABLE, encodedStandardTemplateName).queryParam("dependencyType", "TEMPLATE", "DATA_COLLECTION")
                .param("stage", "STAGE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[*].directionType", containsInAnyOrder("SOFT", "HARD")))
                .andExpect(jsonPath("$.content[*].name", containsInAnyOrder("new-data-collection", updateRequestDTO.getName())));

        //get preview
        mockMvc.perform(get(TemplateController.BASE_NAME + TemplateController.TEMPLATE_PREVIEW_ENDPOINT_WITH_PATH_VARIABLE, encTemplateName))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));

        //get preview
        mockMvc.perform(delete(TemplateController.BASE_NAME + TemplateController.TEMPLATE_ENDPOINT_WITH_PATH_VARIABLE, encTemplateName))
                .andExpect(status().isOk());
    }

    private StageEntity addStage() {
        final StageEntity stageEntity = new StageEntity();
        stageEntity.setSequenceOrder(1);
        stageEntity.setName("STAGE");
        stageEntity.setPromotionPath(defaultPromotionPathEntity);
        final InstanceEntity instanceEntity = new InstanceEntity();
        instanceEntity.setName("instance");
        instanceEntity.setSocket("socket");
        instanceEntity.setStage(stageEntity);
        stageEntity.setInstances(Arrays.asList(instanceEntity));
        instanceRepository.save(instanceEntity);
        return stageEntity;
    }

    @Test
    void shouldCreateAndExportCompositionTemplateWithAllResources() throws Exception {
        final String userEmail = "admin@email.com";
        final DataCollectionEntity dataCollectionEntity = dataCollectionService.create("new-data-collection", DataCollectionType.JSON, "{\"file\":\"data\"}".getBytes(), "datacollection.json", userEmail);
        dataSampleService.create(dataCollectionEntity, "ds1", "ds1.json", "{\"file\":\"file1.json\"}", "comment1", userEmail);

        performCreateTemplateRequest("src/test/resources/test-data/templates/template-create-request.json");
        performCreateTemplateRequest("src/test/resources/test-data/templates/template-create-request-header.json");
        performCreateTemplateRequest("src/test/resources/test-data/templates/template-create-request-footer.json");
        performCreateTemplateRequest("src/test/resources/test-data/templates/template-create-request-with-data-collection2.json");

        //Create resource
        final String imageName = "b02.jpg";
        mockMvc.perform(MockMvcRequestBuilders.multipart(ResourceController.BASE_NAME)
                .file(new MockMultipartFile("resource", imageName, "text/plain", readFileBytes("src/test/resources/test-data/resources/random.png")))
                .file(new MockMultipartFile("type", "type", "text/plain", ResourceTypeEnum.IMAGE.toString().getBytes()))
                .file(new MockMultipartFile("name", "name", "text/plain", imageName.getBytes()))
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated());

        final String encodeTemplatePartString = encodeStringToBase64("some-template");

        //Create new version
        final MockMultipartFile file = new MockMultipartFile("data", "template.html", "text/plain", Files.readAllBytes(Path.of("src/test/resources/test-data/templates/template-update-request-data.html")));
        mockMvc.perform(MockMvcRequestBuilders.multipart(TEMPLATE_URL, encodeTemplatePartString)
                .file(file)
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                })
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());

        //Create composite template
        final TemplateCreateRequestDTO request = objectMapper.readValue(new File("src/test/resources/test-data/templates/template-create-request-composition-for-export.json"), TemplateCreateRequestDTO.class);
        mockMvc.perform(post(TemplateController.BASE_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        final String encodedTemplateName = encodeStringToBase64(request.getName());

        //check export
        mockMvc.perform(get(TemplateController.BASE_NAME + TEMPLATE_EXPORT_ENDPOINT_WITH_PATH_VARIABLE, encodedTemplateName))
                .andExpect(status().isOk())
                .andExpect(countZipEntries(3))
                .andExpect(hasItems("templates\\composite-template", "data\\ds1.json"));

    }

    private void generateStageEntity(final List<TemplateFileEntity> files) {
        final StageEntity nextStage = addStage();
        for (final TemplateFileEntity file : files) {
            file.setStage(nextStage);
        }
        templateFileRepository.saveAll(files);
    }

    private ResultMatcher matchZipEntries(String... entries) {
        return mvcResult -> {
            try (final ZipInputStream zipStream = new ZipInputStream(new ByteArrayInputStream(mvcResult.getResponse().getContentAsByteArray()))) {
                final List<String> zipEntries = new LinkedList<>();
                ZipEntry ze;
                while ((ze = zipStream.getNextEntry()) != null) {
                    zipEntries.add(ze.getName());
                }
                assertThat(zipEntries, containsInAnyOrder(entries));
            }
        };
    }

    private ResultMatcher countZipEntries(int count) {
        return mvcResult -> {
            try (final ZipInputStream zipStream = new ZipInputStream(new ByteArrayInputStream(mvcResult.getResponse().getContentAsByteArray()))) {
                final List<String> zipEntries = new LinkedList<>();
                ZipEntry ze;
                while ((ze = zipStream.getNextEntry()) != null) {
                    zipEntries.add(ze.getName());
                }
                assertThat(zipEntries, hasSize(count));
            }
        };
    }

    private ResultMatcher hasItems(String... entries) {
        return mvcResult -> {
            try (final ZipInputStream zipStream = new ZipInputStream(new ByteArrayInputStream(mvcResult.getResponse().getContentAsByteArray()))) {
                final List<String> zipEntries = new LinkedList<>();
                ZipEntry ze;
                while ((ze = zipStream.getNextEntry()) != null) {
                    zipEntries.add(ze.getName());
                }
                for (String entry : entries) {
                    assertThat(zipEntries, hasItem(entry));
                }
            }
        };
    }

}

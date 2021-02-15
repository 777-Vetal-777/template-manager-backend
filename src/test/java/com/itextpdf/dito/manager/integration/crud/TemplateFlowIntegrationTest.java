package com.itextpdf.dito.manager.integration.crud;

import com.itextpdf.dito.manager.controller.template.TemplateController;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionType;
import com.itextpdf.dito.manager.dto.template.create.TemplateCreateRequestDTO;
import com.itextpdf.dito.manager.dto.template.update.TemplateUpdateRequestDTO;
import com.itextpdf.dito.manager.entity.InstanceEntity;
import com.itextpdf.dito.manager.entity.PromotionPathEntity;
import com.itextpdf.dito.manager.entity.StageEntity;
import com.itextpdf.dito.manager.entity.WorkspaceEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;
import com.itextpdf.dito.manager.integration.AbstractIntegrationTest;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionRepository;
import com.itextpdf.dito.manager.repository.instance.InstanceRepository;
import com.itextpdf.dito.manager.repository.stage.StageRepository;
import com.itextpdf.dito.manager.repository.template.TemplateFileRepository;
import com.itextpdf.dito.manager.repository.template.TemplateRepository;
import com.itextpdf.dito.manager.repository.workspace.WorkspaceRepository;
import com.itextpdf.dito.manager.service.datacollection.DataCollectionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static com.itextpdf.dito.manager.controller.template.TemplateController.TEMPLATE_DEPENDENCIES_PAGEABLE_ENDPOINT_WITH_PATH_VARIABLE;
import static com.itextpdf.dito.manager.controller.template.TemplateController.TEMPLATE_ROLLBACK_ENDPOINT_WITH_PATH_VARIABLE;
import static com.itextpdf.dito.manager.controller.template.TemplateController.TEMPLATE_VERSION_ENDPOINT;
import static com.itextpdf.dito.manager.controller.template.TemplateController.TEMPLATE_VERSION_ENDPOINT_WITH_PATH_VARIABLE;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    private WorkspaceRepository workspaceRepository;
    @Autowired
    private InstanceRepository instanceRepository;
    @Autowired
    private StageRepository stageRepository;

    @BeforeEach
    public void clearDb() {
        templateRepository.deleteAll();
        templateFileRepository.deleteAll();
        dataCollectionRepository.deleteAll();
        workspaceRepository.deleteAll();
        instanceRepository.deleteAll();
        stageRepository.deleteAll();
    }

    @Test
    public void testCreateTemplateWithoutData() throws Exception {
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
        final MockMultipartFile file = new MockMultipartFile("template", "template.html", "text/plain", "{\"file\":\"data\"}".getBytes());
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

        //Rollback version
        final Long currentVersion = 2L;
        mockMvc.perform(post(TemplateController.BASE_NAME + TEMPLATE_ROLLBACK_ENDPOINT_WITH_PATH_VARIABLE, encodedTemplateName, currentVersion))
                .andExpect(status().isOk())
                .andExpect(jsonPath("version").value("3"))
                .andExpect(jsonPath("modifiedOn").isNotEmpty())
                .andExpect(jsonPath("modifiedBy").isNotEmpty());
    }

    @Test
    public void testCreateTemplateWithData() throws Exception {
        dataCollectionService.create("data-collection", DataCollectionType.JSON, "{\"file\":\"data\"}".getBytes(), "datacollection.json", "admin@email.com");

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


    }

    @Test
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
    }

    private void generateStageEntity(final List<TemplateFileEntity> files) {
        final StageEntity stageEntity = new StageEntity();
        stageEntity.setSequenceOrder(1);
        stageEntity.setName("STAGE");
        final PromotionPathEntity promotionPathEntity = new PromotionPathEntity();
        stageEntity.setPromotionPath(promotionPathEntity);
        final WorkspaceEntity workspaceEntity = new WorkspaceEntity();
        promotionPathEntity.setWorkspace(workspaceEntity);
        workspaceEntity.setName("workspace");
        workspaceEntity.setTimezone("Europe/Brussels");
        workspaceEntity.setPromotionPath(promotionPathEntity);
        workspaceRepository.save(workspaceEntity);
        final InstanceEntity instanceEntity = new InstanceEntity();
        instanceEntity.setName("instance");
        instanceEntity.setSocket("socket");
        instanceEntity.setStage(stageEntity);
        instanceEntity.setTemplateFile(files);
        stageEntity.setInstances(Arrays.asList(instanceEntity));
        instanceRepository.save(instanceEntity);
        for (final TemplateFileEntity file : files) {
            file.setStage(stageEntity);
        }
        templateFileRepository.saveAll(files);
    }
}

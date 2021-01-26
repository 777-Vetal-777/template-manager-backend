package com.itextpdf.dito.manager.integration.crud;

import com.itextpdf.dito.manager.controller.datacollection.DataCollectionController;
import com.itextpdf.dito.manager.controller.instance.InstanceController;
import com.itextpdf.dito.manager.controller.resource.ResourceController;
import com.itextpdf.dito.manager.controller.template.TemplateController;
import com.itextpdf.dito.manager.controller.workspace.WorkspaceController;
import com.itextpdf.dito.manager.dto.instance.create.InstancesRememberRequestDTO;
import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.dto.resource.update.ResourceUpdateRequestDTO;
import com.itextpdf.dito.manager.dto.template.create.TemplateCreateRequestDTO;
import com.itextpdf.dito.manager.dto.workspace.create.WorkspaceCreateRequestDTO;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceFileEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.integration.AbstractIntegrationTest;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionLogRepository;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionRepository;
import com.itextpdf.dito.manager.repository.instance.InstanceRepository;
import com.itextpdf.dito.manager.repository.resource.ResourceFileRepository;
import com.itextpdf.dito.manager.repository.resource.ResourceLogRepository;
import com.itextpdf.dito.manager.repository.resource.ResourceRepository;
import com.itextpdf.dito.manager.repository.template.TemplateFileRepository;
import com.itextpdf.dito.manager.repository.template.TemplateRepository;
import com.itextpdf.dito.manager.repository.workspace.WorkspaceRepository;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.net.URI;
import java.util.Base64;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

import static com.itextpdf.dito.manager.controller.resource.ResourceController.FONTS_ENDPOINT;
import static com.itextpdf.dito.manager.controller.resource.ResourceController.RESOURCE_VERSION_ENDPOINT;
import static com.itextpdf.dito.manager.dto.dependency.DependencyDirectionType.HARD;
import static com.itextpdf.dito.manager.dto.dependency.DependencyType.TEMPLATE;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ResourceFlowIntegrationTest extends AbstractIntegrationTest {
    private static final String DATA_COLLECTION_NAME = "DataCollectionName";
    private static final String NAME = "test-image";
    private static final String IMAGE_TYPE = "IMAGE";
    private static final String FONT_TYPE = "FONT";
    private static final String IMAGES = "images";
    private static final String FONTS = "fonts";
    private static final String STYLESHEETS = "stylesheets";
    private static final String PICTURE_FILE_NAME = "any-name.png";

    private static final Integer AMOUNT_VERSIONS = 5;
    private static final String AUTHOR_NAME = "admin admin";
    private static final MockMultipartFile IMAGE_FILE_PART = new MockMultipartFile("resource", PICTURE_FILE_NAME, "text/plain", "{\"file\":\"data\"}".getBytes());
    private static final MockMultipartFile IMAGE_TYPE_PART = new MockMultipartFile("type", "type", "text/plain", IMAGE_TYPE.getBytes());
    private static final MockMultipartFile NAME_PART = new MockMultipartFile("name", "name", "text/plain", NAME.getBytes());

    private static final String REGULAR_FILE_NAME = "regular.ttf";
    private static final String BOLD_FILE_NAME = "bold.ttf";
    private static final String ITALIC_FILE_NAME = "italic.ttf";
    private static final String BOLD_ITALIC_FILE_NAME = "bold_italic.ttf";

    private static final MockMultipartFile FONT_TYPE_PART = new MockMultipartFile("type", FONT_TYPE, "text/plain", FONT_TYPE.getBytes());
    private static final MockMultipartFile REGULAR_FONT_FILE_PART = new MockMultipartFile("regular", REGULAR_FILE_NAME, "text/plain", "{\"file\":\"data\"}".getBytes());
    private static final MockMultipartFile BOLD_FONT_FILE_PART = new MockMultipartFile("bold", BOLD_FILE_NAME, "text/plain", "{\"file\":\"data\"}".getBytes());
    private static final MockMultipartFile ITALIC_FONT_FILE_PART = new MockMultipartFile("italic", ITALIC_FILE_NAME, "text/plain", "{\"file\":\"data\"}".getBytes());
    private static final MockMultipartFile BOLD_ITALIC_FILE_PART = new MockMultipartFile("bold_italic", BOLD_ITALIC_FILE_NAME, "text/plain", "{\"file\":\"data\"}".getBytes());

    @Autowired
    private ResourceRepository resourceRepository;
    @Autowired
    private ResourceFileRepository resourceFileRepository;
    @Autowired
    private ResourceLogRepository resourceLogRepository;
    @Autowired
    private TemplateRepository templateRepository;
    @Autowired
    private TemplateFileRepository templateFileRepository;
    @Autowired
    private DataCollectionRepository dataCollectionRepository;
    @Autowired
    private DataCollectionLogRepository dataCollectionLogRepository;
    @Autowired
    private WorkspaceRepository workspaceRepository;
    @Autowired
    private InstanceRepository instanceRepository;

    @AfterEach
    void tearDown() {
        templateRepository.deleteAll();
        dataCollectionLogRepository.deleteAll();
        dataCollectionRepository.deleteAll();
        resourceLogRepository.deleteAll();
        resourceFileRepository.deleteAll();
        resourceRepository.deleteAll();
        workspaceRepository.deleteAll();
        instanceRepository.deleteAll();

    }

    private MockMultipartFile getUpdateTemplateBooleanPart(Boolean updateTemplate) {
        return new MockMultipartFile("updateTemplate", "updateTemplate", "application/json",
                updateTemplate.toString().getBytes());
    }

    @Test
    void shouldCreateFontAndReturnFileByUUID() throws Exception {
        final URI createFontURI = UriComponentsBuilder.fromUriString(ResourceController.BASE_NAME+FONTS_ENDPOINT).build().encode().toUri();
        mockMvc.perform(MockMvcRequestBuilders.multipart(createFontURI)
                .file(NAME_PART)
                .file(FONT_TYPE_PART)
                .file(REGULAR_FONT_FILE_PART)
                .file(BOLD_FONT_FILE_PART)
                .file(ITALIC_FONT_FILE_PART)
                .file(BOLD_ITALIC_FILE_PART)
                .file(IMAGE_TYPE_PART)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());
        final Optional<ResourceEntity> createdResourceEntity = resourceRepository.findByNameAndType(NAME, ResourceTypeEnum.FONT);
        assertTrue(createdResourceEntity.isPresent());
        final List<ResourceFileEntity> files = createdResourceEntity.get().getLatestFile();
        files.forEach(file ->{
            try {
                mockMvc.perform(
                        get(ResourceController.BASE_NAME + ResourceController.RESOURCE_FILE_ENDPOINT_WITH_FILE_PATH_VARIABLE, file.getUuid()))
                        .andExpect(status().isOk());
            } catch (Exception ignored) {}

        });
        mockMvc.perform(get(ResourceController.BASE_NAME + ResourceController.RESOURCE_FILE_ENDPOINT_WITH_FILE_PATH_VARIABLE, "BAD UUID"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateNewFontsAndReturnThem() throws Exception {
        final URI createFontURI = UriComponentsBuilder.fromUriString(ResourceController.BASE_NAME+FONTS_ENDPOINT).build().encode().toUri();
              //create resource
        mockMvc.perform(MockMvcRequestBuilders.multipart(createFontURI)
                .file(NAME_PART)
                .file(FONT_TYPE_PART)
                .file(REGULAR_FONT_FILE_PART)
                .file(BOLD_FONT_FILE_PART)
                .file(ITALIC_FONT_FILE_PART)
                .file(BOLD_ITALIC_FILE_PART)
                .file(IMAGE_TYPE_PART)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(NAME))
                .andExpect(jsonPath("comment").isEmpty())
                .andExpect(jsonPath("description").isEmpty())
                .andExpect(jsonPath("createdOn").isNotEmpty())
                .andExpect(jsonPath("modifiedBy").value(AUTHOR_NAME))
                .andExpect(jsonPath("modifiedOn").isNotEmpty())
                .andExpect(jsonPath("version").value(1))
                .andExpect(jsonPath("$.metadataUrls[0].fileName").isNotEmpty())
                .andExpect(jsonPath("$.metadataUrls[0].uuid").isNotEmpty())
                .andExpect(jsonPath("$.metadataUrls[0].fontType").isNotEmpty())
                .andExpect(jsonPath("$.metadataUrls[1].fileName").isNotEmpty())
                .andExpect(jsonPath("$.metadataUrls[1].uuid").isNotEmpty())
                .andExpect(jsonPath("$.metadataUrls[1].fontType").isNotEmpty())
                .andExpect(jsonPath("$.metadataUrls[2].fileName").isNotEmpty())
                .andExpect(jsonPath("$.metadataUrls[2].uuid").isNotEmpty())
                .andExpect(jsonPath("$.metadataUrls[2].fontType").isNotEmpty())
                .andExpect(jsonPath("$.metadataUrls[3].fileName").isNotEmpty())
                .andExpect(jsonPath("$.metadataUrls[3].uuid").isNotEmpty())
                .andExpect(jsonPath("$.metadataUrls[3].fontType").isNotEmpty());
        final Optional<ResourceEntity> createdResourceEntity = resourceRepository.findByNameAndType(NAME, ResourceTypeEnum.FONT);
        assertTrue(createdResourceEntity.isPresent());
        Long createdResourceId = createdResourceEntity.get().getId();
        assertNotNull(resourceFileRepository.findFirstByResource_IdOrderByVersionDesc(createdResourceId));
        assertNotNull(resourceLogRepository.findFirstByResource_IdOrderByDateDesc(createdResourceId));
        //Create again and get conflict error
        mockMvc.perform(MockMvcRequestBuilders.multipart(createFontURI)
                .file(NAME_PART)
                .file(FONT_TYPE_PART)
                .file(REGULAR_FONT_FILE_PART)
                .file(BOLD_FONT_FILE_PART)
                .file(ITALIC_FONT_FILE_PART)
                .file(BOLD_ITALIC_FILE_PART)
                .file(IMAGE_TYPE_PART)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isConflict());
        //GET by name
        mockMvc.perform(
                get(ResourceController.BASE_NAME + ResourceController.RESOURCE_ENDPOINT_WITH_PATH_VARIABLE_AND_TYPE,
                        FONTS, Base64.getEncoder().encodeToString(NAME.getBytes())))
                .andExpect(status().isOk());
        //UPDATE by name
        ResourceUpdateRequestDTO resourceUpdateRequestDTO = objectMapper.readValue(new File("src/test/resources/test-data/resources/fonts_update_metadata.json"), ResourceUpdateRequestDTO.class);

        mockMvc.perform(put(ResourceController.BASE_NAME + ResourceController.RESOURCE_ENDPOINT_WITH_PATH_VARIABLE, Base64.getEncoder().encodeToString(NAME.getBytes()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resourceUpdateRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(resourceUpdateRequestDTO.getName()))
                .andExpect(jsonPath("description").value(resourceUpdateRequestDTO.getDescription()));
        //DELETE by name
        mockMvc.perform(
                delete(ResourceController.BASE_NAME + ResourceController.RESOURCE_ENDPOINT_WITH_PATH_VARIABLE_AND_TYPE,
                        FONTS, Base64.getEncoder().encodeToString(resourceUpdateRequestDTO.getName().getBytes())))
                .andExpect(status().isOk());
        assertTrue(Objects.isNull(resourceFileRepository.findFirstByResource_IdOrderByVersionDesc(createdResourceId)));
        assertTrue(Objects.isNull(resourceLogRepository.findFirstByResource_IdOrderByDateDesc(createdResourceId)));

        //DELETE by name
        mockMvc.perform(
                delete(ResourceController.BASE_NAME + ResourceController.RESOURCE_ENDPOINT_WITH_PATH_VARIABLE_AND_TYPE,
                        FONTS, Base64.getEncoder().encodeToString(NAME.getBytes())))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnSuccessfullyFontResourceDependencies() throws Exception {
        //CREATE DATA COLLECTION
        final MockMultipartFile file = new MockMultipartFile("attachment", "any-name.json", "text/plain", "{\"file\":\"data\"}".getBytes());
        final MockMultipartFile name = new MockMultipartFile("name", "name", "text/plain", DATA_COLLECTION_NAME.getBytes());
        final MockMultipartFile type = new MockMultipartFile("type", "type", "text/plain", "JSON".getBytes());
        final URI uri = UriComponentsBuilder.fromUriString(DataCollectionController.BASE_NAME).build().encode().toUri();
        mockMvc.perform(MockMvcRequestBuilders.multipart(uri)
                .file(file)
                .file(name)
                .file(type)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("name").value(DATA_COLLECTION_NAME));

        final Optional<DataCollectionEntity> existingDataCollectionEntity = dataCollectionRepository.findByName(DATA_COLLECTION_NAME);
        assertTrue(existingDataCollectionEntity.isPresent());

        //CREATE TEMPLATE
        final TemplateCreateRequestDTO request = objectMapper.readValue(new File("src/test/resources/test-data/templates/template-create-request.json"), TemplateCreateRequestDTO.class);
        request.setDataCollectionName(DATA_COLLECTION_NAME);
        mockMvc.perform(post(TemplateController.BASE_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("dataCollection").value(DATA_COLLECTION_NAME));
        final Optional<TemplateEntity> template = templateRepository.findByName(request.getName());
        assertTrue(template.isPresent());
        final TemplateEntity existingTemplate = template.get();

        //CREATE RESOURCE
        final URI createFontURI = UriComponentsBuilder.fromUriString(ResourceController.BASE_NAME+FONTS_ENDPOINT).build().encode().toUri();
        mockMvc.perform(MockMvcRequestBuilders.multipart(createFontURI)
                .file(NAME_PART)
                .file(FONT_TYPE_PART)
                .file(REGULAR_FONT_FILE_PART)
                .file(BOLD_FONT_FILE_PART)
                .file(ITALIC_FONT_FILE_PART)
                .file(BOLD_ITALIC_FILE_PART)
                .file(IMAGE_TYPE_PART)
                .contentType(MediaType.MULTIPART_FORM_DATA));
        final Optional<ResourceEntity> createdResource = resourceRepository.findByNameAndType(NAME, ResourceTypeEnum.FONT);
        assertTrue(createdResource.isPresent());

        final ResourceEntity createdResourceEntity = createdResource.get();
        final ResourceFileEntity latestFile = resourceFileRepository.findFirstByResource_IdOrderByVersionDesc(createdResourceEntity.getId());
        existingTemplate.getLatestFile().getResourceFiles().addAll(Collections.singleton(latestFile));
        templateFileRepository.save(existingTemplate.getLatestFile());

        //GET RESOURCE DEPENDENCIES
        mockMvc.perform(
                get(ResourceController.BASE_NAME + ResourceController.RESOURCE_DEPENDENCIES_ENDPOINT_WITH_PATH_VARIABLE, FONTS, Base64.getEncoder().encodeToString(NAME.getBytes())))
                .andExpect(status().isOk())
                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()))
                .andExpect(jsonPath("$").value(hasSize(1)))
                .andExpect(jsonPath("$[0].name").value(existingTemplate.getName()))
                .andExpect(jsonPath("$[0].version").value(1))
                .andExpect(jsonPath("$[0].dependencyType").value(TEMPLATE.toString()))
                .andExpect(jsonPath("$[0].stage").isEmpty())
                .andExpect(jsonPath("$[0].directionType").value(HARD.toString()));
    }

    @Test
    void test_create_get_update_delete_image() throws Exception {
        final URI uri = UriComponentsBuilder.fromUriString(ResourceController.BASE_NAME).build().encode().toUri();
        //Create
        mockMvc.perform(MockMvcRequestBuilders.multipart(uri)
                .file(IMAGE_FILE_PART)
                .file(NAME_PART)
                .file(IMAGE_TYPE_PART)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("name").value(NAME))
                .andExpect(jsonPath("type").value(IMAGE_TYPE))
                .andExpect(jsonPath("createdOn").isNotEmpty())
                .andExpect(jsonPath("modifiedBy").isNotEmpty());
        Long createdResourceId = resourceRepository.findByName(NAME).getId();
        assertNotNull(resourceFileRepository.findFirstByResource_IdOrderByVersionDesc(createdResourceId));
        assertNotNull(resourceLogRepository.findFirstByResource_IdOrderByDateDesc(createdResourceId));

        //Create again and get conflict error
        mockMvc.perform(MockMvcRequestBuilders.multipart(uri)
                .file(IMAGE_FILE_PART)
                .file(NAME_PART)
                .file(IMAGE_TYPE_PART)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isConflict());

        //GET by name
        mockMvc.perform(
                get(ResourceController.BASE_NAME + ResourceController.RESOURCE_ENDPOINT_WITH_PATH_VARIABLE_AND_TYPE,
                        IMAGES, Base64.getEncoder().encodeToString(NAME.getBytes())))
                .andExpect(status().isOk());

        //DELETE by name
        mockMvc.perform(
                delete(ResourceController.BASE_NAME + ResourceController.RESOURCE_ENDPOINT_WITH_PATH_VARIABLE_AND_TYPE,
                        IMAGES, Base64.getEncoder().encodeToString(NAME.getBytes())))
                .andExpect(status().isOk());

        assertTrue(Objects.isNull(resourceFileRepository.findFirstByResource_IdOrderByVersionDesc(createdResourceId)));
        assertTrue(Objects.isNull(resourceLogRepository.findFirstByResource_IdOrderByDateDesc(createdResourceId)));

        //repeat DELETE by name
        mockMvc.perform(
                delete(ResourceController.BASE_NAME + ResourceController.RESOURCE_ENDPOINT_WITH_PATH_VARIABLE_AND_TYPE,
                        IMAGES, Base64.getEncoder().encodeToString(NAME.getBytes())))
                .andExpect(status().isNotFound());
    }

    @Test
    void test_create_empty_stylesheet() throws Exception {
        final String stylesheetName = "test-stylesheet";
        final String stylesheetType = "STYLESHEET";
        final MockMultipartFile TYPE_PART = new MockMultipartFile("type", "type", "text/plain", stylesheetType.getBytes());
        final MockMultipartFile NAME_PART = new MockMultipartFile("name", "name", "text/plain", stylesheetName.getBytes());
        final MockMultipartFile FILE_PART = new MockMultipartFile("resource", "any_name.css", "text/plain", "".getBytes());
        //Create
        mockMvc.perform(MockMvcRequestBuilders.multipart(ResourceController.BASE_NAME)
                .file(FILE_PART)
                .file(NAME_PART)
                .file(TYPE_PART)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("name").value(stylesheetName))
                .andExpect(jsonPath("type").value(stylesheetType))
                .andExpect(jsonPath("createdOn").isNotEmpty())
                .andExpect(jsonPath("modifiedBy").isNotEmpty())
                .andExpect(jsonPath("version").value(1));
        final Optional<ResourceEntity> createdResourceEntity = resourceRepository.findByNameAndType(stylesheetName, ResourceTypeEnum.STYLESHEET);
        assertTrue(createdResourceEntity.isPresent());
        Long createdResourceId = createdResourceEntity.get().getId();
        assertNotNull(resourceFileRepository.findFirstByResource_IdOrderByVersionDesc(createdResourceId));
        assertNotNull(resourceLogRepository.findFirstByResource_IdOrderByDateDesc(createdResourceId));

    }


    @Test
    void test_create_get_update_stylesheet() throws Exception {
        final String stylesheetName = "test-stylesheet";
        final String stylesheetType = "STYLESHEET";
        final MockMultipartFile TYPE_PART = new MockMultipartFile("type", "type", "text/plain", stylesheetType.getBytes());
        final MockMultipartFile NAME_PART = new MockMultipartFile("name", "name", "text/plain", stylesheetName.getBytes());
        final MockMultipartFile FILE_PART = new MockMultipartFile("resource", "any_name.css", "text/plain", ".h1 {\n \tfont-style: Helvetica\n }".getBytes());
        //Create
        mockMvc.perform(MockMvcRequestBuilders.multipart(ResourceController.BASE_NAME)
                .file(FILE_PART)
                .file(NAME_PART)
                .file(TYPE_PART)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("name").value(stylesheetName))
                .andExpect(jsonPath("type").value(stylesheetType))
                .andExpect(jsonPath("createdOn").isNotEmpty())
                .andExpect(jsonPath("modifiedBy").isNotEmpty())
                .andExpect(jsonPath("version").value(1));
        final Optional<ResourceEntity> createdResourceEntity = resourceRepository.findByNameAndType(stylesheetName, ResourceTypeEnum.STYLESHEET);
        assertTrue(createdResourceEntity.isPresent());
        Long createdResourceId = createdResourceEntity.get().getId();
        assertNotNull(resourceFileRepository.findFirstByResource_IdOrderByVersionDesc(createdResourceId));
        assertNotNull(resourceLogRepository.findFirstByResource_IdOrderByDateDesc(createdResourceId));

        //Create again and get conflict error
        mockMvc.perform(MockMvcRequestBuilders.multipart(ResourceController.BASE_NAME)
                .file(FILE_PART)
                .file(NAME_PART)
                .file(TYPE_PART)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isConflict());

        //GET by name
        mockMvc.perform(
                get(ResourceController.BASE_NAME + ResourceController.RESOURCE_ENDPOINT_WITH_PATH_VARIABLE_AND_TYPE,
                        STYLESHEETS, Base64.getEncoder().encodeToString(stylesheetName.getBytes())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(stylesheetName))
                .andExpect(jsonPath("type").value(stylesheetType))
                .andExpect(jsonPath("createdOn").isNotEmpty())
                .andExpect(jsonPath("modifiedBy").isNotEmpty())
                .andExpect(jsonPath("version").value(1));

        //UPDATE by name
        ResourceUpdateRequestDTO resourceUpdateRequestDTO = objectMapper.readValue(new File("src/test/resources/test-data/resources/stylesheet_update_metadata.json"), ResourceUpdateRequestDTO.class);

        mockMvc.perform(put(ResourceController.BASE_NAME + ResourceController.RESOURCE_ENDPOINT_WITH_PATH_VARIABLE, Base64.getEncoder().encodeToString(stylesheetName.getBytes()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resourceUpdateRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(resourceUpdateRequestDTO.getName()))
                .andExpect(jsonPath("description").value(resourceUpdateRequestDTO.getDescription()));

    }

    @Test
    void testGetDependenciesPageable() throws Exception {
        final String encodedResourceName = Base64.getEncoder().encodeToString("resource-name".getBytes());
        final String encodedResourceType = Base64.getEncoder().encodeToString(IMAGE_TYPE.getBytes());
        mockMvc.perform(get(ResourceController.BASE_NAME
                        + ResourceController.RESOURCE_DEPENDENCIES_PAGEABLE_ENDPOINT_WITH_PATH_VARIABLE, encodedResourceName,
                encodedResourceType))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldSuccessfullyCreateNewVersionOfResource() throws Exception {
        final URI createResourceURI = UriComponentsBuilder.fromUriString(ResourceController.BASE_NAME).build().encode()
                .toUri();
        final URI resourcesVersionsURI = UriComponentsBuilder
                .fromUriString(ResourceController.BASE_NAME + RESOURCE_VERSION_ENDPOINT).build().encode().toUri();
        //create resource
        mockMvc.perform(MockMvcRequestBuilders.multipart(createResourceURI)
                .file(IMAGE_FILE_PART)
                .file(NAME_PART)
                .file(IMAGE_TYPE_PART)
                .contentType(MediaType.MULTIPART_FORM_DATA));
        //create new version of resource
        mockMvc.perform(
                MockMvcRequestBuilders.multipart(resourcesVersionsURI).file(NAME_PART).file(IMAGE_FILE_PART).file(
                        IMAGE_TYPE_PART)
                        .file(getUpdateTemplateBooleanPart(true)).contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk()).andExpect(jsonPath("name").value(NAME))
                .andExpect(jsonPath("name").value(NAME))
                .andExpect(jsonPath("type").value(IMAGE_TYPE))
                .andExpect(jsonPath("createdOn").isNotEmpty())
                .andExpect(jsonPath("version").value(2L))
                .andExpect(jsonPath("$.metadataUrls[0].fileName").value(PICTURE_FILE_NAME))
                .andExpect(jsonPath("deployed").value(false));
    }

    @Test
    void shouldCreateStylesheetVersionsAndReturnThem() throws Exception {
        final String stylesheetName = "test-stylesheet";
        final String stylesheetType = "STYLESHEET";
        final MockMultipartFile TYPE_PART = new MockMultipartFile("type", "type", "text/plain", stylesheetType.getBytes());
        final MockMultipartFile NAME_PART = new MockMultipartFile("name", "name", "text/plain", stylesheetName.getBytes());
        final MockMultipartFile FILE_PART = new MockMultipartFile("resource", "any_name.css", "text/plain", ".h1 {\n \tfont-style: Helvetica\n }".getBytes());

        //create test INSTANCE
        InstancesRememberRequestDTO instancesRememberRequestDTO = objectMapper
                .readValue(new File("src/test/resources/test-data/resources/instances-create-request.json"),
                        InstancesRememberRequestDTO.class);

        mockMvc.perform(post(InstanceController.BASE_NAME)
                .content(objectMapper.writeValueAsString(instancesRememberRequestDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //create test WORKSPACE
        WorkspaceCreateRequestDTO workspaceCreateRequestDTO = objectMapper
                .readValue(new File("src/test/resources/test-data/resources/workspace-create-request.json"),
                        WorkspaceCreateRequestDTO.class);

        mockMvc.perform(post(WorkspaceController.BASE_NAME)
                .content(objectMapper.writeValueAsString(workspaceCreateRequestDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        //create TEMPLATE

        TemplateCreateRequestDTO templateCreateRequestDTO = objectMapper.readValue(new File("src/test/resources/test-data/resources/template-create-request.json"), TemplateCreateRequestDTO.class);
        mockMvc.perform(post(TemplateController.BASE_NAME)
                .content(objectMapper.writeValueAsString(templateCreateRequestDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        final Optional<TemplateEntity> template = templateRepository.findByName(templateCreateRequestDTO.getName());
        assertTrue(template.isPresent());
        final TemplateEntity existingTemplate = template.get();

        //create resource
        mockMvc.perform(MockMvcRequestBuilders.multipart(ResourceController.BASE_NAME)
                .file(FILE_PART)
                .file(NAME_PART)
                .file(TYPE_PART)
                .contentType(MediaType.MULTIPART_FORM_DATA));

        final Optional<ResourceEntity> createdResource = resourceRepository.findByNameAndType(stylesheetName, ResourceTypeEnum.STYLESHEET);
        assertTrue(createdResource.isPresent());

        final ResourceEntity createdResourceEntity = createdResource.get();
        final ResourceFileEntity latestResourceFile = resourceFileRepository.findFirstByResource_IdOrderByVersionDesc(createdResourceEntity.getId());
        existingTemplate.getLatestFile().getResourceFiles().addAll(Collections.singleton(latestResourceFile));
        templateFileRepository.save(existingTemplate.getLatestFile());

        //create new versions of resource
        for (int i = 0; i < AMOUNT_VERSIONS; i++) {
            mockMvc.perform(MockMvcRequestBuilders.multipart(ResourceController.BASE_NAME + RESOURCE_VERSION_ENDPOINT).file(NAME_PART).file(FILE_PART)
                    .file(TYPE_PART).file(getUpdateTemplateBooleanPart(true))
                    .contentType(MediaType.MULTIPART_FORM_DATA));
        }
        mockMvc.perform(
                get(ResourceController.BASE_NAME + ResourceController.RESOURCE_VERSION_ENDPOINT_WITH_PATH_VARIABLE,
                        STYLESHEETS, Base64.getEncoder().encodeToString(stylesheetName.getBytes()))).andExpect(status().isOk())
                .andExpect(jsonPath("empty").value(false))
                .andExpect(jsonPath("content").value(hasSize(6)))
                .andExpect(jsonPath("$.content[0].version").value(1))
                .andExpect(jsonPath("$.content[0].modifiedBy").isNotEmpty())
                .andExpect(jsonPath("$.content[0].modifiedOn").isNotEmpty())
                .andExpect(jsonPath("$.content[0].comment").isEmpty())
                .andExpect(jsonPath("$.content[0].stage").value("Development"))
                .andExpect(jsonPath("$.content[1].version").value(2))
                .andExpect(jsonPath("$.content[2].version").value(3))
                .andExpect(jsonPath("$.content[3].version").value(4))
                .andExpect(jsonPath("$.content[4].version").value(5))
                .andExpect(jsonPath("$.content[4].stage").value("Development"));
    }

    @Test
    void shouldCreateVersionsAndReturnThem() throws Exception {
        final URI createResourceURI = UriComponentsBuilder.fromUriString(ResourceController.BASE_NAME).build().encode()
                .toUri();
        final URI resourcesVersionsURI = UriComponentsBuilder
                .fromUriString(ResourceController.BASE_NAME + RESOURCE_VERSION_ENDPOINT).build().encode().toUri();
        //create resource
        mockMvc.perform(MockMvcRequestBuilders.multipart(createResourceURI)
                .file(IMAGE_FILE_PART)
                .file(NAME_PART)
                .file(IMAGE_TYPE_PART)
                .contentType(MediaType.MULTIPART_FORM_DATA));
        //create new versions of resource
        for (int i = 0; i <= AMOUNT_VERSIONS; i++) {
            mockMvc.perform(MockMvcRequestBuilders.multipart(resourcesVersionsURI).file(NAME_PART).file(IMAGE_FILE_PART)
                    .file(IMAGE_TYPE_PART).file(getUpdateTemplateBooleanPart(true))
                    .contentType(MediaType.MULTIPART_FORM_DATA));
        }
        mockMvc.perform(
                get(ResourceController.BASE_NAME + ResourceController.RESOURCE_VERSION_ENDPOINT_WITH_PATH_VARIABLE,
                        IMAGES, Base64.getEncoder().encodeToString(NAME.getBytes()))).andExpect(status().isOk())
                .andExpect(jsonPath("empty").value(false))
                .andExpect(jsonPath("$.content[0].version").value(1))
                .andExpect(jsonPath("$.content[0].modifiedBy").isNotEmpty())
                .andExpect(jsonPath("$.content[0].modifiedOn").isNotEmpty())
                .andExpect(jsonPath("$.content[0].comment").isEmpty())
                .andExpect(jsonPath("$.content[0].stage").isEmpty())
                .andExpect(jsonPath("$.content[1].version").value(2))
                .andExpect(jsonPath("$.content[2].version").value(3))
                .andExpect(jsonPath("$.content[3].version").value(4))
                .andExpect(jsonPath("$.content[4].version").value(5));
    }

    @Test
    void test_failure_get() throws Exception {
        final String notExistingResourceName = "unknown-resource";
        mockMvc.perform(
                get(ResourceController.BASE_NAME + ResourceController.RESOURCE_VERSION_ENDPOINT_WITH_PATH_VARIABLE,
                        IMAGES, Base64.getEncoder().encodeToString(notExistingResourceName.getBytes())))
                .andExpect(status().isNotFound());

    }

    @Test
    public void shouldDropBadRequestWhenFileTypeUnknown() throws Exception {
        final MockMultipartFile wrongFile = new MockMultipartFile("resource", "any-name.dtf", "text/plain",
                "{\"file\":\"data\"}".getBytes());
        final URI uri = UriComponentsBuilder.fromUriString(ResourceController.BASE_NAME).build().encode().toUri();
        mockMvc.perform(MockMvcRequestBuilders.multipart(uri)
                .file(wrongFile)
                .file(NAME_PART)
                .file(IMAGE_TYPE_PART)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnConflictResponseWhenResourceAlreadyExist() throws Exception {
        final URI uri = UriComponentsBuilder.fromUriString(ResourceController.BASE_NAME).build().encode().toUri();
        //Create
        mockMvc.perform(MockMvcRequestBuilders.multipart(uri)
                .file(IMAGE_FILE_PART)
                .file(NAME_PART)
                .file(IMAGE_TYPE_PART)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("name").value(NAME))
                .andExpect(jsonPath("type").value("IMAGE"))
                .andExpect(jsonPath("createdOn").isNotEmpty())
                .andExpect(jsonPath("modifiedBy").isNotEmpty());
        assertNotNull(resourceRepository.findByName(NAME));

        //Create again and get conflict error
        mockMvc.perform(MockMvcRequestBuilders.multipart(uri)
                .file(IMAGE_FILE_PART)
                .file(NAME_PART)
                .file(IMAGE_TYPE_PART)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturnSuccessfullyResourceDependencies() throws Exception {
        //CREATE DATA COLLECTION
        final MockMultipartFile file = new MockMultipartFile("attachment", "any-name.json", "text/plain", "{\"file\":\"data\"}".getBytes());
        final MockMultipartFile name = new MockMultipartFile("name", "name", "text/plain", DATA_COLLECTION_NAME.getBytes());
        final MockMultipartFile type = new MockMultipartFile("type", "type", "text/plain", "JSON".getBytes());
        final URI uri = UriComponentsBuilder.fromUriString(DataCollectionController.BASE_NAME).build().encode().toUri();
        mockMvc.perform(MockMvcRequestBuilders.multipart(uri)
                .file(file)
                .file(name)
                .file(type)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("name").value(DATA_COLLECTION_NAME));

        final Optional<DataCollectionEntity> existingDataCollectionEntity = dataCollectionRepository.findByName(DATA_COLLECTION_NAME);
        assertTrue(existingDataCollectionEntity.isPresent());

        //CREATE TEMPLATE
        final TemplateCreateRequestDTO request = objectMapper.readValue(new File("src/test/resources/test-data/templates/template-create-request.json"), TemplateCreateRequestDTO.class);
        request.setDataCollectionName(DATA_COLLECTION_NAME);
        mockMvc.perform(post(TemplateController.BASE_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("dataCollection").value(DATA_COLLECTION_NAME));
        final Optional<TemplateEntity> template = templateRepository.findByName(request.getName());
        assertTrue(template.isPresent());
        final TemplateEntity existingTemplate = template.get();

        //CREATE RESOURCE
        final URI createResourceURI = UriComponentsBuilder.fromUriString(ResourceController.BASE_NAME).build().encode().toUri();
        final URI resourcesVersionsURI = UriComponentsBuilder.fromUriString(ResourceController.BASE_NAME + RESOURCE_VERSION_ENDPOINT).build().encode().toUri();
        mockMvc.perform(MockMvcRequestBuilders.multipart(createResourceURI)
                .file(IMAGE_FILE_PART)
                .file(NAME_PART)
                .file(IMAGE_TYPE_PART)
                .contentType(MediaType.MULTIPART_FORM_DATA)).andExpect(status().isCreated());

        final Optional<ResourceEntity> createdResource = resourceRepository.findByNameAndType(NAME, ResourceTypeEnum.IMAGE);
        assertTrue(createdResource.isPresent());

        final ResourceEntity createdResourceEntity = createdResource.get();
        final ResourceFileEntity latestFile = resourceFileRepository.findFirstByResource_IdOrderByVersionDesc(createdResourceEntity.getId());
        existingTemplate.getLatestFile().getResourceFiles().addAll(Collections.singleton(latestFile));
        templateFileRepository.save(existingTemplate.getLatestFile());

        //create new version of resource
        mockMvc.perform(
                MockMvcRequestBuilders.multipart(resourcesVersionsURI).file(NAME_PART).file(IMAGE_FILE_PART).file(
                        IMAGE_TYPE_PART)
                        .file(getUpdateTemplateBooleanPart(true)).contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk()).andExpect(jsonPath("name").value(NAME))
                .andExpect(jsonPath("name").value(NAME))
                .andExpect(jsonPath("type").value(IMAGE_TYPE))
                .andExpect(jsonPath("version").value(2));

        //GET RESOURCE DEPENDENCIES
        mockMvc.perform(
                get(ResourceController.BASE_NAME + ResourceController.RESOURCE_DEPENDENCIES_ENDPOINT_WITH_PATH_VARIABLE, IMAGES, Base64.getEncoder().encodeToString(NAME.getBytes())))
                .andExpect(status().isOk())
                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()))
                .andExpect(jsonPath("$").value(hasSize(1)))
                .andExpect(jsonPath("$[0].name").value(existingTemplate.getName()))
                .andExpect(jsonPath("$[0].version").value(2))
                .andExpect(jsonPath("$[0].dependencyType").value(TEMPLATE.toString()))
                .andExpect(jsonPath("$[0].stage").isEmpty())
                .andExpect(jsonPath("$[0].directionType").value(HARD.toString()));
    }
}

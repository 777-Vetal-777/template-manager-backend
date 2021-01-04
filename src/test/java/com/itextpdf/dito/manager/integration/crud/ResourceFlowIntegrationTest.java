package com.itextpdf.dito.manager.integration.crud;

import com.itextpdf.dito.manager.controller.resource.ResourceController;
import com.itextpdf.dito.manager.integration.AbstractIntegrationTest;
import com.itextpdf.dito.manager.repository.resource.ResourceFileRepository;
import com.itextpdf.dito.manager.repository.resource.ResourceLogRepository;
import com.itextpdf.dito.manager.repository.resource.ResourceRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.constraints.AssertTrue;
import java.net.URI;
import java.util.Base64;
import java.util.Objects;

import static com.itextpdf.dito.manager.controller.resource.ResourceController.RESOURCE_VERSION_ENDPOINT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ResourceFlowIntegrationTest extends AbstractIntegrationTest {
    private static final String NAME = "test-image";
    private static final String TYPE = "IMAGE";
    private static final String IMAGES = "images";
    private static final String FILE_NAME = "any-name.png";
    private static final Integer AMOUNT_VERSIONS = 5;
    private static final String AUTHOR_NAME = "admin admin";
    private static final MockMultipartFile FILE_PART = new MockMultipartFile("resource", FILE_NAME, "text/plain", "{\"file\":\"data\"}".getBytes());
    private static final MockMultipartFile NAME_PART = new MockMultipartFile("name", "name", "text/plain", NAME.getBytes());
    private static final MockMultipartFile TYPE_PART = new MockMultipartFile("type", "type", "text/plain", TYPE.getBytes());

    @Autowired
    private ResourceRepository resourceRepository;
    @Autowired
    private ResourceFileRepository resourceFileRepository;
    @Autowired
    private ResourceLogRepository resourceLogRepository;

    @AfterEach
    public void tearDown() {
        resourceRepository.deleteAll();

    }

    private MockMultipartFile getUpdateTemplateBooleanPart(Boolean updateTemplate) {
        return new MockMultipartFile("updateTemplate", "updateTemplate", "application/json", updateTemplate.toString().getBytes());
    }

    @Test
    public void test_create_get_update_delete() throws Exception {
        final URI uri = UriComponentsBuilder.fromUriString(ResourceController.BASE_NAME).build().encode().toUri();
        //Create
        mockMvc.perform(MockMvcRequestBuilders.multipart(uri)
                .file(FILE_PART)
                .file(NAME_PART)
                .file(TYPE_PART)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("name").value(NAME))
                .andExpect(jsonPath("type").value("IMAGE"))
                .andExpect(jsonPath("createdOn").isNotEmpty())
                .andExpect(jsonPath("modifiedBy").isNotEmpty());
        Long createdResourceId = resourceRepository.findByName(NAME).getId();
        assertNotNull(resourceFileRepository.findFirstByResource_IdOrderByVersionDesc(createdResourceId));
        assertNotNull(resourceLogRepository.findFirstByResource_IdOrderByDateDesc(createdResourceId));

        //Create again and get conflict error
        mockMvc.perform(MockMvcRequestBuilders.multipart(uri)
                .file(FILE_PART)
                .file(NAME_PART)
                .file(TYPE_PART)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isConflict());

        //GET by name
        mockMvc.perform(get(ResourceController.BASE_NAME + ResourceController.RESOURCE_ENDPOINT_WITH_PATH_VARIABLE_AND_TYPE,
                IMAGES, Base64.getEncoder().encodeToString(NAME.getBytes())))
                .andExpect(status().isOk());

        //DELETE by name
        mockMvc.perform(delete(ResourceController.BASE_NAME + ResourceController.RESOURCE_ENDPOINT_WITH_PATH_VARIABLE_AND_TYPE,
                IMAGES, Base64.getEncoder().encodeToString(NAME.getBytes())))
                .andExpect(status().isOk());

        assertTrue(Objects.isNull(resourceFileRepository.findFirstByResource_IdOrderByVersionDesc(createdResourceId)));
        assertTrue(Objects.isNull(resourceLogRepository.findFirstByResource_IdOrderByDateDesc(createdResourceId)));

        //repeat DELETE by name
        mockMvc.perform(delete(ResourceController.BASE_NAME + ResourceController.RESOURCE_ENDPOINT_WITH_PATH_VARIABLE_AND_TYPE,
                IMAGES, Base64.getEncoder().encodeToString(NAME.getBytes())))
                .andExpect(status().isNotFound());


    }

    @Test
    public void testGetDependenciesPageable() throws Exception {
        final String encodedResourceName = Base64.getEncoder().encodeToString("resource-name".getBytes());
        final String encodedResourceType = Base64.getEncoder().encodeToString(TYPE.getBytes());
        mockMvc.perform(get(ResourceController.BASE_NAME + ResourceController.RESOURCE_DEPENDENCIES_PAGEABLE_ENDPOINT_WITH_PATH_VARIABLE, encodedResourceName, encodedResourceType))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldSuccessfullyCreateNewVersionOfResource() throws Exception {
        final URI createResourceURI = UriComponentsBuilder.fromUriString(ResourceController.BASE_NAME).build().encode().toUri();
        final URI resourcesVersionsURI = UriComponentsBuilder.fromUriString(ResourceController.BASE_NAME + RESOURCE_VERSION_ENDPOINT).build().encode().toUri();
        //create resource
        mockMvc.perform(MockMvcRequestBuilders.multipart(createResourceURI)
                .file(FILE_PART)
                .file(NAME_PART)
                .file(TYPE_PART)
                .contentType(MediaType.MULTIPART_FORM_DATA));
        //create new version of resource
        mockMvc.perform(MockMvcRequestBuilders.multipart(resourcesVersionsURI).file(NAME_PART).file(FILE_PART).file(TYPE_PART).file(getUpdateTemplateBooleanPart(true)).contentType(MediaType.MULTIPART_FORM_DATA)).andExpect(status().isOk()).andExpect(jsonPath("name").value(NAME))
                .andExpect(jsonPath("name").value(NAME))
                .andExpect(jsonPath("type").value(TYPE))
                .andExpect(jsonPath("createdOn").isNotEmpty())
                .andExpect(jsonPath("version").value(2L))
                .andExpect(jsonPath("fileName").value(FILE_NAME))
                .andExpect(jsonPath("deployed").value(false));
    }

    @Test
    public void shouldCreateVersionsAndReturnThem() throws Exception {
        final URI createResourceURI = UriComponentsBuilder.fromUriString(ResourceController.BASE_NAME).build().encode().toUri();
        final URI resourcesVersionsURI = UriComponentsBuilder.fromUriString(ResourceController.BASE_NAME + RESOURCE_VERSION_ENDPOINT).build().encode().toUri();
        //create resource
        mockMvc.perform(MockMvcRequestBuilders.multipart(createResourceURI)
                .file(FILE_PART)
                .file(NAME_PART)
                .file(TYPE_PART)
                .contentType(MediaType.MULTIPART_FORM_DATA));
        //create new versions of resource
        for (int i = 0; i <= AMOUNT_VERSIONS; i++) {
            mockMvc.perform(MockMvcRequestBuilders.multipart(resourcesVersionsURI).file(NAME_PART).file(FILE_PART).file(TYPE_PART).file(getUpdateTemplateBooleanPart(true)).contentType(MediaType.MULTIPART_FORM_DATA));
        }
        mockMvc.perform(get(ResourceController.BASE_NAME + ResourceController.RESOURCE_VERSION_ENDPOINT_WITH_PATH_VARIABLE, IMAGES, Base64.getEncoder().encodeToString(NAME.getBytes()))).andExpect(status().isOk())
                .andExpect(jsonPath("empty").value(false))
                .andExpect(jsonPath("$.content[0].version").value(1))
                .andExpect(jsonPath("$.content[0].modifiedBy").value(AUTHOR_NAME))
                .andExpect(jsonPath("$.content[0].modifiedOn").isNotEmpty())
                .andExpect(jsonPath("$.content[0].comment").isEmpty())
                .andExpect(jsonPath("$.content[0].deployed").value(false))
                .andExpect(jsonPath("$.content[1].version").value(2))
                .andExpect(jsonPath("$.content[2].version").value(3))
                .andExpect(jsonPath("$.content[3].version").value(4))
                .andExpect(jsonPath("$.content[4].version").value(5));
    }

    @Test
    public void test_failure_get() throws Exception {
        final String notExistingResourceName = "unknown-resource";
        mockMvc.perform(get(ResourceController.BASE_NAME + ResourceController.RESOURCE_VERSION_ENDPOINT_WITH_PATH_VARIABLE,
                IMAGES, Base64.getEncoder().encodeToString(notExistingResourceName.getBytes())))
                .andExpect(status().isNotFound());

    }

    @Test
    public void shouldDropBadRequestWhenFileTypeUnknown() throws Exception {
        final MockMultipartFile wrongFile = new MockMultipartFile("resource", "any-name.dtf", "text/plain", "{\"file\":\"data\"}".getBytes());
        final URI uri = UriComponentsBuilder.fromUriString(ResourceController.BASE_NAME).build().encode().toUri();
        mockMvc.perform(MockMvcRequestBuilders.multipart(uri)
                .file(wrongFile)
                .file(NAME_PART)
                .file(TYPE_PART)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnConflictResponceWhenResourceAlreadyExist() throws Exception {
        final URI uri = UriComponentsBuilder.fromUriString(ResourceController.BASE_NAME).build().encode().toUri();
        //Create
        mockMvc.perform(MockMvcRequestBuilders.multipart(uri)
                .file(FILE_PART)
                .file(NAME_PART)
                .file(TYPE_PART)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("name").value(NAME))
                .andExpect(jsonPath("type").value("IMAGE"))
                .andExpect(jsonPath("createdOn").isNotEmpty())
                .andExpect(jsonPath("modifiedBy").isNotEmpty());
        assertNotNull(resourceRepository.findByName(NAME));

        //Create again and get conflict error
        mockMvc.perform(MockMvcRequestBuilders.multipart(uri)
                .file(FILE_PART)
                .file(NAME_PART)
                .file(TYPE_PART)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isConflict());
    }
}

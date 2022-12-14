package com.itextpdf.dito.manager.integration.crud;

import com.itextpdf.dito.manager.controller.datacollection.DataCollectionController;
import com.itextpdf.dito.manager.controller.template.TemplateController;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionType;
import com.itextpdf.dito.manager.dto.datacollection.update.DataCollectionUpdateRequestDTO;
import com.itextpdf.dito.manager.dto.template.create.TemplateCreateRequestDTO;
import com.itextpdf.dito.manager.entity.RoleEntity;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.exception.template.TemplateNotFoundException;
import com.itextpdf.dito.manager.filter.datacollection.DataCollectionPermissionFilter;
import com.itextpdf.dito.manager.integration.AbstractIntegrationTest;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionLogRepository;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionRepository;
import com.itextpdf.dito.manager.repository.role.RoleRepository;
import com.itextpdf.dito.manager.repository.template.TemplateRepository;
import com.itextpdf.dito.manager.repository.user.UserRepository;
import com.itextpdf.dito.manager.service.datacollection.DataCollectionService;

import com.itextpdf.dito.manager.service.user.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.itextpdf.dito.manager.controller.datacollection.DataCollectionController.VERSIONS_ENDPOINT;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DataCollectionFlowIntegrationTest extends AbstractIntegrationTest {

    private static final String NAME = "test-data-collection";
    private static final String TYPE = "JSON";
    private static final String CUSTOM_USER_EMAIL = "dataCollectionPermissionUser@email.com";
    private static final String CUSTOM_USER_PASSWORD = "password2";

    @Autowired
    private DataCollectionRepository dataCollectionRepository;
    @Autowired
    private DataCollectionLogRepository dataCollectionLogRepository;
    @Autowired
    private TemplateRepository templateRepository;
    @Autowired
    private DataCollectionService dataCollectionService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserService userService;

    @AfterEach
    void clearDb() {
        templateRepository.deleteAll();
        dataCollectionRepository.deleteAll();
        userRepository.findByEmail(CUSTOM_USER_EMAIL).ifPresent(userRepository::delete);
    }

    @BeforeEach
    void initDb() {
        final RoleEntity roleEntity = roleRepository.findByNameAndMasterTrue("TEMPLATE_DESIGNER").get();
        UserEntity user2 = new UserEntity();
        user2.setEmail(CUSTOM_USER_EMAIL);
        user2.setFirstName("Geoffrey");
        user2.setLastName("Grant");
        user2.setPassword(CUSTOM_USER_PASSWORD);
        user2.setRoles(Set.of(roleEntity));
        user2.setActive(Boolean.TRUE);
        user2.setPasswordUpdatedByAdmin(Boolean.FALSE);

        userRepository.save(user2);
    }

    @Test
    void shouldThrowInvalidJsonWhenOnCreate() throws Exception{
        final MockMultipartFile file = new MockMultipartFile("attachment", "any-name.json", "text/plain", "{bad_datacollection[[][][{{\"file\":\"data\"}".getBytes());
        final MockMultipartFile name = new MockMultipartFile("name", "name", "text/plain", NAME.getBytes());
        final MockMultipartFile type = new MockMultipartFile("type", "type", "text/plain", "JSON".getBytes());

        final MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.multipart(DataCollectionController.BASE_NAME)
                .file(file)
                .file(name)
                .file(type)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value("Data collection is not valid."))
                .andReturn();

        assertNotNull(mvcResult.getResponse());
    }

    @Test
    void shouldDropExceptionWhenTemplateUsesDataCollection() throws Exception {
        //CREATE DATA COLLECTION
        final MockMultipartFile file = new MockMultipartFile("attachment", "any-name.json", "text/plain",
                "{\"file\":\"data\"}".getBytes());
        final MockMultipartFile name = new MockMultipartFile("name", "name", "text/plain", NAME.getBytes());
        final MockMultipartFile type = new MockMultipartFile("type", "type", "text/plain", "JSON".getBytes());
        final URI uri = UriComponentsBuilder.fromUriString(DataCollectionController.BASE_NAME).build().encode().toUri();
        mockMvc.perform(MockMvcRequestBuilders.multipart(uri)
                .file(file)
                .file(name)
                .file(type)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("name").value(NAME));

        final Optional<DataCollectionEntity> existingDataCollectionEntity = dataCollectionRepository.findByName(NAME);
        assertTrue(existingDataCollectionEntity.isPresent());
        //CREATE TEMPLATE
        final TemplateCreateRequestDTO request = objectMapper
                .readValue(new File("src/test/resources/test-data/templates/template-create-request.json"),
                        TemplateCreateRequestDTO.class);
        request.setDataCollectionName(NAME);
        mockMvc.perform(post(TemplateController.BASE_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("dataCollection").value(NAME));
        final Optional<TemplateEntity> template = templateRepository.findByName(request.getName());
        assertTrue(template.isPresent());
        assertNotNull(dataCollectionService.getByTemplateName("some-template"));
        //DELETE by name
        mockMvc.perform(
                delete(DataCollectionController.BASE_NAME + "/" + Base64.getEncoder().encodeToString(NAME.getBytes())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("message").value("Data collection has outbound dependencies"));
        assertTrue(dataCollectionRepository.existsByName(NAME));
    }

    @Test
    public void shouldCreateAndRollbackVersionOfDataCollection() throws Exception {
        //CREATE NEW DATA COLLECTION
        final MockMultipartFile file = new MockMultipartFile("attachment", "any-name.json", "text/plain",
                "{\"file\":\"data\"}".getBytes());
        final MockMultipartFile name = new MockMultipartFile("name", "name", "text/plain", NAME.getBytes());
        final MockMultipartFile type = new MockMultipartFile("type", "type", "text/plain", TYPE.getBytes());
        final URI uri = UriComponentsBuilder.fromUriString(DataCollectionController.BASE_NAME).build().encode().toUri();
        final URI newVersionURI = UriComponentsBuilder
                .fromUriString(DataCollectionController.BASE_NAME + VERSIONS_ENDPOINT).build().encode().toUri();
        mockMvc.perform(MockMvcRequestBuilders.multipart(uri)
                .file(file)
                .file(name)
                .file(type)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("name").value(NAME))
                .andExpect(jsonPath("version").value(1))
                .andExpect(jsonPath("type").value("JSON"))
                .andExpect(jsonPath("modifiedOn").isNotEmpty())
                .andExpect(jsonPath("description").isEmpty())
                .andExpect(jsonPath("fileName").value(file.getOriginalFilename()))
                .andExpect(jsonPath("createdOn").isNotEmpty());

        assertTrue(dataCollectionRepository.existsByName(NAME));
        //CREATE NEW VERSION
        mockMvc.perform(MockMvcRequestBuilders.multipart(newVersionURI)
                .file(name)
                .file(type)
                .file(file)
                .with(user("admin@email.com").authorities(new SimpleGrantedAuthority("E6_US35_CREATE_A_NEW_VERSION_OF_DATA_COLLECTION_USING_JSON")))
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("name").value(NAME))
                .andExpect(jsonPath("version").value(2))
                .andExpect(jsonPath("type").value("JSON"))
                .andExpect(jsonPath("modifiedOn").isNotEmpty())
                .andExpect(jsonPath("description").isEmpty())
                .andExpect(jsonPath("fileName").value(file.getOriginalFilename()))
                .andExpect(jsonPath("createdOn").isNotEmpty());

        //ROLLBACK VERSION failure
        mockMvc.perform(post(DataCollectionController.BASE_NAME + DataCollectionController.DATA_COLLECTION_ROLLBACK_ENDPOINT_WITH_PATH_VARIABLE, encodeStringToBase64(NAME), 100L))
                .andExpect(status().isNotFound());

        //ROLLBACK VERSION success
        final Long currentVersion = 2L;
        mockMvc.perform(post(DataCollectionController.BASE_NAME + DataCollectionController.DATA_COLLECTION_ROLLBACK_ENDPOINT_WITH_PATH_VARIABLE, encodeStringToBase64(NAME), currentVersion))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(NAME))
                .andExpect(jsonPath("version").value(3))
                .andExpect(jsonPath("type").value("JSON"))
                .andExpect(jsonPath("modifiedOn").isNotEmpty())
                .andExpect(jsonPath("comment").value("Rollback to version: 2"))
                .andExpect(jsonPath("fileName").value(file.getOriginalFilename()))
                .andExpect(jsonPath("createdOn").isNotEmpty());
    }

    @Test
    public void test_success_createAndGet() throws Exception {
        //CREATE
        final MockMultipartFile file = new MockMultipartFile("attachment", "any-name.json", "text/plain",
                "{\"file\":\"data\"}".getBytes());
        final MockMultipartFile name = new MockMultipartFile("name", "name", "text/plain", NAME.getBytes());
        final MockMultipartFile type = new MockMultipartFile("type", "type", "text/plain", TYPE.getBytes());
        final URI uri = UriComponentsBuilder.fromUriString(DataCollectionController.BASE_NAME).build().encode().toUri();

        mockMvc.perform(MockMvcRequestBuilders.multipart(uri)
                .file(file)
                .file(name)
                .file(type)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("name").value(NAME))
                .andExpect(jsonPath("type").value("JSON"))
                .andExpect(jsonPath("modifiedOn").isNotEmpty())
                .andExpect(jsonPath("description").isEmpty())
                .andExpect(jsonPath("fileName").value(file.getOriginalFilename()))
                .andExpect(jsonPath("createdOn").isNotEmpty());
        assertTrue(dataCollectionRepository.existsByName(NAME));

        final MockMultipartFile multipartFile = new MockMultipartFile("attachment", "any-name.json", "text/plain", "".getBytes());
        mockMvc.perform(MockMvcRequestBuilders.multipart(uri)
                .file(multipartFile)
                .file(name)
                .file(type)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());

        final MockMultipartFile multipartFile2 = new MockMultipartFile("attachment", "any-name.json", "text/plain", "{123A".getBytes());
        final MockMultipartFile name2 = new MockMultipartFile("name2", "name2", "text/plain", NAME.getBytes());
        mockMvc.perform(MockMvcRequestBuilders.multipart(uri)
                .file(multipartFile2)
                .file(name2)
                .file(type)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());

        //GET by name
        mockMvc.perform(
                get(DataCollectionController.BASE_NAME + "/" + Base64.getEncoder().encodeToString(NAME.getBytes()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //Get List
        mockMvc.perform(
                get(DataCollectionController.BASE_NAME + "/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("modifiedOn", "10/02/2021")
                        .param("modifiedOn", "10/02/2021"))
                .andExpect(status().isOk());

        //get roles
        final DataCollectionPermissionFilter filter = new DataCollectionPermissionFilter();
        final List<String> list = new ArrayList<>();
        list.add("name");
        filter.setName(list);
        final Pageable pageable = PageRequest.of(0, 8);

        //UPDATE by name
        final String newCollectionName = "new_collectionName";

        final String encodedCollectionName = Base64.getEncoder().encodeToString(NAME.getBytes());
        final DataCollectionUpdateRequestDTO collectionUpdateRequestDTO = new DataCollectionUpdateRequestDTO();
        collectionUpdateRequestDTO.setType(DataCollectionType.valueOf(TYPE));
        collectionUpdateRequestDTO.setName(newCollectionName);
        collectionUpdateRequestDTO.setDescription("new description");

        mockMvc.perform(patch(DataCollectionController.BASE_NAME + DataCollectionController.DATA_COLLECTION_WITH_PATH_VARIABLE, encodedCollectionName)
                .content(objectMapper.writeValueAsString(collectionUpdateRequestDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("description").value(collectionUpdateRequestDTO.getDescription()))
                .andExpect(jsonPath("name").value(collectionUpdateRequestDTO.getName()))
                .andExpect(jsonPath("createdOn").isNotEmpty())
                .andExpect(jsonPath("type").value("JSON"))
                .andExpect(jsonPath("modifiedOn").isNotEmpty());
        assertFalse(dataCollectionLogRepository.findAll().isEmpty());

        //DELETE by name
        mockMvc.perform(delete(DataCollectionController.BASE_NAME + "/" + Base64.getEncoder()
                .encodeToString(collectionUpdateRequestDTO.getName().getBytes())))
                .andExpect(status().isOk());
        assertFalse(dataCollectionRepository.existsByName(NAME));
        assertTrue(dataCollectionLogRepository.findAll().isEmpty());

    }

    @Test
    public void shouldDropNoSuchDataCollectionTypeWhenTypeUnknown() throws Exception {
        final MockMultipartFile file = new MockMultipartFile("attachment", "any-name.json", "text/plain",
                "{\"file\":\"data\"}".getBytes());
        final MockMultipartFile name = new MockMultipartFile("name", "name", "text/plain", NAME.getBytes());
        final MockMultipartFile type = new MockMultipartFile("type", "type", "text/plain", "WRONG".getBytes());
        final URI uri = UriComponentsBuilder.fromUriString(DataCollectionController.BASE_NAME).build().encode().toUri();

        mockMvc.perform(MockMvcRequestBuilders.multipart(uri)
                .file(file)
                .file(name)
                .file(type)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
        assertFalse(dataCollectionRepository.existsByName(NAME));
    }

    @Test
    public void create_WhenCollectionsWithSameNameAlreadyExists_ThenResponseIsBadRequest() throws Exception {
        final MockMultipartFile file = new MockMultipartFile("attachment", "any-name.json", "text/plain",
                "{\"file\":\"data\"}".getBytes());
        final MockMultipartFile name = new MockMultipartFile("name", "name", "text/plain", NAME.getBytes());
        final MockMultipartFile type = new MockMultipartFile("type", "type", "text/plain", TYPE.getBytes());
        final URI uri = UriComponentsBuilder.fromUriString(DataCollectionController.BASE_NAME).build().encode().toUri();

        mockMvc.perform(MockMvcRequestBuilders.multipart(uri)
                .file(file)
                .file(name)
                .file(type)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated());

        mockMvc.perform(MockMvcRequestBuilders.multipart(uri)
                .file(file)
                .file(name)
                .file(type)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(
                        jsonPath("message").value(String.format("Data collection with id %s already exists.", NAME)));
        assertTrue(dataCollectionRepository.existsByName(NAME));
    }

    @Test
    public void testGetList() throws Exception {
        mockMvc.perform(get(DataCollectionController.BASE_NAME)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        assertTrue(dataCollectionRepository.findAll().isEmpty());
    }

    @Test
    public void test_failure_get() throws Exception {
        final String notExistingCollectionName = "unknown-collection";
        mockMvc.perform(get(DataCollectionController.BASE_NAME + "/" + Base64.getEncoder()
                .encodeToString(notExistingCollectionName.getBytes()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        assertFalse(dataCollectionRepository.existsByName(notExistingCollectionName));
    }

    @Test
    void testNoTemplateByDataCollectionException() {
        assertThrows(TemplateNotFoundException.class, () -> dataCollectionService.getByTemplateName("not-existing-template"));
    }

    @Test
    void shouldReturnPermissionsTable() throws Exception {
        //CREATE DATA COLLECTION
        final MockMultipartFile file = new MockMultipartFile("attachment", "any-name.json", "text/plain",
                "{\"file\":\"data\"}".getBytes());
        final MockMultipartFile name = new MockMultipartFile("name", "name", "text/plain", NAME.getBytes());
        final MockMultipartFile type = new MockMultipartFile("type", "type", "text/plain", "JSON".getBytes());
        final URI uri = UriComponentsBuilder.fromUriString(DataCollectionController.BASE_NAME).build().encode().toUri();
        mockMvc.perform(MockMvcRequestBuilders.multipart(uri)
                .file(file)
                .file(name)
                .file(type)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("name").value(NAME));

        final DataCollectionEntity dataCollectionEntity = dataCollectionRepository.findByName(NAME).get();
        dataCollectionService.applyRole(dataCollectionEntity.getName(), "TEMPLATE_DESIGNER", Arrays.asList("E6_US34_EDIT_DATA_COLLECTION_METADATA"));

        //GET by name
        final MvcResult mvcResult = mockMvc.perform(
                get(DataCollectionController.BASE_NAME + "/" + Base64.getEncoder().encodeToString(NAME.getBytes()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON).with(user(CUSTOM_USER_EMAIL).password(CUSTOM_USER_PASSWORD).authorities(
                        Stream.of("E6_US33_VIEW_DATA_COLLECTION_METADATA", "E6_US31_DATA_COLLECTIONS_NAVIGATION_MENU")
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList()))))
                .andExpect(jsonPath("$.permissions", hasSize(1)))
                .andExpect(status().isOk()).andReturn();

        assertNotNull(mvcResult.getResponse());
    }
}
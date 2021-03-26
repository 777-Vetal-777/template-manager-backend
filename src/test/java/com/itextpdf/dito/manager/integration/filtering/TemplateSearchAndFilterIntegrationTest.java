package com.itextpdf.dito.manager.integration.filtering;

import com.itextpdf.dito.manager.controller.template.TemplateController;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionType;
import com.itextpdf.dito.manager.dto.template.create.TemplateCreateRequestDTO;
import com.itextpdf.dito.manager.entity.RoleEntity;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.integration.AbstractIntegrationTest;
import com.itextpdf.dito.manager.repository.role.RoleRepository;
import com.itextpdf.dito.manager.repository.template.TemplateFileRepository;
import com.itextpdf.dito.manager.repository.template.TemplateRepository;
import com.itextpdf.dito.manager.repository.user.UserRepository;
import com.itextpdf.dito.manager.service.datacollection.DataCollectionService;
import com.itextpdf.dito.manager.service.template.TemplateService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MvcResult;

import java.io.File;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for filtering and search in {@link TemplateEntity} table.
 */
class TemplateSearchAndFilterIntegrationTest extends AbstractIntegrationTest implements FilterAndSearchTest {
    public static final String CUSTOM_USER_EMAIL = "templatePermissionUser@email.com";
    public static final String CUSTOM_USER_PASSWORD = "password2";

    @Autowired
    private TemplateRepository templateRepository;
    @Autowired
    private TemplateFileRepository templateFileRepository;
    @Autowired
    private DataCollectionService dataCollectionService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private TemplateService templateService;
    private TemplateCreateRequestDTO request;

    @BeforeEach
    void init() throws Exception {
        dataCollectionService.create("data-collection-test", DataCollectionType.JSON, "{\"file\":\"data\"}".getBytes(), "datacollection.json", "admin@email.com");

        request = objectMapper.readValue(new File("src/test/resources/test-data/templates/template-create-request-with-data-collection.json"), TemplateCreateRequestDTO.class);
        request.setDataCollectionName("data-collection-test");
        mockMvc.perform(post(TemplateController.BASE_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

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

    @AfterEach
    void clearDb() {
        templateRepository.deleteAll();
        templateFileRepository.deleteAll();
        dataCollectionService.delete("data-collection-test", "admin@email.com");
        userRepository.findByEmail(CUSTOM_USER_EMAIL).ifPresent(userRepository::delete);

    }

    @Test
    void getAll_WhenSortedByUnsupportedField_ThenResponseIsBadRequest() throws Exception {
        final MvcResult result = mockMvc.perform(get(TemplateController.BASE_NAME)
                .param("sort", "unsupportedField")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertNotNull(result.getResponse());
    }

    @Override
    @Test
    public void test_filtering() throws Exception {
        mockMvc.perform(get(TemplateController.BASE_NAME)
                .param("name", request.getName()))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is(request.getName())));
        mockMvc.perform(get(TemplateController.BASE_NAME)
                .param("modifiedOn", "01/01/1970")
                .param("modifiedOn", "01/01/1980"))
                .andExpect(jsonPath("$.content", hasSize(0)));
        mockMvc.perform(get(TemplateController.BASE_NAME)
                .param("type", "STANDARD"))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is(request.getName())));
        mockMvc.perform(get(TemplateController.BASE_NAME)
                .param("modifiedOn", "01/01/1970"))
                .andExpect(status().isBadRequest());
        final MvcResult result =
                mockMvc.perform(get(TemplateController.BASE_NAME)
                        .param("dataCollection", request.getDataCollectionName()))
                        .andExpect(jsonPath("$.content", hasSize(1)))
                        .andExpect(jsonPath("$.content[0].name", is(request.getName())))
                        .andReturn();
        assertNotNull(result.getResponse());
    }

    @Override
    @Test
    public void test_searchAndFiltering() throws Exception {
        mockMvc.perform(get(TemplateController.BASE_NAME)
                .param("search", "admin")
                .param("name", "unknown-template"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));

        final MvcResult result =
                mockMvc.perform(get(TemplateController.BASE_NAME)
                        .param("search", request.getName()))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.content", hasSize(1)))
                        .andExpect(jsonPath("$.content[0].name", is(request.getName())))
                        .andReturn();

        assertNotNull(result.getResponse());
    }

    @Override
    @Test
    public void test_sortWithSearch() throws Exception {
        for (String field : TemplateRepository.SUPPORTED_SORT_FIELDS) {
            final MvcResult result =
                    mockMvc.perform(get(TemplateController.BASE_NAME)
                            .param("sort", field)
                            .param("search", "template"))
                            .andExpect(status().isOk())
                            .andReturn();

            assertNotNull(result.getResponse());
        }
    }

    @Override
    @Test
    public void test_sortWithFiltering() throws Exception {
        for (String field : TemplateRepository.SUPPORTED_SORT_FIELDS) {
            final MvcResult result =
                    mockMvc.perform(get(TemplateController.BASE_NAME)
                            .param("sort", field))
                            .andExpect(status().isOk())
                            .andReturn();

            assertNotNull(result.getResponse());
        }
    }

    @Test
    void shouldReturnListOfPermissions() throws Exception {
        final TemplateEntity templateEntity = templateRepository.findByName("some-template").get();
        templateService.applyRole(templateEntity.getName(), "TEMPLATE_DESIGNER", Arrays.asList("E9_US75_EDIT_TEMPLATE_METADATA_STANDARD"), "admin@email.com");

        mockMvc.perform(get(TemplateController.BASE_NAME)
                .param("type", "STANDARD")
                .accept(MediaType.APPLICATION_JSON).with(user(CUSTOM_USER_EMAIL).password(CUSTOM_USER_PASSWORD).authorities(
                Stream.of("E9_US70_TEMPLATES_TABLE", "E9_US71_TEMPLATE_NAVIGATION_MENU_STANDARD")
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is(request.getName())))
                .andExpect(jsonPath("$.content[0].permissions", hasSize(2)));

    }

}

package com.itextpdf.dito.manager.integration.filtering;

import com.itextpdf.dito.manager.controller.datacollection.DataCollectionController;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionType;
import com.itextpdf.dito.manager.entity.RoleEntity;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.integration.AbstractIntegrationTest;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionRepository;
import com.itextpdf.dito.manager.repository.role.RoleRepository;
import com.itextpdf.dito.manager.repository.user.UserRepository;
import com.itextpdf.dito.manager.service.datacollection.DataCollectionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DataCollectionSearchAndFilterIntegrationTest extends AbstractIntegrationTest implements FilterAndSearchTest {
    private static final String NAME = "test-data-collection";
    private static final String TYPE = "JSON";
    public static final String CUSTOM_USER_EMAIL = "dataCollectionPermissionUser@email.com";
    public static final String CUSTOM_USER_PASSWORD = "password2";

    @Autowired
    private DataCollectionService dataCollectionService;
    @Autowired
    private DataCollectionRepository dataCollectionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;


    @BeforeEach
    public void init() throws Exception {
        dataCollectionService.create("data-collection-search-test", DataCollectionType.JSON, "{\"file\":\"data\"}".getBytes(), "datacollection.json", "admin@email.com");

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
    public void clearDb() {
        dataCollectionService.delete("data-collection-search-test", "admin@email.com");
        userRepository.findByEmail(CUSTOM_USER_EMAIL).ifPresent(userRepository::delete);

    }

    @Test
    public void getAll_WhenSortedByUnsupportedField_ThenResponseIsBadRequest() throws Exception {
        final MvcResult result = mockMvc.perform(get(DataCollectionController.BASE_NAME)
                .param("sort", "unsupportedField")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()).andReturn();
        assertNotNull(result.getResponse());
    }

    @Override
    @Test
    public void test_filtering() throws Exception {
        mockMvc.perform(get(DataCollectionController.BASE_NAME)
                .param("name", "data-COLLECTION-search-test"))
                .andExpect(jsonPath("$.content", hasSize(1)));
        final MvcResult result = mockMvc.perform(get(DataCollectionController.BASE_NAME)
                .param("modifiedOn", "01/01/1970")
                .param("modifiedOn", "01/01/1980"))
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andReturn();
        assertNotNull(result.getResponse());
    }

    @Override
    @Test
    public void test_searchAndFiltering() throws Exception {
        mockMvc.perform(get(DataCollectionController.BASE_NAME)
                .param("search", "data-COLLECTION-search-test"))
                .andExpect(jsonPath("$.content", hasSize(1)));

        final MvcResult result = mockMvc.perform(get(DataCollectionController.BASE_NAME)
                .param("search", "admin")
                .param("name", "unknown-template"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andReturn();
        assertNotNull(result.getResponse());
    }

    @Override
    @Test
    public void test_sortWithSearch() throws Exception {
        for (String field : DataCollectionRepository.SUPPORTED_SORT_FIELDS) {
            final MvcResult result = mockMvc.perform(get(DataCollectionController.BASE_NAME)
                    .param("sort", field)
                    .param("search", "template"))
                    .andExpect(status().isOk()).andReturn();
            assertNotNull(result.getResponse());
        }
    }

    @Override
    @Test
    public void test_sortWithFiltering() throws Exception {
        for (String field : DataCollectionRepository.SUPPORTED_SORT_FIELDS) {
            final MvcResult result = mockMvc.perform(get(DataCollectionController.BASE_NAME)
                    .param("sort", field))
                    .andExpect(status().isOk()).andReturn();
            assertNotNull(result.getResponse());
        }
    }
    @Test
    void shouldReturnPermissionsTable() throws Exception {
        final DataCollectionEntity dataCollectionEntity = dataCollectionRepository.findByName("data-collection-search-test").get();
        dataCollectionService.applyRole(dataCollectionEntity.getName(),"TEMPLATE_DESIGNER", Arrays.asList("E6_US34_EDIT_DATA_COLLECTION_METADATA"));

        mockMvc.perform(get(DataCollectionController.BASE_NAME)
                .param("search", "data-COLLECTION-search-test")
                .accept(MediaType.APPLICATION_JSON).with(user(CUSTOM_USER_EMAIL).password(CUSTOM_USER_PASSWORD).authorities(
                Stream.of("E6_US30_TABLE_OF_DATA_COLLECTIONS", "E6_US31_DATA_COLLECTIONS_NAVIGATION_MENU")
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].permissions", hasSize(1)));
    }
}

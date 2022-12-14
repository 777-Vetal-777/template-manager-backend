package com.itextpdf.dito.manager.integration.crud.permissions;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Base64;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.UriComponentsBuilder;

import com.itextpdf.dito.manager.controller.resource.ResourceController;
import com.itextpdf.dito.manager.controller.role.RoleController;
import com.itextpdf.dito.manager.dto.resource.update.ApplyRoleRequestDTO;
import com.itextpdf.dito.manager.dto.role.create.RoleCreateRequestDTO;
import com.itextpdf.dito.manager.integration.AbstractIntegrationTest;
import com.itextpdf.dito.manager.repository.resource.ResourcePermissionRepository;

public class ResourcePermissionsFlowIntegrationStylesheetTest extends AbstractIntegrationTest {

    final String resourceNameForPermissions = "resource-for-permissions";
    final String roleName = "role-for-resource-permissions-test";

    private static final String NAME = "test-stylesheet-for-permissions";
    private static final String TYPE = "STYLESHEET";
    private static final String STYLESHEETS = "stylesheets";
    private static final String FILE_NAME = "any-permission-name.css";

    private static final MockMultipartFile NAME_PART = new MockMultipartFile("name", "name", "text/css",
            NAME.getBytes());
    private static final MockMultipartFile TYPE_PART = new MockMultipartFile("type", "type", "text/css",
            TYPE.getBytes());

    @BeforeEach
    public void init() throws Exception {
        //Imitate created data collection
        final URI uri = UriComponentsBuilder.fromUriString(ResourceController.BASE_NAME).build().encode().toUri();
        //Create
        final MockMultipartFile filePart = new MockMultipartFile("resource", FILE_NAME, "text/css", Files.readAllBytes(Path.of("src/test/resources/test-data/resources/test-css.css")));
        mockMvc.perform(MockMvcRequestBuilders.multipart(uri)
                .file(filePart)
                .file(NAME_PART)
                .file(TYPE_PART)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated());

        RoleCreateRequestDTO request = objectMapper.readValue(new File("src/test/resources/test-data/resources/permissions/role-for-permissions-test-create-request.json"), RoleCreateRequestDTO.class);
        mockMvc.perform(post(RoleController.BASE_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @AfterEach
    public void tearDown() throws Exception {
        //Clear DB
        mockMvc.perform(
                delete(ResourceController.BASE_NAME + ResourceController.RESOURCE_ENDPOINT_WITH_PATH_VARIABLE_AND_TYPE,
                        STYLESHEETS, Base64.getEncoder().encodeToString(NAME.getBytes())))
                .andExpect(status().isOk());
        mockMvc.perform(delete(RoleController.BASE_NAME + "/" + encodeStringToBase64(roleName)))
                .andExpect(status().isOk());
    }

    @Test
    public void test_resourcePermissionFlow() throws Exception {
        final String encodedResourceName = encodeStringToBase64(NAME);

        //Create permission
        ApplyRoleRequestDTO applyRoleRequestDTO = objectMapper.readValue(new File("src/test/resources/test-data/resources/permissions/resource-apply-role-request.json"), ApplyRoleRequestDTO.class);
        applyRoleRequestDTO.setPermissions(Arrays.asList("E8_US66_2_DELETE_RESOURCE_STYLESHEET", "E8_US63_CREATE_NEW_VERSION_OF_RESOURCE_STYLESHEET"));
        mockMvc.perform(post(ResourceController.BASE_NAME + ResourceController.RESOURCE_APPLIED_ROLES_ENDPOINT_WITH_RESOURCE_PATH_VARIABLE, STYLESHEETS, encodedResourceName)
                .content(objectMapper.writeValueAsString(applyRoleRequestDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //Get permission
        mockMvc.perform(get(ResourceController.BASE_NAME + ResourceController.RESOURCE_APPLIED_ROLES_ENDPOINT_WITH_RESOURCE_PATH_VARIABLE, STYLESHEETS, encodedResourceName)
                .param("name", roleName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));

        //Filter permissions
        for (String field : ResourcePermissionRepository.SUPPORTED_SORT_FIELDS) {
            mockMvc.perform(get(ResourceController.BASE_NAME + ResourceController.RESOURCE_APPLIED_ROLES_ENDPOINT_WITH_RESOURCE_PATH_VARIABLE, STYLESHEETS, encodedResourceName)
                    .param("sort", field)
                    .param("name", roleName))
                    .andExpect(status().isOk());
        }

        //Search permissions
        for (String field : ResourcePermissionRepository.SUPPORTED_SORT_FIELDS) {
            mockMvc.perform(get(ResourceController.BASE_NAME + ResourceController.RESOURCE_APPLIED_ROLES_ENDPOINT_WITH_RESOURCE_PATH_VARIABLE, STYLESHEETS, encodedResourceName)
                    .param("sort", field)
                    .param("search", field)
                    .param("name", roleName))
                    .andExpect(status().isOk());
        }

        //Delete permission
        final MvcResult result = mockMvc.perform(delete(ResourceController.BASE_NAME + ResourceController.RESOURCE_APPLIED_ROLES_ENDPOINT_WITH_RESOURCE_AND_ROLE_PATH_VARIABLES, STYLESHEETS, encodedResourceName, encodeStringToBase64(roleName)))
                .andExpect(status().isOk())
                .andReturn();
        assertNotNull(result.getResponse());
    }
}

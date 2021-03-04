package com.itextpdf.dito.manager.integration.crud.permissions;

import static com.itextpdf.dito.manager.controller.resource.ResourceController.FONTS_ENDPOINT;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.net.URI;
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

public class ResourcePermissionsFlowIntegrationFontTest extends AbstractIntegrationTest {

    final String resourceNameForPermissions = "resource-for-permissions";
    final String roleName = "role-for-resource-permissions-test";

    private static final String NAME = "test-fonts-for-permissions";
    private static final String FONTS = "fonts";
    
    private static final String FONT_TYPE = "FONT";
    private static final String REGULAR_FILE_NAME = "regular.ttf";
    private static final String BOLD_FILE_NAME = "bold.ttf";
    private static final String ITALIC_FILE_NAME = "italic.ttf";
    private static final String BOLD_ITALIC_FILE_NAME = "bold_italic.ttf";
    
    private static final MockMultipartFile FONT_TYPE_PART = new MockMultipartFile("type", FONT_TYPE, "text/plain", FONT_TYPE.getBytes());
    private static final MockMultipartFile REGULAR_FONT_FILE_PART = new MockMultipartFile("regular", REGULAR_FILE_NAME, "text/plain", readFileBytes("src/test/resources/test-data/resources/random-regular.ttf"));
    private static final MockMultipartFile BOLD_FONT_FILE_PART = new MockMultipartFile("bold", BOLD_FILE_NAME, "text/plain", readFileBytes("src/test/resources/test-data/resources/random-regular.ttf"));
    private static final MockMultipartFile ITALIC_FONT_FILE_PART = new MockMultipartFile("italic", ITALIC_FILE_NAME, "text/plain", readFileBytes("src/test/resources/test-data/resources/random-regular.ttf"));
    private static final MockMultipartFile BOLD_ITALIC_FILE_PART = new MockMultipartFile("bold_italic", BOLD_ITALIC_FILE_NAME, "text/plain", readFileBytes("src/test/resources/test-data/resources/random-regular.ttf"));


    private static final MockMultipartFile NAME_PART = new MockMultipartFile("name", "name", "application/x-font-ttf",
            NAME.getBytes());

    @BeforeEach
    public void init() throws Exception {
        //Create
        final URI createFontURI = UriComponentsBuilder.fromUriString(ResourceController.BASE_NAME + FONTS_ENDPOINT).build().encode().toUri();
        mockMvc.perform(MockMvcRequestBuilders.multipart(createFontURI)
                .file(NAME_PART)
                .file(FONT_TYPE_PART)
                .file(REGULAR_FONT_FILE_PART)
                .file(BOLD_FONT_FILE_PART)
                .file(ITALIC_FONT_FILE_PART)
                .file(BOLD_ITALIC_FILE_PART)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());

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
                		FONTS, Base64.getEncoder().encodeToString(NAME.getBytes())))
                .andExpect(status().isOk());
        mockMvc.perform(delete(RoleController.BASE_NAME + "/" + encodeStringToBase64(roleName)))
                .andExpect(status().isOk());
    }

    @Test
    public void test_resourcePermissionFlow() throws Exception {
        final String encodedResourceName = encodeStringToBase64(NAME);

        //Create permission
        ApplyRoleRequestDTO applyRoleRequestDTO = objectMapper.readValue(new File("src/test/resources/test-data/resources/permissions/resource-apply-role-request.json"), ApplyRoleRequestDTO.class);
        mockMvc.perform(post(ResourceController.BASE_NAME + ResourceController.RESOURCE_APPLIED_ROLES_ENDPOINT_WITH_RESOURCE_PATH_VARIABLE, FONTS, encodedResourceName)
                .content(objectMapper.writeValueAsString(applyRoleRequestDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //Get permission
        mockMvc.perform(get(ResourceController.BASE_NAME + ResourceController.RESOURCE_APPLIED_ROLES_ENDPOINT_WITH_RESOURCE_PATH_VARIABLE, FONTS, encodedResourceName)
                .param("name", roleName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));

        //Filter permissions
        for (String field : ResourcePermissionRepository.SUPPORTED_SORT_FIELDS) {
            mockMvc.perform(get(ResourceController.BASE_NAME + ResourceController.RESOURCE_APPLIED_ROLES_ENDPOINT_WITH_RESOURCE_PATH_VARIABLE, FONTS, encodedResourceName)
                    .param("sort", field)
                    .param("name", roleName))
                    .andExpect(status().isOk());
        }

        //Search permissions
        for (String field : ResourcePermissionRepository.SUPPORTED_SORT_FIELDS) {
            mockMvc.perform(get(ResourceController.BASE_NAME + ResourceController.RESOURCE_APPLIED_ROLES_ENDPOINT_WITH_RESOURCE_PATH_VARIABLE, FONTS, encodedResourceName)
                    .param("sort", field)
                    .param("search", field)
                    .param("name", roleName))
                    .andExpect(status().isOk());
        }

        //Delete permission
        final MvcResult result = mockMvc.perform(delete(ResourceController.BASE_NAME + ResourceController.RESOURCE_APPLIED_ROLES_ENDPOINT_WITH_RESOURCE_AND_ROLE_PATH_VARIABLES, FONTS, encodedResourceName, encodeStringToBase64(roleName)))
                .andExpect(status().isOk())
                .andReturn();
        assertNotNull(result.getResponse());
    }
}

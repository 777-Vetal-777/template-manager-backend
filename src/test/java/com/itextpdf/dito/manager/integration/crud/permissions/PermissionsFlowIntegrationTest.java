package com.itextpdf.dito.manager.integration.crud.permissions;

import com.itextpdf.dito.manager.component.mapper.permission.PermissionMapper;
import com.itextpdf.dito.manager.controller.permission.PermissionController;
import com.itextpdf.dito.manager.controller.resource.ResourceController;
import com.itextpdf.dito.manager.controller.role.RoleController;
import com.itextpdf.dito.manager.dto.permission.PermissionDTO;
import com.itextpdf.dito.manager.dto.resource.update.ApplyRoleRequestDTO;
import com.itextpdf.dito.manager.dto.role.create.RoleCreateRequestDTO;
import com.itextpdf.dito.manager.entity.PermissionEntity;
import com.itextpdf.dito.manager.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.net.URI;
import java.util.Arrays;
import java.util.Base64;

import static com.itextpdf.dito.manager.controller.resource.ResourceController.FONTS_ENDPOINT;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PermissionsFlowIntegrationTest extends AbstractIntegrationTest {
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
    private static final MockMultipartFile NAME_PART = new MockMultipartFile("name", "name", "application/x-font-ttf", NAME.getBytes());
    private static final String roleName = "role-for-resource-permissions-test";
    @Autowired
    private PermissionMapper mapper;

    @BeforeEach
    void init() throws Exception {
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

        final RoleCreateRequestDTO request = objectMapper.readValue(new File("src/test/resources/test-data/resources/permissions/role-for-permissions-test-create-request.json"), RoleCreateRequestDTO.class);
        mockMvc.perform(post(RoleController.BASE_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @AfterEach
    void tearDown() throws Exception {
        //Clear DB
        mockMvc.perform(
                delete(ResourceController.BASE_NAME + ResourceController.RESOURCE_ENDPOINT_WITH_PATH_VARIABLE_AND_TYPE,
                        FONTS, Base64.getEncoder().encodeToString(NAME.getBytes())))
                .andExpect(status().isOk());
        mockMvc.perform(delete(RoleController.BASE_NAME + "/" + encodeStringToBase64(roleName)))
                .andExpect(status().isOk());
    }

    @Test
    void test_getRolesByUserSearch() {
        assertDoesNotThrow(() ->
                mockMvc.perform(
                        get(RoleController.BASE_NAME))
                        .andExpect(jsonPath("content.[0].id").isNotEmpty())
                        .andExpect(jsonPath("content.[0].name").value("ADMINISTRATOR"))
                        .andExpect(jsonPath("content.[0].type").value("SYSTEM"))
                        .andExpect(jsonPath("content.[0].master").value(true)));
    }


    @Test
    void test_resourcePermissionFlow() throws Exception {
        final String encodedResourceName = encodeStringToBase64(NAME);

        // Create permission
        final ApplyRoleRequestDTO applyRoleRequestDTO = objectMapper.readValue(new File("src/test/resources/test-data/resources/permissions/resource-apply-role-request.json"), ApplyRoleRequestDTO.class);
        applyRoleRequestDTO.setPermissions(Arrays.asList("E8_US62_1_CREATE_NEW_VERSION_OF_RESOURCE_FONT", "E8_US58_EDIT_RESOURCE_METADATA_FONT"));
        mockMvc.perform(post(ResourceController.BASE_NAME + ResourceController.RESOURCE_APPLIED_ROLES_ENDPOINT_WITH_RESOURCE_PATH_VARIABLE,
                FONTS, encodedResourceName)
                .content(objectMapper.writeValueAsString(applyRoleRequestDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Get permission
        final MvcResult result = mockMvc.perform(get(PermissionController.BASE_NAME).param("name", roleName)).andExpect(status().isOk()).andReturn();
        assertNotNull(result.getResponse());
        final PermissionDTO dto = new PermissionDTO();
        result.getResponse();
        dto.setName("some-name");
        final PermissionEntity entity = mapper.map(dto);
        assertEquals(entity.getName(), dto.getName());

    }
}

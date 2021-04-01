package com.itextpdf.dito.manager.integration.crud.permissions;

import com.itextpdf.dito.manager.controller.permission.PermissionController;
import com.itextpdf.dito.manager.controller.resource.ResourceController;
import com.itextpdf.dito.manager.controller.role.RoleController;
import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.dto.resource.update.ApplyRoleRequestDTO;
import com.itextpdf.dito.manager.dto.role.create.RoleCreateRequestDTO;
import com.itextpdf.dito.manager.entity.RoleEntity;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.integration.AbstractIntegrationTest;
import com.itextpdf.dito.manager.repository.resource.ResourcePermissionRepository;
import com.itextpdf.dito.manager.repository.role.RoleRepository;
import com.itextpdf.dito.manager.repository.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.itextpdf.dito.manager.controller.resource.ResourceController.FONTS_ENDPOINT;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ResourcePermissionsFlowIntegrationTest extends AbstractIntegrationTest {

    private static final String CUSTOM_USER_EMAIL = "templatePermissionUser@email.com";
    private static final String CUSTOM_USER_PASSWORD = "password2";
    private static final String IMAGE_NAME = "test-image-for-permissions";
    private static final String IMAGE = "IMAGE";
    private static final String IMAGES = "images";
    private static final String IMAGE_FILE_NAME = "any-permission-name.png";
    private static final MockMultipartFile IMAGE_NAME_PART = new MockMultipartFile("name", "name", "text/plain", IMAGE_NAME.getBytes(StandardCharsets.UTF_8));
    private static final MockMultipartFile IMAGE_TYPE_PART = new MockMultipartFile("type", "type", "text/plain", IMAGE.getBytes(StandardCharsets.UTF_8));
    private static final String STYLESHEET_NAME = "test-css-for-permissions";
    private static final String STYLESHEET = "STYLESHEET";
    private static final String STYLESHEETS = "stylesheets";
    private static final String STYLESHEET_FILE_NAME = "any-permission-name.css";
    private static final MockMultipartFile STYLESHEET_NAME_PART = new MockMultipartFile("name", "name", "text/plain", STYLESHEET_NAME.getBytes(StandardCharsets.UTF_8));
    private static final MockMultipartFile STYLESHEET_TYPE_PART = new MockMultipartFile("type", "type", "text/plain", STYLESHEET.getBytes(StandardCharsets.UTF_8));
    private static final String FONT_NAME = "test-ttf-for-permissions";
    private static final String FONT = "FONT";
    private static final String FONTS = "fonts";
    private static final MockMultipartFile FONT_NAME_PART = new MockMultipartFile("name", "name", "text/plain", FONT_NAME.getBytes(StandardCharsets.UTF_8));
    private static final MockMultipartFile FONT_TYPE_PART = new MockMultipartFile("type", "type", "text/plain", FONT.getBytes(StandardCharsets.UTF_8));

    private static final String roleName = "role-for-resource-permissions-test";
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    public void init() throws Exception {
        //Create
        final MockMultipartFile imageFilePart = new MockMultipartFile("resource", IMAGE_FILE_NAME, MediaType.TEXT_PLAIN_VALUE, Files.readAllBytes(Path.of("src/test/resources/test-data/resources/random.png")));
        mockMvc.perform(MockMvcRequestBuilders.multipart(ResourceController.BASE_NAME)
                .file(imageFilePart)
                .file(IMAGE_NAME_PART)
                .file(IMAGE_TYPE_PART)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated());

        final MockMultipartFile stylesheetFilePart = new MockMultipartFile("resource", STYLESHEET_FILE_NAME, MediaType.TEXT_PLAIN_VALUE, Files.readAllBytes(Path.of("src/test/resources/test-data/resources/test-css.css")));
        mockMvc.perform(MockMvcRequestBuilders.multipart(ResourceController.BASE_NAME)
                .file(stylesheetFilePart)
                .file(STYLESHEET_NAME_PART)
                .file(STYLESHEET_TYPE_PART)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated());

        final MockMultipartFile REGULAR_FONT_FILE_PART = new MockMultipartFile("regular", "regular.ttf", "text/plain", readFileBytes("src/test/resources/test-data/resources/random-regular.ttf"));
        final MockMultipartFile BOLD_FONT_FILE_PART = new MockMultipartFile("bold", "bold.ttf", "text/plain", readFileBytes("src/test/resources/test-data/resources/random-regular.ttf"));
        final MockMultipartFile ITALIC_FONT_FILE_PART = new MockMultipartFile("italic", "italic.ttf", "text/plain", readFileBytes("src/test/resources/test-data/resources/random-regular.ttf"));
        final MockMultipartFile BOLD_ITALIC_FILE_PART = new MockMultipartFile("bold_italic", "bold-italic.ttf", "text/plain", readFileBytes("src/test/resources/test-data/resources/random-regular.ttf"));
        mockMvc.perform(MockMvcRequestBuilders.multipart(ResourceController.BASE_NAME + FONTS_ENDPOINT)
                .file(FONT_NAME_PART)
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

        final String roleName = "TEMPLATE_DESIGNER";
        final RoleEntity roleEntity = roleRepository.findByNameAndMasterTrue(roleName).get();
        UserEntity user = new UserEntity();
        user.setEmail(CUSTOM_USER_EMAIL);
        user.setFirstName("Geoffrey");
        user.setLastName("Grant");
        user.setPassword(CUSTOM_USER_PASSWORD);
        user.setRoles(Set.of(roleEntity));
        user.setActive(Boolean.TRUE);
        user.setPasswordUpdatedByAdmin(Boolean.FALSE);

        userRepository.save(user);

    }

    @AfterEach
    public void tearDown() throws Exception {
        //Clear DB
        mockMvc.perform(
                delete(ResourceController.BASE_NAME + ResourceController.RESOURCE_ENDPOINT_WITH_PATH_VARIABLE_AND_TYPE,
                        IMAGES, Base64.getEncoder().encodeToString(IMAGE_NAME.getBytes())))
                .andExpect(status().isOk());
        mockMvc.perform(
                delete(ResourceController.BASE_NAME + ResourceController.RESOURCE_ENDPOINT_WITH_PATH_VARIABLE_AND_TYPE,
                        STYLESHEETS, Base64.getEncoder().encodeToString(STYLESHEET_NAME.getBytes())))
                .andExpect(status().isOk());
        mockMvc.perform(
                delete(ResourceController.BASE_NAME + ResourceController.RESOURCE_ENDPOINT_WITH_PATH_VARIABLE_AND_TYPE,
                        FONTS, Base64.getEncoder().encodeToString(FONT_NAME.getBytes())))
                .andExpect(status().isOk());
        mockMvc.perform(delete(RoleController.BASE_NAME + "/" + encodeStringToBase64(roleName)))
                .andExpect(status().isOk());
        userRepository.findByEmail(CUSTOM_USER_EMAIL).ifPresent(userRepository::delete);
    }

    @Test
    public void test_resourcePermissionFlow() throws Exception {
        final String encodedResourceName = encodeStringToBase64(IMAGE_NAME);

        //Create permission
        ApplyRoleRequestDTO applyRoleRequestDTO = objectMapper.readValue(new File("src/test/resources/test-data/resources/permissions/resource-apply-role-request.json"), ApplyRoleRequestDTO.class);
        mockMvc.perform(post(ResourceController.BASE_NAME + ResourceController.RESOURCE_APPLIED_ROLES_ENDPOINT_WITH_RESOURCE_PATH_VARIABLE, IMAGES, encodedResourceName)
                .content(objectMapper.writeValueAsString(applyRoleRequestDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //Get resource list
        mockMvc.perform(get(ResourceController.BASE_NAME))
                .andExpect(status().isOk())
                .andExpect(jsonPath("content[0].permissions").isNotEmpty());

        //Get permission
        mockMvc.perform(get(ResourceController.BASE_NAME + ResourceController.RESOURCE_APPLIED_ROLES_ENDPOINT_WITH_RESOURCE_PATH_VARIABLE, IMAGES, encodedResourceName)
                .param("name", roleName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));

        //Filter permissions
        for (String field : ResourcePermissionRepository.SUPPORTED_SORT_FIELDS) {
            mockMvc.perform(get(ResourceController.BASE_NAME + ResourceController.RESOURCE_APPLIED_ROLES_ENDPOINT_WITH_RESOURCE_PATH_VARIABLE, IMAGES, encodedResourceName)
                    .param("sort", field)
                    .param("name", roleName))
                    .andExpect(status().isOk());
        }

        //Search permissions
        for (String field : ResourcePermissionRepository.SUPPORTED_SORT_FIELDS) {
            mockMvc.perform(get(ResourceController.BASE_NAME + ResourceController.RESOURCE_APPLIED_ROLES_ENDPOINT_WITH_RESOURCE_PATH_VARIABLE, IMAGES, encodedResourceName)
                    .param("sort", field)
                    .param("search", field)
                    .param("name", roleName))
                    .andExpect(status().isOk());
        }

        //Delete permission
        final MvcResult result = mockMvc.perform(delete(ResourceController.BASE_NAME + ResourceController.RESOURCE_APPLIED_ROLES_ENDPOINT_WITH_RESOURCE_AND_ROLE_PATH_VARIABLES, IMAGES, encodedResourceName, encodeStringToBase64(roleName)))
                .andExpect(status().isOk())
                .andReturn();
        assertNotNull(result.getResponse());

        applyRoleRequestDTO.setRoleName("ADMINISTRATOR");
        mockMvc.perform(post(ResourceController.BASE_NAME + ResourceController.RESOURCE_APPLIED_ROLES_ENDPOINT_WITH_RESOURCE_PATH_VARIABLE, IMAGES, encodedResourceName)
                .content(objectMapper.writeValueAsString(applyRoleRequestDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void getAll() throws Exception {
        final MvcResult result = mockMvc.perform(get(PermissionController.BASE_NAME)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        assertNotNull(result.getResponse());
    }

    @Test
    public void getAllPageable() throws Exception {
        final MvcResult result = mockMvc.perform(get(PermissionController.BASE_NAME + PermissionController.PAGEABLE_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(20)))
                .andReturn();
        assertNotNull(result.getResponse());
    }

    @Test
    void shouldReturnListOfPermissions() throws Exception {
        final String encodedResourceName = encodeStringToBase64(IMAGE_NAME);

        //Create permission
        final ApplyRoleRequestDTO applyRoleRequestDTO = objectMapper.readValue(new File("src/test/resources/test-data/resources/permissions/resource-apply-role-request.json"), ApplyRoleRequestDTO.class);
        mockMvc.perform(post(ResourceController.BASE_NAME + ResourceController.RESOURCE_APPLIED_ROLES_ENDPOINT_WITH_RESOURCE_PATH_VARIABLE, IMAGES, encodedResourceName)
                .content(objectMapper.writeValueAsString(applyRoleRequestDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //Get resource metadata
        mockMvc.perform(get(ResourceController.BASE_NAME + ResourceController.RESOURCE_ENDPOINT_WITH_PATH_VARIABLE_AND_TYPE, IMAGES, encodedResourceName)
                .with(user(CUSTOM_USER_EMAIL).authorities(Stream.of("E8_US54_VIEW_RESOURCE_METADATA_IMAGE", "E8_US52_TABLE_OF_RESOURCES")
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList())
                )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("permissions", hasSize(4)));

        //Get resource list
        final MvcResult mvcResult = mockMvc.perform(get(ResourceController.BASE_NAME)
                .with(user(CUSTOM_USER_EMAIL).authorities(Collections.singletonList(new SimpleGrantedAuthority("E8_US52_TABLE_OF_RESOURCES")))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("content[0].permissions", hasSize(4)))
                .andReturn();

        assertNotNull(mvcResult.getResponse());

        userRepository.findByEmail(CUSTOM_USER_EMAIL).ifPresent(userRepository::delete);
    }

    @ParameterizedTest
    @CsvSource(value = {
            IMAGE + ";" + IMAGE_NAME + ";TEMPLATE_DESIGNER;E8_US55_EDIT_RESOURCE_METADATA_IMAGE,E8_US62_CREATE_NEW_VERSION_OF_RESOURCE_IMAGE,E8_US65_ROLL_BACK_OF_THE_RESOURCE_IMAGE,E8_US66_DELETE_RESOURCE_IMAGE",
            STYLESHEET + ";" + STYLESHEET_NAME + ";TEMPLATE_DESIGNER;E8_US66_2_DELETE_RESOURCE_STYLESHEET,E8_US63_CREATE_NEW_VERSION_OF_RESOURCE_STYLESHEET,E8_US61_EDIT_RESOURCE_METADATA_STYLESHEET,E8_US65_2_ROLL_BACK_OF_THE_RESOURCE_STYLESHEET",
            FONT + ";" + FONT_NAME + ";TEMPLATE_DESIGNER;E8_US66_1_DELETE_RESOURCE_FONT,E8_US62_1_CREATE_NEW_VERSION_OF_RESOURCE_FONT,E8_US58_EDIT_RESOURCE_METADATA_FONT,E8_US65_1_ROLL_BACK_OF_THE_RESOURCE_FONT"
    }, delimiter = ';')
    void shouldCheckAppliedPermissions(final String type, final String name, final String roleName, final String permissions) throws Exception {

        final ResourceTypeEnum resourceTypeEnum = ResourceTypeEnum.valueOf(type);
        final String encodedResourceName = encodeStringToBase64(name);
        final String[] permissionsArray = permissions.split(",");
        assertNotNull(encodedResourceName);

        //Get resource metadata
        mockMvc.perform(get(ResourceController.BASE_NAME).param("type", resourceTypeEnum.toString())
                .with(user(CUSTOM_USER_EMAIL).authorities(Collections.singletonList(new SimpleGrantedAuthority("E8_US52_TABLE_OF_RESOURCES")))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", hasSize(1)))
                .andExpect(jsonPath("content[0].permissions", hasSize(4)));

        //Create permission
        final ApplyRoleRequestDTO applyRoleRequestDTO = new ApplyRoleRequestDTO();
        applyRoleRequestDTO.setRoleName(roleName);
        applyRoleRequestDTO.setPermissions(Collections.emptyList());

        mockMvc.perform(post(ResourceController.BASE_NAME + ResourceController.RESOURCE_APPLIED_ROLES_ENDPOINT_WITH_RESOURCE_PATH_VARIABLE, resourceTypeEnum.pluralName, encodedResourceName)
                .content(objectMapper.writeValueAsString(applyRoleRequestDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //Get resource metadata
        mockMvc.perform(get(ResourceController.BASE_NAME).param("type", resourceTypeEnum.toString())
                .with(user(CUSTOM_USER_EMAIL).authorities(Collections.singletonList(new SimpleGrantedAuthority("E8_US52_TABLE_OF_RESOURCES")))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", hasSize(1)))
                .andExpect(jsonPath("content[0].permissions", hasSize(0)));

        //Update permission
        applyRoleRequestDTO.setPermissions(Arrays.asList(permissionsArray));
        mockMvc.perform(post(ResourceController.BASE_NAME + ResourceController.RESOURCE_APPLIED_ROLES_ENDPOINT_WITH_RESOURCE_PATH_VARIABLE, resourceTypeEnum.pluralName, encodedResourceName)
                .content(objectMapper.writeValueAsString(applyRoleRequestDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //Get resource metadata
        mockMvc.perform(get(ResourceController.BASE_NAME).param("type", resourceTypeEnum.toString())
                .with(user(CUSTOM_USER_EMAIL).authorities(Collections.singletonList(new SimpleGrantedAuthority("E8_US52_TABLE_OF_RESOURCES")))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", hasSize(1)))
                .andExpect(jsonPath("content[0].permissions", hasSize(4)));
    }

}

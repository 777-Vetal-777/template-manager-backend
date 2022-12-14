package com.itextpdf.dito.manager.integration.crud.permissions;

import com.itextpdf.dito.manager.controller.resource.ResourceController;
import com.itextpdf.dito.manager.controller.role.RoleController;
import com.itextpdf.dito.manager.controller.template.TemplateController;
import com.itextpdf.dito.manager.dto.resource.update.ApplyRoleRequestDTO;
import com.itextpdf.dito.manager.dto.role.create.RoleCreateRequestDTO;
import com.itextpdf.dito.manager.dto.template.create.TemplateCreateRequestDTO;
import com.itextpdf.dito.manager.dto.template.create.TemplatePartDTO;
import com.itextpdf.dito.manager.dto.template.update.TemplateUpdateRequestDTO;
import com.itextpdf.dito.manager.entity.RoleEntity;
import com.itextpdf.dito.manager.entity.TemplateTypeEnum;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.integration.AbstractIntegrationTest;
import com.itextpdf.dito.manager.repository.role.RoleRepository;
import com.itextpdf.dito.manager.repository.template.TemplateRepository;
import com.itextpdf.dito.manager.repository.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TemplatePermissionsFlowIntegrationTest extends AbstractIntegrationTest {
    public static final String CUSTOM_USER_EMAIL = "TemplatePermissionUser@email.com";
    public static final String CUSTOM_USER_PASSWORD = "password2";
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TemplateRepository templateRepository;

    @Autowired
    private RoleRepository roleRepository;

    private String templateName;
    private String roleName;

    @BeforeEach
    void init() throws Exception {
        //create template
        final TemplateCreateRequestDTO templateCreateRequestDTO = objectMapper.readValue(new File("src/test/resources/test-data/templates/permissions/template-create-request.json"), TemplateCreateRequestDTO.class);
        templateName = templateCreateRequestDTO.getName();
        mockMvc.perform(post(TemplateController.BASE_NAME)
                .content(objectMapper.writeValueAsString(templateCreateRequestDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        //create role for testing
        final RoleCreateRequestDTO roleCreateRequestDTO = objectMapper.readValue(new File("src/test/resources/test-data/templates/permissions/role-for-permissions-test-create-request.json"), RoleCreateRequestDTO.class);
        roleName = roleCreateRequestDTO.getName();
        mockMvc.perform(post(RoleController.BASE_NAME)
                .content(objectMapper.writeValueAsString(roleCreateRequestDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        final RoleEntity role = roleRepository.findByNameAndMasterTrue(roleName).orElseThrow();

        UserEntity user2 = new UserEntity();
        user2.setEmail(CUSTOM_USER_EMAIL);
        user2.setFirstName("Geoffrey");
        user2.setLastName("Grant");
        user2.setPassword(CUSTOM_USER_PASSWORD);
        user2.setRoles(Set.of(role));
        user2.setActive(Boolean.TRUE);
        user2.setPasswordUpdatedByAdmin(Boolean.FALSE);

        userRepository.save(user2);
    }

    @AfterEach
    void destroy() throws Exception {
        templateRepository.findByName(templateName).ifPresent(templateRepository::delete);

        userRepository.findByEmail(CUSTOM_USER_EMAIL).ifPresent(userRepository::delete);

        roleRepository.findByNameAndMasterTrue(roleName).ifPresent(roleRepository::delete);
    }

    @Test
    void testApplyRole() throws Exception {
        //check that requests done well
        final TemplateUpdateRequestDTO templateUpdateRequestDTO = objectMapper.readValue(new File("src/test/resources/test-data/templates/permissions/template-update-metadata-request.json"), TemplateUpdateRequestDTO.class);
        mockMvc.perform(patch(TemplateController.BASE_NAME + TemplateController.TEMPLATE_ENDPOINT_WITH_PATH_VARIABLE, encodeStringToBase64(templateName))
                .content(objectMapper.writeValueAsString(templateUpdateRequestDTO))
                .with(user(CUSTOM_USER_EMAIL).password(CUSTOM_USER_PASSWORD).authorities(
                        Stream.of("E9_US75_EDIT_TEMPLATE_METADATA_STANDARD")
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList())))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        final URI uri = UriComponentsBuilder.fromUriString(TemplateController.BASE_NAME + TemplateController.TEMPLATE_VERSION_ENDPOINT).build().encode().toUri();
        final MockMultipartFile name = new MockMultipartFile("name", "name", "text/plain", templateName.getBytes());
        final MockMultipartFile comment = new MockMultipartFile("comment", "comment", "text/plain", "some comment".getBytes());
        mockMvc.perform(MockMvcRequestBuilders.multipart(uri)
                .file(name)
                .file(comment)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .with(user(CUSTOM_USER_EMAIL).password(CUSTOM_USER_PASSWORD).authorities(
                        Stream.of("E9_US76_CREATE_NEW_VERSION_OF_TEMPLATE_STANDARD")
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList())
                )))
                .andExpect(status().isOk());

        final ApplyRoleRequestDTO applyRoleRequestDTO = objectMapper.readValue(new File("src/test/resources/test-data/templates/permissions/role-for-permissions-test-apply-request.json"), ApplyRoleRequestDTO.class);
        mockMvc.perform(post(TemplateController.BASE_NAME + TemplateController.TEMPLATE_ROLES_ENDPOINT_WITH_PATH_VARIABLE, encodeStringToBase64(templateName))
                .content(objectMapper.writeValueAsString(applyRoleRequestDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(CUSTOM_USER_EMAIL).password(CUSTOM_USER_PASSWORD).authorities(
                        Stream.of("E9_US82_TABLE_OF_TEMPLATE_PERMISSIONS_STANDARD")
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList())
                ))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        //reducing role as global administrator, custom user has no permissions for the action
        mockMvc.perform(post(TemplateController.BASE_NAME + TemplateController.TEMPLATE_ROLES_ENDPOINT_WITH_PATH_VARIABLE, encodeStringToBase64(templateName))
                .content(objectMapper.writeValueAsString(applyRoleRequestDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //check that requests are forbidden now
        mockMvc.perform(patch(TemplateController.BASE_NAME + TemplateController.TEMPLATE_ENDPOINT_WITH_PATH_VARIABLE, encodeStringToBase64(templateName))
                .content(objectMapper.writeValueAsString(templateUpdateRequestDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(CUSTOM_USER_EMAIL).password(CUSTOM_USER_PASSWORD)))
                .andExpect(status().isForbidden());

        mockMvc.perform(MockMvcRequestBuilders.multipart(uri)
                .file(name)
                .file(comment)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .with(user(CUSTOM_USER_EMAIL).password(CUSTOM_USER_PASSWORD)))
                .andExpect(status().isForbidden());

        //repeat reducing role and check that is going well
        mockMvc.perform(post(TemplateController.BASE_NAME + TemplateController.TEMPLATE_ROLES_ENDPOINT_WITH_PATH_VARIABLE, encodeStringToBase64(templateName))
                .content(objectMapper.writeValueAsString(applyRoleRequestDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //get template list
        mockMvc.perform(get(TemplateController.BASE_NAME))
                .andExpect(status().isOk())
                .andExpect(jsonPath("content[0].permissions").isNotEmpty());

        //delete custom role
        final MvcResult result = mockMvc.perform(delete(TemplateController.BASE_NAME + TemplateController.TEMPLATE_ROLES_ENDPOINT_WITH_PATH_VARIABLE_AND_ROLE_NAME,
                encodeStringToBase64(templateName), encodeStringToBase64(roleName)))
                .andExpect(status().isOk())
                .andReturn();
        assertNotNull(result.getResponse());

        //repeating delete returns an error
        mockMvc.perform(delete(TemplateController.BASE_NAME + TemplateController.TEMPLATE_ROLES_ENDPOINT_WITH_PATH_VARIABLE_AND_ROLE_NAME,
                encodeStringToBase64(templateName), encodeStringToBase64(roleName)))
                .andExpect(status().isNotFound());

    }

    @Test
    void testApplyRoleForComposition() throws Exception {
        final String compositionTemplateName = "composition_template";
        final String encodedCompositionTemplateName = encodeStringToBase64(compositionTemplateName);
        final TemplateCreateRequestDTO templateCreateRequestDTO = new TemplateCreateRequestDTO();
        templateCreateRequestDTO.setType(TemplateTypeEnum.COMPOSITION);
        templateCreateRequestDTO.setName(compositionTemplateName);
        final TemplatePartDTO templatePartDTO = new TemplatePartDTO();
        templatePartDTO.setName(templateName);
        templateCreateRequestDTO.setTemplateParts(Collections.singletonList(templatePartDTO));

        mockMvc.perform(post(TemplateController.BASE_NAME)
                .content(objectMapper.writeValueAsString(templateCreateRequestDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());

        //Get template metadata
        mockMvc.perform(get(TemplateController.BASE_NAME).param("name", compositionTemplateName)
                .with(user(CUSTOM_USER_EMAIL).authorities(Collections.singletonList(new SimpleGrantedAuthority("E9_US70_TEMPLATES_TABLE")))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", hasSize(1)))
                .andExpect(jsonPath("content[0].permissions", hasSize(3)));

        //Create permission
        final ApplyRoleRequestDTO applyRoleRequestDTO = new ApplyRoleRequestDTO();
        applyRoleRequestDTO.setRoleName(roleName);
        applyRoleRequestDTO.setPermissions(Collections.emptyList());

        mockMvc.perform(post(TemplateController.BASE_NAME + TemplateController.TEMPLATE_ROLES_ENDPOINT_WITH_PATH_VARIABLE, encodedCompositionTemplateName)
                .content(objectMapper.writeValueAsString(applyRoleRequestDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //Get template metadata
        mockMvc.perform(get(TemplateController.BASE_NAME).param("name", compositionTemplateName)
                .with(user(CUSTOM_USER_EMAIL).authorities(Collections.singletonList(new SimpleGrantedAuthority("E9_US70_TEMPLATES_TABLE")))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", hasSize(1)))
                .andExpect(jsonPath("content[0].permissions", hasSize(0)));

        //Update permission
        final String[] permissionsArray = {"E9_US76_CREATE_NEW_VERSION_OF_TEMPLATE_STANDARD", "E9_US100_ROLL_BACK_OF_THE_COMPOSITION_TEMPLATE", "E9_US77_CREATE_NEW_VERSION_OF_TEMPLATE_COMPOSED"};
        applyRoleRequestDTO.setPermissions(Arrays.asList(permissionsArray));
        mockMvc.perform(post(TemplateController.BASE_NAME + TemplateController.TEMPLATE_ROLES_ENDPOINT_WITH_PATH_VARIABLE, encodedCompositionTemplateName)
                .content(objectMapper.writeValueAsString(applyRoleRequestDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //Get resource metadata
        mockMvc.perform(get(TemplateController.BASE_NAME).param("name", compositionTemplateName)
                .with(user(CUSTOM_USER_EMAIL).authorities(Collections.singletonList(new SimpleGrantedAuthority("E9_US70_TEMPLATES_TABLE")))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", hasSize(1)))
                .andExpect(jsonPath("content[0].permissions", hasSize(3)));

        final MvcResult result = mockMvc.perform(delete(TemplateController.BASE_NAME + TemplateController.TEMPLATE_ENDPOINT_WITH_PATH_VARIABLE, encodedCompositionTemplateName))
                .andExpect(status().isOk())
                .andReturn();
        assertNotNull(result.getResponse());
    }

}

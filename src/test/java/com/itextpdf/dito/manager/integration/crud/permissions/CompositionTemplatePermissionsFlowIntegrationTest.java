package com.itextpdf.dito.manager.integration.crud.permissions;

import com.itextpdf.dito.manager.component.encoder.Encoder;
import com.itextpdf.dito.manager.controller.role.RoleController;
import com.itextpdf.dito.manager.controller.template.TemplateController;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionType;
import com.itextpdf.dito.manager.dto.resource.update.ApplyRoleRequestDTO;
import com.itextpdf.dito.manager.dto.role.create.RoleCreateRequestDTO;
import com.itextpdf.dito.manager.dto.template.create.TemplateCreateRequestDTO;
import com.itextpdf.dito.manager.dto.template.update.TemplateUpdateRequestDTO;
import com.itextpdf.dito.manager.entity.PermissionEntity;
import com.itextpdf.dito.manager.entity.RoleEntity;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.integration.AbstractIntegrationTest;
import com.itextpdf.dito.manager.integration.editor.controller.template.TemplateManagementController;
import com.itextpdf.dito.manager.repository.permission.PermissionRepository;
import com.itextpdf.dito.manager.repository.role.RoleRepository;
import com.itextpdf.dito.manager.repository.template.TemplateFileRepository;
import com.itextpdf.dito.manager.repository.template.TemplateRepository;
import com.itextpdf.dito.manager.repository.user.UserRepository;
import com.itextpdf.dito.manager.service.datacollection.DataCollectionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CompositionTemplatePermissionsFlowIntegrationTest extends AbstractIntegrationTest {
    public static final String CUSTOM_USER_EMAIL = "TemplatePermissionUser@email.com";
    public static final String CUSTOM_USER_PASSWORD = "password2";
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TemplateRepository templateRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private TemplateFileRepository templateFileRepository;
    @Autowired
    private PermissionRepository permissionRepository;
    @Autowired
    private DataCollectionService dataCollectionService;
    @Autowired
    Encoder encoder;

    private String templateName;
    private String roleName;

    @BeforeEach
    void init() throws Exception {
        dataCollectionService.create("new-data-collection", DataCollectionType.JSON, "{\"file\":\"data\"}".getBytes(), "datacollection.json", "admin@email.com");

        templateRepository.deleteAll();
        performCreateTemplateRequest("src/test/resources/test-data/templates/template-create-request.json");
        performCreateTemplateRequest("src/test/resources/test-data/templates/template-create-request-header.json");
        performCreateTemplateRequest("src/test/resources/test-data/templates/template-create-request-footer.json");
        performCreateTemplateRequest("src/test/resources/test-data/templates/template-create-request-footer2.json");
        performCreateTemplateRequest("src/test/resources/test-data/templates/template-create-request-with-data-collection2.json");

        final TemplateCreateRequestDTO request = objectMapper.readValue(new File("src/test/resources/test-data/templates/template-create-request-composition.json"), TemplateCreateRequestDTO.class);

        templateName = request.getName();
        request.getTemplateParts().removeIf(part -> "some-footer-template".equals(part.getName()));
        request.getTemplateParts().removeIf(part -> "some-template-with-data-collection".equals(part.getName()));

        mockMvc.perform(post(TemplateController.BASE_NAME)
                .content(objectMapper.writeValueAsString(request))
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
        final PermissionEntity permissionEntity = permissionRepository.findByName("E9_US77_CREATE_NEW_VERSION_OF_TEMPLATE_COMPOSED").get();
        role.getPermissions().add(permissionEntity);
        final RoleEntity savedRole = roleRepository.saveAndFlush(role);

        final UserEntity user2 = new UserEntity();
        user2.setEmail(CUSTOM_USER_EMAIL);
        user2.setFirstName("Geoffrey");
        user2.setLastName("Grant");
        user2.setPassword(CUSTOM_USER_PASSWORD);
        user2.setRoles(Set.of(savedRole));
        user2.setActive(Boolean.TRUE);
        user2.setPasswordUpdatedByAdmin(Boolean.FALSE);

        userRepository.save(user2);
        
        final TemplateEntity templateEntity = templateRepository.findByName(templateName).get();
        templateEntity.setAppliedRoles(new HashSet<>(Arrays.asList(savedRole)));
        templateRepository.saveAndFlush(templateEntity);
    }

    @AfterEach
    void destroy() throws Exception {
        templateFileRepository.deleteAll();
        templateRepository.deleteAll();
        userRepository.findByEmail(CUSTOM_USER_EMAIL).ifPresent(userRepository::delete);
        roleRepository.findByNameAndMasterTrue(roleName).ifPresent(roleRepository::delete);
    }

    @Test
    void testApplyRole() throws Exception {
        //check that requests done well
        final TemplateUpdateRequestDTO templateUpdateRequestDTO = objectMapper.readValue(new File("src/test/resources/test-data/templates/template-update-request.json"), TemplateUpdateRequestDTO.class);
        mockMvc.perform(patch(TemplateController.BASE_NAME + TemplateController.TEMPLATE_ENDPOINT_WITH_PATH_VARIABLE, encodeStringToBase64(templateName))
                .content(objectMapper.writeValueAsString(templateUpdateRequestDTO))
                .with(user(CUSTOM_USER_EMAIL).password(CUSTOM_USER_PASSWORD).authorities(
                        Stream.of("E9_US75_EDIT_TEMPLATE_METADATA_STANDARD")
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList())))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        templateName = "updated-template-name";
        final URI uri = UriComponentsBuilder.fromUriString(TemplateController.BASE_NAME + TemplateController.TEMPLATE_VERSION_ENDPOINT).build().encode().toUri();
        final MockMultipartFile name = new MockMultipartFile("name", "name", "text/plain", templateName.getBytes());
        final MockMultipartFile comment = new MockMultipartFile("comment", "comment", "text/plain", "some comment".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart(uri)
                .file(name)
                .file(comment)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .with(user(CUSTOM_USER_EMAIL).password(CUSTOM_USER_PASSWORD).authorities(
                        Stream.of("E9_US77_CREATE_NEW_VERSION_OF_TEMPLATE_COMPOSED")
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList())
                ))
        )
                .andExpect(status().isOk());
        
        //removing permission
        final TemplateEntity templateEntity = templateRepository.findByName(templateName).get();
        templateEntity.setAppliedRoles(null);
        templateRepository.saveAndFlush(templateEntity);

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

        //delete custom role
        final MvcResult result = mockMvc.perform(delete(TemplateController.BASE_NAME + TemplateController.TEMPLATE_ROLES_ENDPOINT_WITH_PATH_VARIABLE_AND_ROLE_NAME,
                encodeStringToBase64(templateName), encodeStringToBase64(roleName)))
                .andExpect(status().isOk())
                .andReturn();
        assertNotNull(result.getResponse());
    }

    private void performCreateTemplateRequest(final String pathname) throws Exception {
        final TemplateCreateRequestDTO request = objectMapper.readValue(new File(pathname), TemplateCreateRequestDTO.class);
        mockMvc.perform(post(TemplateController.BASE_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

    }


}

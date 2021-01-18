package com.itextpdf.dito.manager.integration.filtering;

import com.itextpdf.dito.manager.controller.template.TemplateController;
import com.itextpdf.dito.manager.entity.PermissionEntity;
import com.itextpdf.dito.manager.entity.RoleEntity;
import com.itextpdf.dito.manager.entity.RoleTypeEnum;
import com.itextpdf.dito.manager.entity.TemplateTypeEnum;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.integration.AbstractIntegrationTest;
import com.itextpdf.dito.manager.repository.permission.PermissionRepository;
import com.itextpdf.dito.manager.repository.template.TemplatePermissionRepository;
import com.itextpdf.dito.manager.repository.template.TemplateRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TemplatePermissionsSearchAndFilterIntegrationTest extends AbstractIntegrationTest implements FilterAndSearchTest {

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private TemplateRepository templateRepository;

    private final static String ROLES = "/roles";

    private TemplateEntity templateEntity = new TemplateEntity();

    private String templateName = "Template";
    private RoleEntity roleEntity1;
    private RoleEntity roleEntity2;
    private PermissionEntity permissionEntity1;
    private PermissionEntity permissionEntity2;


    @BeforeEach
    public void init() {
        roleEntity1 = new RoleEntity();
        roleEntity1.setName("ADMIN");
        roleEntity1.setType(RoleTypeEnum.CUSTOM);
        roleEntity1.setMaster(false);
        roleEntity2 = new RoleEntity();
        roleEntity2.setName("GLOBAL_ADMINISTRATOR");
        roleEntity2.setType(RoleTypeEnum.CUSTOM);
        roleEntity2.setMaster(false);
        Set<RoleEntity> set = new HashSet<>();
        permissionEntity1 = permissionRepository.findByName("E9_US75_EDIT_TEMPLATE_METADATA_STANDARD").get();
        permissionEntity2 = permissionRepository.findByName("E9_US76_CREATE_NEW_VERSION_OF_TEMPLATE_STANDARD").get();
        roleEntity1.setPermissions(new HashSet<>(Arrays.asList(permissionEntity1, permissionEntity2)));
        roleEntity2.setPermissions(new HashSet<>(Arrays.asList(permissionEntity1, permissionEntity2)));
        set.add(roleEntity1);
        set.add(roleEntity2);
        templateEntity.setAppliedRoles(set);
        templateEntity.setName(templateName);
        templateEntity.setType(TemplateTypeEnum.HEADER);
        permissionRepository.saveAll(Arrays.asList(permissionEntity1, permissionEntity2));
        roleEntity1.setTemplates(new HashSet<>(Arrays.asList(templateEntity)));
        templateRepository.save(templateEntity);

    }

    @AfterEach
    public void clearDb() {
        templateRepository.deleteAll();
    }

    @Override
    @Test
    public void test_filtering() throws Exception {
        mockMvc.perform(get(TemplateController.BASE_NAME + "/" + Base64.getEncoder().encodeToString(templateName.getBytes()) + ROLES)
                .param("name", "GLOBAL_ADMINISTRATOR"))
                .andExpect(jsonPath("$.content", hasSize(1)));


        mockMvc.perform(get(TemplateController.BASE_NAME + "/" + Base64.getEncoder().encodeToString(templateName.getBytes()) + ROLES)
                .param("name", "ADMIN"))
                .andExpect(jsonPath("$.content", hasSize(1)));

        mockMvc.perform(get(TemplateController.BASE_NAME + "/" + Base64.getEncoder().encodeToString(templateName.getBytes()) + ROLES)
                .param("name", "GLOBAL_ADMINISTRATOR"))
                .andExpect(jsonPath("$.content", hasSize(1)));

        mockMvc.perform(get(TemplateController.BASE_NAME + "/" + Base64.getEncoder().encodeToString(templateName.getBytes()) + ROLES)
                .param("name", "ADMIN, GLOBAL_ADMINISTRATOR")
                .param("E9_US75_EDIT_TEMPLATE_METADATA_STANDARD", "false,true"))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].E9_US75_EDIT_TEMPLATE_METADATA_STANDARD").value("true"))
                .andExpect(jsonPath("$.content[0].E9_US76_CREATE_NEW_VERSION_OF_TEMPLATE_STANDARD").value("true"));

        mockMvc.perform(get(TemplateController.BASE_NAME + "/" + Base64.getEncoder().encodeToString(templateName.getBytes()) + ROLES)
                .param("name", "ADMIN, GLOBAL_ADMINISTRATOR")
                .param("E9_US75_EDIT_TEMPLATE_METADATA_STANDARD", "false,true"))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].E9_US24_EXPORT_TEMPLATE_DATA").value("false"))
                .andExpect(jsonPath("$.content[0].E9_US81_PREVIEW_TEMPLATE_STANDARD").value("false"));

    }

    @Override
    @Test
    public void test_searchAndFiltering() throws Exception {
        mockMvc.perform(get(TemplateController.BASE_NAME + "/" + Base64.getEncoder().encodeToString(templateName.getBytes()) + ROLES)
                .param("name", "ADMIN, GLOBAL_ADMINISTRATOR")
                .param("search", "ADMIN"))
                .andExpect(jsonPath("$.content", hasSize(2)));

        mockMvc.perform(get(TemplateController.BASE_NAME + "/" + Base64.getEncoder().encodeToString(templateName.getBytes()) + ROLES)
                .param("name", "ADMIN, GLOBAL_ADMINISTRATOR")
                .param("E9_US75_EDIT_TEMPLATE_METADATA_STANDARD", "true")
                .param("search", "ADMIN"))
                .andExpect(jsonPath("$.content", hasSize(2)));

        mockMvc.perform(get(TemplateController.BASE_NAME + "/" + Base64.getEncoder().encodeToString(templateName.getBytes()) + ROLES)
                .param("name", "ADMIN, GLOBAL_ADMINISTRATOR")
                .param("E9_US76_CREATE_NEW_VERSION_OF_TEMPLATE_STANDARD", "true")
                .param("search", "ADMIN"))
                .andExpect(jsonPath("$.content", hasSize(2)));

        mockMvc.perform(get(TemplateController.BASE_NAME + "/" + Base64.getEncoder().encodeToString(templateName.getBytes()) + ROLES)
                .param("name", "ADMIN, GLOBAL_ADMINISTRATOR")
                .param("E9_US81_PREVIEW_TEMPLATE_STANDARD", "false")
                .param("search", "ADMIN"))
                .andExpect(jsonPath("$.content", hasSize(2)));

        mockMvc.perform(get(TemplateController.BASE_NAME + "/" + Base64.getEncoder().encodeToString(templateName.getBytes()) + ROLES)
                .param("name", "ADMIN, GLOBAL_ADMINISTRATOR")
                .param("E9_US81_PREVIEW_TEMPLATE_STANDARD", "false")
                .param("search", "ADMIN"))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].E9_US76_CREATE_NEW_VERSION_OF_TEMPLATE_STANDARD").value(true))
                .andExpect(jsonPath("$.content[1].E9_US76_CREATE_NEW_VERSION_OF_TEMPLATE_STANDARD").value(true));
    }

    @Override
    @Test
    public void test_sortWithSearch() throws Exception {
        for (String field : TemplatePermissionRepository.SUPPORTED_SORT_FIELDS) {
            mockMvc.perform(get(TemplateController.BASE_NAME + "/" + Base64.getEncoder().encodeToString(templateName.getBytes()) + ROLES)
                    .param("name", "ADMIN")
                    .param("sort", field)
                    .param("search", "ADMIN"))
                    .andExpect(status().isOk());
        }

    }

    @Override
    @Test
    public void test_sortWithFiltering() throws Exception {
        for (String field : TemplatePermissionRepository.SUPPORTED_SORT_FIELDS) {
            mockMvc.perform(get(TemplateController.BASE_NAME + "/" + Base64.getEncoder().encodeToString(templateName.getBytes()) + ROLES)
                    .param("name", "ADMIN")
                    .param("sort", field))
                    .andExpect(status().isOk());
        }
    }
}

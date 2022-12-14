package com.itextpdf.dito.manager.integration.crud.permissions;

import com.itextpdf.dito.manager.controller.datacollection.DataCollectionController;
import com.itextpdf.dito.manager.controller.role.RoleController;
import com.itextpdf.dito.manager.dto.resource.update.ApplyRoleRequestDTO;
import com.itextpdf.dito.manager.dto.role.create.RoleCreateRequestDTO;
import com.itextpdf.dito.manager.integration.AbstractIntegrationTest;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionPermissionsRepository;
import com.itextpdf.dito.manager.repository.user.UserRepository;
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

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DataCollectionPermissionsFlowIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    final String dataCollectionName = "data-collection-for-permissions";
    final String roleName = "role-for-permissions-test";

    @BeforeEach
    public void init() throws Exception {
        //Imitate created data collection
        final MockMultipartFile file = new MockMultipartFile("attachment", "any-name.json", "text/plain", "{\"file\":\"data\"}".getBytes());
        final MockMultipartFile name = new MockMultipartFile("name", "name", "text/plain", dataCollectionName.getBytes());
        final MockMultipartFile type = new MockMultipartFile("type", "type", "text/plain", "JSON".getBytes());
        final URI uri = UriComponentsBuilder.fromUriString(DataCollectionController.BASE_NAME).build().encode().toUri();
        mockMvc.perform(MockMvcRequestBuilders.multipart(uri)
                .file(file)
                .file(name)
                .file(type)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated());

        RoleCreateRequestDTO request = objectMapper.readValue(new File("src/test/resources/test-data/datacollections/permissions/role-for-permissions-test-create-request.json"), RoleCreateRequestDTO.class);
        mockMvc.perform(post(RoleController.BASE_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @AfterEach
    public void tearDown() throws Exception {
        //Clear DB
        mockMvc.perform(delete(DataCollectionController.BASE_NAME + "/" + encodeStringToBase64(dataCollectionName)))
                .andExpect(status().isOk());
        mockMvc.perform(delete(RoleController.BASE_NAME + "/" + encodeStringToBase64(roleName)))
                .andExpect(status().isOk());
    }

    @Test
    public void test_dataCollectionPermissions() throws Exception {
        final String encodedDataCollectionName = encodeStringToBase64(dataCollectionName);

        //Create permission for data collection
        ApplyRoleRequestDTO applyRoleRequestDTO = objectMapper.readValue(new File("src/test/resources/test-data/datacollections/permissions/data-collection-apply-role-request.json"), ApplyRoleRequestDTO.class);
        mockMvc.perform(post(DataCollectionController.BASE_NAME + DataCollectionController.DATA_COLLECTION_APPLIED_ROLES_ENDPOINT_WITH_DATA_COLLECTION_PATH_VARIABLE, encodedDataCollectionName)
                .content(objectMapper.writeValueAsString(applyRoleRequestDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //Get permission for data collection
        mockMvc.perform(get(DataCollectionController.BASE_NAME + DataCollectionController.DATA_COLLECTION_APPLIED_ROLES_ENDPOINT_WITH_DATA_COLLECTION_PATH_VARIABLE, encodedDataCollectionName)
                .param("name", roleName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));

        //Sort and filter permission for data collection
        for (String field : DataCollectionPermissionsRepository.SUPPORTED_SORT_PERMISSION_FIELDS) {
            mockMvc.perform(get(DataCollectionController.BASE_NAME + DataCollectionController.DATA_COLLECTION_APPLIED_ROLES_ENDPOINT_WITH_DATA_COLLECTION_PATH_VARIABLE, encodedDataCollectionName)
                    .param("sort", field)
                    .param("name", roleName))
                    .andExpect(status().isOk());
        }

        //Search permission for data collection
        for (String field : DataCollectionPermissionsRepository.SUPPORTED_SORT_PERMISSION_FIELDS) {
            mockMvc.perform(get(DataCollectionController.BASE_NAME + DataCollectionController.DATA_COLLECTION_APPLIED_ROLES_ENDPOINT_WITH_DATA_COLLECTION_PATH_VARIABLE, encodedDataCollectionName)
                    .param("sort", field)
                    .param("search", field)
                    .param("name", roleName))
                    .andExpect(status().isOk());
        }


        //Delete data collection permission
        final MvcResult result = mockMvc.perform(delete(DataCollectionController.BASE_NAME + DataCollectionController.DATA_COLLECTION_APPLIED_ROLES_ENDPOINT_WITH_DATA_COLLECTION_AND_ROLE_PATH_VARIABLES, encodedDataCollectionName, encodeStringToBase64(roleName)))
                .andExpect(status().isOk())
                .andReturn();
        assertNotNull(result.getResponse());
    }

}

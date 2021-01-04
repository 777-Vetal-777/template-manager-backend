package com.itextpdf.dito.manager.integration.crud;

import com.itextpdf.dito.manager.controller.instance.InstanceController;
import com.itextpdf.dito.manager.controller.workspace.WorkspaceController;
import com.itextpdf.dito.manager.dto.instance.InstanceDTO;
import com.itextpdf.dito.manager.dto.instance.create.InstanceRememberRequestDTO;
import com.itextpdf.dito.manager.dto.instance.create.InstancesRememberRequestDTO;
import com.itextpdf.dito.manager.dto.promotionpath.PromotionPathDTO;
import com.itextpdf.dito.manager.dto.workspace.WorkspaceDTO;
import com.itextpdf.dito.manager.dto.workspace.create.WorkspaceCreateRequestDTO;
import com.itextpdf.dito.manager.integration.AbstractIntegrationTest;
import com.itextpdf.dito.manager.repository.instance.InstanceRepository;
import com.itextpdf.dito.manager.repository.workspace.WorkspaceRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.io.File;
import java.util.Arrays;
import java.util.Base64;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class WorkspaceFlowIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private WorkspaceRepository workspaceRepository;

    @Autowired
    private InstanceRepository instanceRepository;

    @AfterEach
    public void teardown() {
        workspaceRepository.deleteAll();
        instanceRepository.deleteAll();
    }

    @BeforeEach
    void tearUp() throws Exception {
        InstancesRememberRequestDTO instancesRememberRequestDTO = objectMapper
                .readValue(new File("src/test/resources/test-data/workspaces/instances-create-request.json"),
                        InstancesRememberRequestDTO.class);
        mockMvc.perform(post(InstanceController.BASE_NAME)
                .content(objectMapper.writeValueAsString(instancesRememberRequestDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateWorkspace() throws Exception {
        WorkspaceCreateRequestDTO request = objectMapper
                .readValue(new File("src/test/resources/test-data/workspaces/workspace-create-request.json"),
                        WorkspaceCreateRequestDTO.class);
        mockMvc.perform(post(WorkspaceController.BASE_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("name").value("workspace-test"))
                .andExpect(jsonPath("language").value("ENG"))
                .andExpect(jsonPath("timezone").value("America/Sao_Paulo"));

        assertTrue(workspaceRepository.findAll().size() == 1);
    }

    @Test
    public void testCreateWorkspaceWithExistingName() throws Exception {
        WorkspaceCreateRequestDTO request = objectMapper
                .readValue(new File("src/test/resources/test-data/workspaces/workspace-create-request.json"),
                        WorkspaceCreateRequestDTO.class);
        mockMvc.perform(post(WorkspaceController.BASE_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(post(WorkspaceController.BASE_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateWorkspace() throws Exception {
        final String INSTANCE_NAME = "MY-DEV-INSTANCE1";
        InstancesRememberRequestDTO instancesRememberRequestDTO = new InstancesRememberRequestDTO();
        InstanceRememberRequestDTO instanceDTO = new InstanceRememberRequestDTO();
        instanceDTO.setName(INSTANCE_NAME);
        instanceDTO.setSocket("localhost:8080");
        instancesRememberRequestDTO.setInstances(Arrays.asList(instanceDTO));
        mockMvc.perform(post(InstanceController.BASE_NAME)
                .content(objectMapper.writeValueAsString(instancesRememberRequestDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        WorkspaceCreateRequestDTO createRequest = objectMapper
                .readValue(new File("src/test/resources/test-data/workspaces/workspace-create-request.json"),
                        WorkspaceCreateRequestDTO.class);
        createRequest.setMainDevelopmentInstanceName(INSTANCE_NAME);
        mockMvc.perform(post(WorkspaceController.BASE_NAME)
                .content(objectMapper.writeValueAsString(createRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        WorkspaceDTO updateRequest = objectMapper
                .readValue(new File("src/test/resources/test-data/workspaces/workspace-create-request.json"),
                        WorkspaceDTO.class);
        final String base64EncodedName = Base64.getEncoder()
                .encodeToString("workspace-test".getBytes());
        mockMvc.perform(patch(WorkspaceController.BASE_NAME + "/" + base64EncodedName)
                .content(objectMapper.writeValueAsString(updateRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(updateRequest.getName()))
                .andExpect(jsonPath("language").value(updateRequest.getLanguage()))
                .andExpect(jsonPath("timezone").value(updateRequest.getTimezone()));
    }

    @Test
    public void testGetWorkspace() throws Exception {
        WorkspaceCreateRequestDTO request = objectMapper
                .readValue(new File("src/test/resources/test-data/workspaces/workspace-create-request.json"),
                        WorkspaceCreateRequestDTO.class);
        mockMvc.perform(post(WorkspaceController.BASE_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        final String base64EncodedName = Base64.getEncoder()
                .encodeToString("workspace-test".getBytes());
        mockMvc.perform(get(WorkspaceController.BASE_NAME + "/" + base64EncodedName)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(request.getName()))
                .andExpect(jsonPath("language").value(request.getLanguage()))
                .andExpect(jsonPath("timezone").value(request.getTimezone()));
    }

    @Test
    public void testGetWorkspaceNotFound() throws Exception {
        final String base64EncodedName = Base64.getEncoder()
                .encodeToString("fake-workspace".getBytes());
        mockMvc.perform(get(WorkspaceController.BASE_NAME + "/" + base64EncodedName)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void test_getStageNames() throws Exception {
        final String base64EncodedName = Base64.getEncoder()
                .encodeToString("workspace-test".getBytes());
        mockMvc.perform(get(WorkspaceController.BASE_NAME + WorkspaceController.WORKSPACE_STAGES_ENDPOINT, base64EncodedName))
                .andExpect(status().isOk());

    }

    @Test
    void testGetPromotionPath() throws Exception {
        WorkspaceCreateRequestDTO workspaceCreateRequestDTO = createTestWorkspace();

        final String base64EncodedName = Base64.getEncoder().encodeToString(workspaceCreateRequestDTO.getName().getBytes());

        mockMvc.perform(get(WorkspaceController.BASE_NAME + "/" + WorkspaceController.WORKSPACE_PROMOTION_PATH_ENDPOINT, base64EncodedName)
                .accept(MediaType.APPLICATION_JSON)).andExpect(jsonPath("stages").isArray()).andExpect(jsonPath("stages", hasSize(1)))
                .andExpect(jsonPath("stages[0].name").value("Development"));
    }

    @Test
    void testUpdatePromotionPath() throws Exception {
        WorkspaceCreateRequestDTO workspaceCreateRequestDTO = createTestWorkspace();

        InstancesRememberRequestDTO instancesRememberRequestDTO = new InstancesRememberRequestDTO();
        InstanceRememberRequestDTO instanceDTO = new InstanceRememberRequestDTO();
        instanceDTO.setName("test-name");
        instanceDTO.setSocket("localhost:9999");
        instancesRememberRequestDTO.setInstances(Arrays.asList(instanceDTO));
        mockMvc.perform(post(InstanceController.BASE_NAME)
                .content(objectMapper.writeValueAsString(instancesRememberRequestDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        PromotionPathDTO request = objectMapper
                .readValue(new File("src/test/resources/test-data/workspaces/promotion-path-update-request.json"),
                        PromotionPathDTO.class);

        final String base64EncodedName = Base64.getEncoder().encodeToString(workspaceCreateRequestDTO.getName().getBytes());

        mockMvc.perform(patch(WorkspaceController.BASE_NAME + "/" + WorkspaceController.WORKSPACE_PROMOTION_PATH_ENDPOINT, base64EncodedName)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(jsonPath("stages").isArray()).andExpect(jsonPath("stages", hasSize(1)))
                .andExpect(jsonPath("stages[0].name").value("test-promotion-path"))
                .andExpect(jsonPath("stages[0].instances[0].name").value("test-name"));
    }

    @Test
    void testUpdatePromotionPathBadRequest() throws Exception {
        WorkspaceCreateRequestDTO workspaceCreateRequestDTO = createTestWorkspace();

        InstancesRememberRequestDTO instancesRememberRequestDTO = new InstancesRememberRequestDTO();
        InstanceRememberRequestDTO instanceDTO = new InstanceRememberRequestDTO();
        instanceDTO.setName("test-name");
        instanceDTO.setSocket("localhost:9999");

        instancesRememberRequestDTO.setInstances(Arrays.asList(instanceDTO));
        mockMvc.perform(post(InstanceController.BASE_NAME)
                .content(objectMapper.writeValueAsString(instancesRememberRequestDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        final InstanceDTO myInstanceDTO = new InstanceDTO();
        myInstanceDTO.setName("MY-DEV-INSTANCE");
        myInstanceDTO.setSocket("localhost:8080");

        PromotionPathDTO request = objectMapper
                .readValue(new File("src/test/resources/test-data/workspaces/promotion-path-update-request.json"),
                        PromotionPathDTO.class);

        request.getStages().get(0).getInstances().add(myInstanceDTO);

        final String base64EncodedName = Base64.getEncoder().encodeToString(workspaceCreateRequestDTO.getName().getBytes());

        mockMvc.perform(patch(WorkspaceController.BASE_NAME + "/" + WorkspaceController.WORKSPACE_PROMOTION_PATH_ENDPOINT, base64EncodedName)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    private WorkspaceCreateRequestDTO createTestWorkspace() throws Exception {
        WorkspaceCreateRequestDTO request = objectMapper
                .readValue(new File("src/test/resources/test-data/workspaces/workspace-create-request.json"),
                        WorkspaceCreateRequestDTO.class);

        mockMvc.perform(post(WorkspaceController.BASE_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        return request;
    }

}

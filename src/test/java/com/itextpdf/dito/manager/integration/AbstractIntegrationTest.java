package com.itextpdf.dito.manager.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.dito.manager.component.client.instance.InstanceClient;
import com.itextpdf.dito.manager.component.client.instance.impl.InstanceClientImpl;
import com.itextpdf.dito.manager.dto.instance.register.InstanceRegisterResponseDTO;
import com.itextpdf.dito.manager.entity.InstanceEntity;
import com.itextpdf.dito.manager.entity.PromotionPathEntity;
import com.itextpdf.dito.manager.entity.StageEntity;
import com.itextpdf.dito.manager.entity.WorkspaceEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;
import com.itextpdf.dito.manager.repository.instance.InstanceRepository;
import com.itextpdf.dito.manager.repository.stage.StageRepository;
import com.itextpdf.dito.manager.repository.workspace.WorkspaceRepository;
import com.itextpdf.dito.manager.service.instance.InstanceService;
import com.itextpdf.dito.manager.service.template.TemplateDeploymentService;
import com.itextpdf.dito.manager.service.user.UserService;
import org.aspectj.lang.annotation.After;
import org.bouncycastle.util.encoders.Base64;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

/**
 * Base class for integration tests.
 * Integration tests use in-memory H2 database initialized by application Liquibase scripts.
 * All properties should be set up in integration-test Spring profile.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integration-test")
@WithUserDetails("admin@email.com")
public abstract class AbstractIntegrationTest {
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    private InstanceService instanceService;
    @Autowired
    private TemplateDeploymentService templateDeploymentService;
    @Autowired
    private WorkspaceRepository workspaceRepository;
    @Autowired
    private InstanceRepository instanceRepository;
    @Autowired
    private StageRepository stageRepository;
    @Autowired
    private UserService userService;

    protected InstanceClient instanceClientMock;
    protected WorkspaceEntity defaultWorkspaceEntity;
    protected  PromotionPathEntity defaultPromotionPathEntity;
    protected InstanceEntity defaultInstanceEntity;

    @BeforeEach
    public void initMocks(){
        instanceClientMock = mock(InstanceClientImpl.class);
        ReflectionTestUtils.setField(instanceService, "instanceClient", instanceClientMock);
        ReflectionTestUtils.setField(templateDeploymentService, "instanceClient", instanceClientMock);

        InstanceRegisterResponseDTO sdkRegisterResponse = new InstanceRegisterResponseDTO();
        sdkRegisterResponse.setToken("test-token");
        when(instanceClientMock.register(any(String.class))).thenReturn(sdkRegisterResponse);

        generateDefaultPromotionPath();
    }

    @AfterEach
    public void clear(){
        stageRepository.deleteAll();
        instanceRepository.deleteAll();
        workspaceRepository.deleteAll();
    }

    protected ResultActions performPostFilesInteraction(URI uri, MockMultipartFile... files) throws Exception {
        MockMultipartHttpServletRequestBuilder multipartRequestBuilder = multipart(uri);
        for (int i = 0; i < files.length; i++) {
            multipartRequestBuilder = multipartRequestBuilder.file(files[i]);
        }
        return this.mockMvc.perform(multipartRequestBuilder);
    }

    private void generateDefaultPromotionPath(){
        final StageEntity defaultStageEntity = new StageEntity();
        defaultStageEntity.setSequenceOrder(0);
        defaultStageEntity.setName("DEV");
        defaultPromotionPathEntity = new PromotionPathEntity();
        defaultStageEntity.setPromotionPath(defaultPromotionPathEntity);
        defaultStageEntity.setPromotionPath(defaultPromotionPathEntity);
        defaultWorkspaceEntity = new WorkspaceEntity();
        defaultPromotionPathEntity.setWorkspace(defaultWorkspaceEntity);
        defaultWorkspaceEntity.setName("workspace-test");
        defaultWorkspaceEntity.setTimezone("Europe/Brussels");
        defaultWorkspaceEntity.setLanguage("ENG");
        defaultWorkspaceEntity.setPromotionPath(defaultPromotionPathEntity);
        workspaceRepository.save(defaultWorkspaceEntity);
        defaultInstanceEntity = new InstanceEntity();
        defaultInstanceEntity.setName("default-instance");
        defaultInstanceEntity.setSocket("socket-2");
        defaultInstanceEntity.setStage(defaultStageEntity);
        defaultInstanceEntity.setCreatedOn(new Date());
        defaultInstanceEntity.setCreatedBy(userService.findByEmail("admin@email.com"));
        instanceRepository.save(defaultInstanceEntity);
        stageRepository.save(defaultStageEntity);
    }

    protected String encodeStringToBase64(String value) {
        return new String(Base64.encode(value.getBytes()));
    }
}

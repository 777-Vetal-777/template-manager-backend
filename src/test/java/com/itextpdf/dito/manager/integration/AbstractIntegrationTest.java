package com.itextpdf.dito.manager.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.dito.manager.component.client.instance.InstanceClient;
import com.itextpdf.dito.manager.component.client.instance.impl.InstanceClientImpl;
import com.itextpdf.dito.manager.component.encoder.Encoder;
import com.itextpdf.dito.manager.dto.instance.register.InstanceRegisterResponseDTO;
import com.itextpdf.dito.manager.entity.InstanceEntity;
import com.itextpdf.dito.manager.entity.PromotionPathEntity;
import com.itextpdf.dito.manager.entity.StageEntity;
import com.itextpdf.dito.manager.entity.WorkspaceEntity;
import com.itextpdf.dito.manager.repository.instance.InstanceRepository;
import com.itextpdf.dito.manager.repository.stage.StageRepository;
import com.itextpdf.dito.manager.repository.workspace.WorkspaceRepository;
import com.itextpdf.dito.manager.service.instance.InstanceService;
import com.itextpdf.dito.manager.service.template.TemplateDeploymentService;
import com.itextpdf.dito.manager.service.user.UserService;

import org.bouncycastle.util.encoders.Base64;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.hamcrest.MatcherAssert.assertThat;
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
    @Autowired
    private Encoder encoder;

    protected InstanceClient instanceClientMock;
    protected WorkspaceEntity defaultWorkspaceEntity;
    protected  PromotionPathEntity defaultPromotionPathEntity;
    protected InstanceEntity defaultInstanceEntity;

    protected static byte[] readFileBytes(final String uri) {
        byte[] result;

        try {
            result = Files.readAllBytes(Path.of(uri));
        } catch (IOException e) {
            result = new byte[] {};
        }

        return result;
    }

    @BeforeEach
    public void initMocks(){
        instanceClientMock = mock(InstanceClientImpl.class);
        ReflectionTestUtils.setField(instanceService, "instanceClient", instanceClientMock);
        ReflectionTestUtils.setField(templateDeploymentService, "instanceClient", instanceClientMock);

        InstanceRegisterResponseDTO sdkRegisterResponse = new InstanceRegisterResponseDTO();
        sdkRegisterResponse.setToken("test-token");
        when(instanceClientMock.register(any(String.class), any(), any())).thenReturn(sdkRegisterResponse);

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
        for (MockMultipartFile file : files) {
            multipartRequestBuilder = multipartRequestBuilder.file(file);
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
        defaultInstanceEntity.setCreatedBy(userService.findActiveUserByEmail("admin@email.com"));
        instanceRepository.save(defaultInstanceEntity);
        stageRepository.save(defaultStageEntity);
    }

    protected String encodeStringToBase64(String value) {
        return encoder.encode(value);
    }

    @SafeVarargs
    protected final ResultMatcher zipMatch(Matcher<Iterable<? super String>>... entries) {
        return mvcResult -> {
            final List<String> zipEntries = new LinkedList<>();
            try (final ZipInputStream zipStream = new ZipInputStream(new ByteArrayInputStream(mvcResult.getResponse().getContentAsByteArray()))) {
                ZipEntry ze;
                while ((ze = zipStream.getNextEntry()) != null) {
                    //replace used for Windows machines
                    zipEntries.add(ze.getName().replace('\\', '/'));
                }
            }
            for (Matcher<Iterable<? super String>> entry : entries) {
                assertThat(zipEntries, entry);
            }
        };
    }
}

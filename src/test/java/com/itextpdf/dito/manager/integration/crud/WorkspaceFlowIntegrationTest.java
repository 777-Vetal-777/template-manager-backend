package com.itextpdf.dito.manager.integration.crud;

import com.itextpdf.dito.manager.controller.instance.InstanceController;
import com.itextpdf.dito.manager.controller.workspace.WorkspaceController;
import com.itextpdf.dito.manager.dto.instance.InstanceDTO;
import com.itextpdf.dito.manager.dto.instance.create.InstanceRememberRequestDTO;
import com.itextpdf.dito.manager.dto.instance.create.InstancesRememberRequestDTO;
import com.itextpdf.dito.manager.dto.promotionpath.PromotionPathDTO;
import com.itextpdf.dito.manager.dto.workspace.WorkspaceDTO;
import com.itextpdf.dito.manager.dto.workspace.create.WorkspaceCreateRequestDTO;
import com.itextpdf.dito.manager.entity.LicenseEntity;
import com.itextpdf.dito.manager.entity.WorkspaceEntity;
import com.itextpdf.dito.manager.integration.AbstractIntegrationTest;
import com.itextpdf.dito.manager.repository.instance.InstanceRepository;
import com.itextpdf.dito.manager.repository.license.LicenseRepository;
import com.itextpdf.dito.manager.repository.workspace.WorkspaceRepository;
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
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Collections;
import java.util.Optional;

import static com.itextpdf.dito.manager.controller.workspace.WorkspaceController.WORKSPACE_CHECK_LICENSE_ENDPOINT;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class WorkspaceFlowIntegrationTest extends AbstractIntegrationTest {
    private static final String WORKSPACE_NAME = "workspace-test";
    private static final String WORKSPACE_TIMEZONE = "America/Sao_Paulo";
    private static final String WORKSPACE_LANGUAGE = "ENG";
    private static final String WORKSPACE_ADJUST_FOR_DAYLIGHT = "Test";
    private static final String MAIN_DEVELOP_INSTANCE_NAME = "MY-DEV-INSTANCE";

    private final MockMultipartFile licensePart = new MockMultipartFile("license", "volume-andersen.xml", "text/xml", Files.readAllBytes(Paths.get("src/test/resources/test-data/license/volume-andersen.xml")));
    private final MockMultipartFile workspaceNamePart = new MockMultipartFile("name", WORKSPACE_NAME, "text/xml", WORKSPACE_NAME.getBytes(StandardCharsets.UTF_8));
    private final MockMultipartFile workspaceTimeZonePart = new MockMultipartFile("timezone", WORKSPACE_TIMEZONE, "text/xml", WORKSPACE_TIMEZONE.getBytes(StandardCharsets.UTF_8));
    private final MockMultipartFile workspaceLanguagePart = new MockMultipartFile("language", WORKSPACE_LANGUAGE, "text/xml", WORKSPACE_LANGUAGE.getBytes(StandardCharsets.UTF_8));
    private final MockMultipartFile workspaceAdjustForDayLightPart = new MockMultipartFile("adjustForDaylight", WORKSPACE_ADJUST_FOR_DAYLIGHT, "text/xml", WORKSPACE_ADJUST_FOR_DAYLIGHT.getBytes(StandardCharsets.UTF_8));
    private final MockMultipartFile mainDevelopInstancePart = new MockMultipartFile("mainDevelopInstance", MAIN_DEVELOP_INSTANCE_NAME, "text/xml", MAIN_DEVELOP_INSTANCE_NAME.getBytes(StandardCharsets.UTF_8));

    private final static URI workspaceBaseUri = UriComponentsBuilder.fromUriString(WorkspaceController.BASE_NAME).build().encode().toUri();
    private static final String WORKSPACE_BASE64_ENCODED_NAME = com.itextpdf.kernel.xmp.impl.Base64.encode(WORKSPACE_NAME);

    @Autowired
    private WorkspaceRepository workspaceRepository;
    @Autowired
    private InstanceRepository instanceRepository;
    @Autowired
    private LicenseRepository licenseRepository;

    WorkspaceFlowIntegrationTest() throws IOException {
    }

    @AfterEach
    public void teardown() {
        workspaceRepository.deleteAll();
        instanceRepository.deleteAll();
    }

    @BeforeEach
    void tearUp() throws Exception {
        final InstancesRememberRequestDTO instancesRememberRequestDTO = objectMapper.readValue(new File("src/test/resources/test-data/workspaces/instances-create-request.json"), InstancesRememberRequestDTO.class);
        final MvcResult result = mockMvc.perform(post(InstanceController.BASE_NAME)
                .content(objectMapper.writeValueAsString(instancesRememberRequestDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        assertNotNull(result.getResponse());
    }

    @Test
    void testShouldGetAllWorkspaces() throws Exception {
        final MvcResult result = mockMvc.perform(get(WorkspaceController.BASE_NAME)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].name").value(WORKSPACE_NAME))
                .andExpect(jsonPath("$.[0].timezone").isNotEmpty())
                .andExpect(jsonPath("$.[0].language").value(WORKSPACE_LANGUAGE))
                .andExpect(jsonPath("$.[0].adjustForDaylight").isNotEmpty())
                .andReturn();
        assertNotNull(result.getResponse());
    }

    @Test
    void testShouldCheckWorkspaceIsExistByName() throws Exception {
        final String base64EncodedName = Base64.getEncoder().encodeToString("workspace-test".getBytes());
        final URI uriWithValidName = UriComponentsBuilder.fromUriString(WorkspaceController.BASE_NAME + "/" + base64EncodedName + "/exist").build().encode().toUri();
        mockMvc.perform(get(uriWithValidName)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        final String base64BadWorkspaceEncodedName = Base64.getEncoder().encodeToString("!@#!@#BadWorkspace".getBytes());
        final URI uriWithBadName = UriComponentsBuilder.fromUriString(WorkspaceController.BASE_NAME + "/" + base64BadWorkspaceEncodedName + "/exist").build().encode().toUri();
        final MvcResult result = mockMvc.perform(get(uriWithBadName)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false))
                .andReturn();
        assertNotNull(result.getResponse());
    }

    @Test
    void testLicenceShouldCheckValidLicenseSuccessfully() throws Exception {
        final URI uri = UriComponentsBuilder
                .fromUriString(WorkspaceController.BASE_NAME + WORKSPACE_CHECK_LICENSE_ENDPOINT).build().encode().toUri();
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart(uri).file(licensePart).contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk()).andReturn();
        assertNotNull(result.getResponse());
    }

    @Test
    void testCreateWorkspace() throws Exception {
        createWorkspace(workspaceNamePart);
        final Optional<WorkspaceEntity> optionalCreatedWorkspace = workspaceRepository.findByName(WORKSPACE_NAME);
        assertTrue(optionalCreatedWorkspace.isPresent());
        final WorkspaceEntity workspaceEntity = optionalCreatedWorkspace.get();
        Optional<LicenseEntity> optionalLicense = licenseRepository.findByWorkspace(workspaceEntity);
        assertTrue(optionalLicense.isPresent());
    }

    @Test
    void testCreateWorkspaceWithExistingName() throws Exception {
        createWorkspace(workspaceNamePart);

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart(workspaceBaseUri)
                .file(workspaceNamePart)
                .file(workspaceTimeZonePart)
                .file(workspaceLanguagePart)
                .file(mainDevelopInstancePart)
                .file(licensePart)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()).andReturn();
        assertNotNull(result.getResponse());
    }

    @Test
    void testCreateWorkspaceWithWrongLicense() throws Exception {
        final MockMultipartFile badLicense = new MockMultipartFile("license", "volume-andersen.xml", "text/xml", "Wrong staff".getBytes(StandardCharsets.UTF_8));

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart(workspaceBaseUri)
                .file(workspaceNamePart)
                .file(workspaceTimeZonePart)
                .file(workspaceLanguagePart)
                .file(mainDevelopInstancePart)
                .file(badLicense)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        assertNotNull(result.getResponse());
    }

    @Test
    void shouldNotCreateWorkspaceWithoutInstance() throws Exception {
        final MockMultipartFile notExistingInstance = new MockMultipartFile("unknownInstance", MAIN_DEVELOP_INSTANCE_NAME, "text/xml", MAIN_DEVELOP_INSTANCE_NAME.getBytes(StandardCharsets.UTF_8));

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart(workspaceBaseUri)
                .file(workspaceNamePart)
                .file(workspaceTimeZonePart)
                .file(workspaceLanguagePart)
                .file(workspaceAdjustForDayLightPart)
                .file(notExistingInstance)
                .file(licensePart)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        assertNotNull(result.getResponse());
    }

    @Test
    void testUpdateWorkspace() throws Exception {
        final String INSTANCE_NAME = "MY-DEV-INSTANCE1";
        final String INSTANCE_SOCKET = "localhost:8090";
        InstancesRememberRequestDTO instancesRememberRequestDTO = new InstancesRememberRequestDTO();
        InstanceRememberRequestDTO instanceDTO = new InstanceRememberRequestDTO();
        instanceDTO.setName(INSTANCE_NAME);
        instanceDTO.setSocket(INSTANCE_SOCKET);
        instancesRememberRequestDTO.setInstances(Collections.singletonList(instanceDTO));
        mockMvc.perform(post(InstanceController.BASE_NAME)
                .content(objectMapper.writeValueAsString(instancesRememberRequestDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        final WorkspaceDTO updateRequest = objectMapper.readValue(new File("src/test/resources/test-data/workspaces/workspace-create-request.json"), WorkspaceDTO.class);
        final MvcResult result = mockMvc.perform(patch(WorkspaceController.BASE_NAME + "/" + WORKSPACE_BASE64_ENCODED_NAME)
                .content(objectMapper.writeValueAsString(updateRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(updateRequest.getName()))
                .andExpect(jsonPath("language").value(updateRequest.getLanguage()))
                .andExpect(jsonPath("timezone").value(updateRequest.getTimezone())).andReturn();
        assertNotNull(result.getResponse());
    }

    @Test
    void testGetWorkspace() throws Exception {
        final String base64EncodedName = Base64.getEncoder()
                .encodeToString("workspace-test".getBytes());
        final MvcResult result = mockMvc.perform(get(WorkspaceController.BASE_NAME + "/" + base64EncodedName)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").isNotEmpty())
                .andExpect(jsonPath("language").isNotEmpty())
                .andExpect(jsonPath("timezone").isNotEmpty()).andReturn();
        assertNotNull(result.getResponse());
    }

    @Test
    void testGetWorkspaceNotFound() throws Exception {
        final String base64EncodedName = Base64.getEncoder()
                .encodeToString("fake-workspace".getBytes());
        final MvcResult result = mockMvc.perform(get(WorkspaceController.BASE_NAME + "/" + base64EncodedName)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()).andReturn();
        assertNotNull(result.getResponse());
    }

    @Test
    void test_getStageNames() throws Exception {
        final String base64EncodedName = Base64.getEncoder()
                .encodeToString("workspace-test".getBytes());
        final MvcResult result = mockMvc.perform(get(WorkspaceController.BASE_NAME + WorkspaceController.WORKSPACE_STAGES_ENDPOINT, base64EncodedName))
                .andExpect(status().isOk()).andReturn();
        assertNotNull(result.getResponse());
    }

    @Test
    void testGetPromotionPath() throws Exception {
        createWorkspace(workspaceNamePart);
        final MvcResult result = mockMvc.perform(get(WorkspaceController.BASE_NAME + "/" + WorkspaceController.WORKSPACE_PROMOTION_PATH_ENDPOINT, WORKSPACE_BASE64_ENCODED_NAME)
                .accept(MediaType.APPLICATION_JSON)).andExpect(jsonPath("stages").isArray()).andExpect(jsonPath("stages", hasSize(1)))
                .andExpect(jsonPath("stages[0].name").value("Development")).andReturn();

        assertNotNull(result.getResponse());
    }

    @Test
    void testUploadLicenseOnNonexistentWorkspace() throws Exception {
        workspaceRepository.deleteAll();
        MockMultipartFile licensePart2 = new MockMultipartFile("license", "volume-andersen.xml", "text/xml", "".getBytes());
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart(workspaceBaseUri)
                .file(workspaceNamePart)
                .file(workspaceTimeZonePart)
                .file(workspaceLanguagePart)
                .file(workspaceAdjustForDayLightPart)
                .file(mainDevelopInstancePart)
                .file(licensePart2)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()).andReturn();

        assertNotNull(result.getResponse());
    }

    @Test
    void testUpdatePromotionPath() throws Exception {
        createWorkspace(workspaceNamePart);

        final InstancesRememberRequestDTO instancesRememberRequestDTO = new InstancesRememberRequestDTO();
        final InstanceRememberRequestDTO instanceDTO = new InstanceRememberRequestDTO();
        instanceDTO.setName("test-name");
        instanceDTO.setSocket("localhost:9999");
        instancesRememberRequestDTO.setInstances(Collections.singletonList(instanceDTO));
        mockMvc.perform(post(InstanceController.BASE_NAME)
                .content(objectMapper.writeValueAsString(instancesRememberRequestDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        final PromotionPathDTO request = objectMapper
                .readValue(new File("src/test/resources/test-data/workspaces/promotion-path-update-request.json"), PromotionPathDTO.class);

        final MvcResult result = mockMvc.perform(patch(WorkspaceController.BASE_NAME + "/" + WorkspaceController.WORKSPACE_PROMOTION_PATH_ENDPOINT, WORKSPACE_BASE64_ENCODED_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(jsonPath("stages").isArray()).andExpect(jsonPath("stages", hasSize(1)))
                .andExpect(jsonPath("stages[0].name").value("test-promotion-path"))
                .andExpect(jsonPath("stages[0].instances[0].name").value("test-name")).andReturn();
        assertNotNull(result.getResponse());
    }

    @Test
    void testUpdatePromotionPathBadRequest() throws Exception {
        final WorkspaceCreateRequestDTO workspaceCreateRequestDTO = createTestWorkspace();

        final InstancesRememberRequestDTO instancesRememberRequestDTO = new InstancesRememberRequestDTO();
        InstanceRememberRequestDTO instanceDTO = new InstanceRememberRequestDTO();
        instanceDTO.setName("test-name");
        instanceDTO.setSocket("localhost:9999");
        instancesRememberRequestDTO.setInstances(Collections.singletonList(instanceDTO));
        mockMvc.perform(post(InstanceController.BASE_NAME)
                .content(objectMapper.writeValueAsString(instancesRememberRequestDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        final InstanceDTO myInstanceDTO = new InstanceDTO();
        myInstanceDTO.setName("MY-DEV-INSTANCE");
        myInstanceDTO.setSocket("localhost:8080");

        final PromotionPathDTO request = objectMapper
                .readValue(new File("src/test/resources/test-data/workspaces/promotion-path-update-request.json"),
                        PromotionPathDTO.class);

        request.getStages().get(0).getInstances().add(myInstanceDTO);

        final String base64EncodedName = Base64.getEncoder().encodeToString(workspaceCreateRequestDTO.getName().getBytes());

        final MvcResult result = mockMvc.perform(patch(WorkspaceController.BASE_NAME + "/" + WorkspaceController.WORKSPACE_PROMOTION_PATH_ENDPOINT, base64EncodedName)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest()).andReturn();
        assertNotNull(result.getResponse());
    }

    private WorkspaceCreateRequestDTO createTestWorkspace() throws Exception {
        final WorkspaceCreateRequestDTO request = objectMapper
                .readValue(new File("src/test/resources/test-data/workspaces/workspace-create-request.json"),
                        WorkspaceCreateRequestDTO.class);

        mockMvc.perform(post(WorkspaceController.BASE_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        return request;
    }

    private void createWorkspace(final MockMultipartFile workspaceNamePart) throws Exception {
        workspaceRepository.deleteAll();
        mockMvc.perform(MockMvcRequestBuilders.multipart(workspaceBaseUri)
                .file(workspaceNamePart)
                .file(workspaceTimeZonePart)
                .file(workspaceLanguagePart)
                .file(workspaceAdjustForDayLightPart)
                .file(mainDevelopInstancePart)
                .file(licensePart)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("name").value(WORKSPACE_NAME))
                .andExpect(jsonPath("language").value("ENG"))
                .andExpect(jsonPath("timezone").value("America/Sao_Paulo"))
                .andExpect(jsonPath("adjustForDaylight").value(true));
    }

    @Test
    public void test_remember_success_with_custom_header() throws Exception {
        final InstancesRememberRequestDTO request = objectMapper.readValue(new File("src/test/resources/test-data/instances/instances-remember-request.json"), InstancesRememberRequestDTO.class);
        request.getInstances().get(0).setHeaderName("MyHeader");
        request.getInstances().get(0).setHeaderValue("MyHeaderValue");
        mockMvc.perform(post(InstanceController.BASE_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].socket", is(request.getInstances().get(0).getSocket())))
                .andExpect(jsonPath("$.[0].headerName", is("MyHeader")))
                .andExpect(jsonPath("$.[0].headerValue", is("MyHeaderValue")));
        assertFalse(instanceRepository.findByName(request.getInstances().get(0).getName()).isEmpty());
    }

    @Test
    public void test_remember_fail_with_custom_header() throws Exception {
        final InstancesRememberRequestDTO request = objectMapper.readValue(new File("src/test/resources/test-data/instances/instances-remember-request.json"), InstancesRememberRequestDTO.class);
        request.getInstances().get(0).setHeaderName("MyHeader");
        request.getInstances().get(0).setHeaderValue("");
        mockMvc.perform(post(InstanceController.BASE_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        request.getInstances().get(0).setHeaderName("");
        request.getInstances().get(0).setHeaderValue("MyHeaderValue");
        mockMvc.perform(post(InstanceController.BASE_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        request.getInstances().get(0).setHeaderName(null);
        request.getInstances().get(0).setHeaderValue("MyHeaderValue");
        mockMvc.perform(post(InstanceController.BASE_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        request.getInstances().get(0).setHeaderName("MyHeader");
        request.getInstances().get(0).setHeaderValue(null);
        mockMvc.perform(post(InstanceController.BASE_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        assertTrue(instanceRepository.findByName(request.getInstances().get(0).getName()).isEmpty());
    }
}
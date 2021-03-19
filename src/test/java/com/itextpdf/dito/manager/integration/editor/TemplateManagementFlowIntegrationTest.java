package com.itextpdf.dito.manager.integration.editor;

import com.itextpdf.dito.editor.server.common.core.descriptor.TemplateAddDescriptor;
import com.itextpdf.dito.editor.server.common.core.descriptor.TemplateFragmentType;
import com.itextpdf.dito.manager.controller.template.TemplateController;
import com.itextpdf.dito.manager.dto.template.create.TemplateCreateRequestDTO;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;
import com.itextpdf.dito.manager.integration.AbstractIntegrationTest;
import com.itextpdf.dito.manager.integration.editor.controller.template.TemplateManagementController;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionRepository;
import com.itextpdf.dito.manager.repository.instance.InstanceRepository;
import com.itextpdf.dito.manager.repository.stage.StageRepository;
import com.itextpdf.dito.manager.repository.template.TemplateFileRepository;
import com.itextpdf.dito.manager.repository.template.TemplateRepository;
import com.itextpdf.dito.manager.repository.workspace.WorkspaceRepository;
import com.itextpdf.dito.manager.service.template.TemplateLoader;
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
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TemplateManagementFlowIntegrationTest extends AbstractIntegrationTest {
	
	private static final String WORKSPACE_ID = "c29tZS10ZW1wbGF0ZQ==";
	private static final String TEMPLATE_NAME = "some-template";
	private static final String TEMPLATE_ID = Base64.getUrlEncoder().encodeToString(TEMPLATE_NAME.getBytes(StandardCharsets.UTF_8));

	@Autowired
	private TemplateRepository templateRepository;
	@Autowired
	private TemplateFileRepository templateFileRepository;
	@Autowired
	private DataCollectionRepository dataCollectionRepository;
	@Autowired
	private TemplateLoader templateLoader;

	@AfterEach
	void clearDb() {
		templateRepository.deleteAll();
		templateFileRepository.deleteAll();
		dataCollectionRepository.deleteAll();
	}
	
	@Test
	void templateAddFirstVersionOfTemplateTest() throws Exception {
		// CREATE
		final TemplateAddDescriptor templateAddDescriptor = new TemplateAddDescriptor(TEMPLATE_NAME, TemplateFragmentType.STANDARD);
		
		final URI uri = UriComponentsBuilder.fromUriString("/workspace/" + WORKSPACE_ID + "/templates").build()
				.encode().toUri();

		final MockMultipartFile descriptor = new MockMultipartFile("descriptor", "descriptor", "application/json",
				objectMapper.writeValueAsString(templateAddDescriptor).getBytes());
		final MockMultipartFile data = new MockMultipartFile("data", "data", "application/json", templateLoader.load());

		mockMvc.perform(MockMvcRequestBuilders.multipart(uri)
				.file(descriptor)
				.file(data)
				.contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		// UPDATE
		final URI uriUpdate = UriComponentsBuilder.fromUriString("/templates/" + TEMPLATE_ID)
				.build().encode().toUri();

		final MockMultipartFile newData = new MockMultipartFile("data", "data", "application/json",
				"{\"file\":\"NewData\"}".getBytes());
		
		mockMvc.perform(MockMvcRequestBuilders.multipart(uriUpdate)
				.file(descriptor)
				.file(newData)
				.contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
		// CHECK UPDATE
		mockMvc.perform(get(uriUpdate)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("file").value("NewData"));

		final List<TemplateFileEntity> templateFileList = templateFileRepository.findAll();
		assertEquals(1,templateFileList.size());
		assertEquals(1L, templateFileList.get(0).getVersion());
		assertNull(templateFileList.get(0).getComment());
	}
	
	@Test
	void templateAddUpdateTest() throws Exception {
		// CREATE
		final TemplateAddDescriptor templateAddDescriptor = new TemplateAddDescriptor(TEMPLATE_NAME, TemplateFragmentType.STANDARD);
		
		final URI uri = UriComponentsBuilder.fromUriString("/workspace/" + WORKSPACE_ID + "/templates").build()
				.encode().toUri();

		final MockMultipartFile descriptor = new MockMultipartFile("descriptor", "descriptor", "application/json",
				objectMapper.writeValueAsString(templateAddDescriptor).getBytes());
		final MockMultipartFile data = new MockMultipartFile("data", "data", "application/json",
				templateLoader.load());

		mockMvc.perform(MockMvcRequestBuilders.multipart(uri)
				.file(descriptor)
				.file(data)
				.contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		// UPDATE
		final URI uriUpdate = UriComponentsBuilder.fromUriString("/templates/" + TEMPLATE_ID)
				.build().encode().toUri();

		final MockMultipartFile newDataForFirstUpdate = new MockMultipartFile("data", "data", "application/json",
				"{\"file\":\"NewDataFirst\"}".getBytes());

		mockMvc.perform(MockMvcRequestBuilders.multipart(uriUpdate)
				.file(descriptor)
				.file(newDataForFirstUpdate)
				.contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
		templateFileRepository.findAll().forEach(t->System.out.println(new String(t.getData())));
		final MockMultipartFile newDataForSecondUpdate = new MockMultipartFile("data", "data", "application/json",
				"{\"file\":\"NewDataSecond\"}".getBytes());

		mockMvc.perform(MockMvcRequestBuilders.multipart(uriUpdate)
				.file(descriptor)
				.file(newDataForSecondUpdate)
				.contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
		templateFileRepository.findAll().forEach(t->System.out.println(new String(t.getData())));
		
		// CHECK UPDATE
		mockMvc.perform(get(uriUpdate)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("file").value("NewDataSecond"));
		
		final List<TemplateFileEntity> templateFileList = templateFileRepository.findAll();
		assertEquals(2, templateFileList.size());
		assertEquals(1L, templateFileList.get(0).getVersion());
		assertEquals(2L, templateFileList.get(1).getVersion());
	}
	
	@Test
	public void testGetDeleteTemplate() throws Exception {
		final TemplateCreateRequestDTO request = objectMapper.readValue(
				new File("src/test/resources/test-data/templates/template-create-request.json"),
				TemplateCreateRequestDTO.class);
		mockMvc.perform(post(TemplateController.BASE_NAME)
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());

		final URI integrationWorkspaceUri = UriComponentsBuilder.fromUriString("/workspace/" + WORKSPACE_ID + "/templates")
				.build().encode().toUri();

		// get integrated templates collection
		mockMvc.perform(get(integrationWorkspaceUri)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(TEMPLATE_ID))
				.andExpect(jsonPath("$[0].displayName").value(TEMPLATE_NAME))
				.andExpect(jsonPath("$[0].type").value("outputTemplate"));
		
		final URI integrationTemplateUri = UriComponentsBuilder.fromUriString("/templates/" + TEMPLATE_ID)
				.build().encode().toUri();
		mockMvc.perform(get(integrationTemplateUri)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_OCTET_STREAM))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("<meta data-dito-template-type=\"output\" />")));
	
		final URI integrationTemplateDescriptorUri = UriComponentsBuilder.fromUriString("/templates/" + TEMPLATE_ID+"/descriptor")
				.build().encode().toUri();
		mockMvc.perform(get(integrationTemplateDescriptorUri)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("id").value(TEMPLATE_ID))
				.andExpect(jsonPath("displayName").value(TEMPLATE_NAME))
				.andExpect(jsonPath("type").value("outputTemplate"));

		final MvcResult result = mockMvc.perform(delete(integrationTemplateUri)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();
		assertNotNull(result.getResponse());
	}

	@Test
	void shouldProcessTemplateWithEmptyImageTag() throws Exception {
		// CREATE
		final TemplateAddDescriptor templateAddDescriptor = new TemplateAddDescriptor(TEMPLATE_NAME, TemplateFragmentType.STANDARD);

		final MockMultipartFile descriptor = new MockMultipartFile("descriptor", "descriptor", "application/json", objectMapper.writeValueAsString(templateAddDescriptor).getBytes());
		final MockMultipartFile data = new MockMultipartFile("data", "data", "application/json", readFileBytes("src/test/resources/test-data/templates/editor-template-with-empty-image.html"));

		mockMvc.perform(MockMvcRequestBuilders.multipart(TemplateManagementController.CREATE_TEMPLATE_URL, WORKSPACE_ID)
				.file(descriptor)
				.file(data)
				.contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		// UPDATE
		mockMvc.perform(MockMvcRequestBuilders.multipart(TemplateManagementController.TEMPLATE_URL, TEMPLATE_ID)
				.file(descriptor)
				.file(data)
				.contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		final List<TemplateFileEntity> templateFileList = templateFileRepository.findAll();
		assertEquals(2, templateFileList.size());
		assertEquals(1L, templateFileList.get(0).getVersion());
		assertEquals(2L, templateFileList.get(1).getVersion());
		assertNull(templateFileList.get(0).getComment());
	}

}

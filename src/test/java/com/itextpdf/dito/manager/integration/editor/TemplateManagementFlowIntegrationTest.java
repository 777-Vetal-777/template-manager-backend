package com.itextpdf.dito.manager.integration.editor;

import com.itextpdf.dito.editor.server.common.core.descriptor.TemplateAddDescriptor;
import com.itextpdf.dito.editor.server.common.core.descriptor.TemplateFragmentType;
import com.itextpdf.dito.manager.controller.template.TemplateController;
import com.itextpdf.dito.manager.dto.template.create.TemplateCreateRequestDTO;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;
import com.itextpdf.dito.manager.integration.AbstractIntegrationTest;
import com.itextpdf.dito.manager.integration.editor.controller.template.TemplateManagementController;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionRepository;
import com.itextpdf.dito.manager.repository.template.TemplateFileRepository;
import com.itextpdf.dito.manager.repository.template.TemplateRepository;
import com.itextpdf.dito.manager.service.template.TemplateLoader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.net.URI;
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
		final TemplateEntity templateEntity = templateRepository.findByName(TEMPLATE_NAME).orElseThrow();
		final URI uriUpdate = UriComponentsBuilder.fromUriString(TemplateManagementController.TEMPLATE_URL).build(templateEntity.getUuid());

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
		
		final URI uri = UriComponentsBuilder.fromUriString(TemplateManagementController.CREATE_TEMPLATE_URL).build(WORKSPACE_ID);

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
		final TemplateEntity templateEntity = templateRepository.findByName(TEMPLATE_NAME).orElseThrow();
		final URI uriUpdate = UriComponentsBuilder.fromUriString(TemplateManagementController.TEMPLATE_URL).build(templateEntity.getUuid());

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

		final TemplateEntity templateEntity = templateRepository.findByName(TEMPLATE_NAME).orElseThrow();
		assertNotNull(templateEntity.getUuid());

		// get integrated templates collection
		mockMvc.perform(get(TemplateManagementController.TEMPLATE_LIST_URL, WORKSPACE_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(templateEntity.getUuid()))
				.andExpect(jsonPath("$[0].displayName").value(TEMPLATE_NAME))
				.andExpect(jsonPath("$[0].fragmentType").value("STANDARD"));
		
		mockMvc.perform(get(TemplateManagementController.TEMPLATE_URL, templateEntity.getUuid())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_OCTET_STREAM))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("<body data-dito-element=\"subform\">")));
	
		mockMvc.perform(get(TemplateManagementController.TEMPLATE_DESCRIPTOR_URL, templateEntity.getUuid())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("id").value(templateEntity.getUuid()))
				.andExpect(jsonPath("displayName").value(TEMPLATE_NAME))
				.andExpect(jsonPath("fragmentType").value("STANDARD"));

		mockMvc.perform(delete(TemplateManagementController.TEMPLATE_URL, templateEntity.getUuid())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
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

		final TemplateEntity templateEntity = templateRepository.findByName(TEMPLATE_NAME).orElseThrow();

		// UPDATE
		mockMvc.perform(MockMvcRequestBuilders.multipart(TemplateManagementController.TEMPLATE_URL, templateEntity.getUuid())
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

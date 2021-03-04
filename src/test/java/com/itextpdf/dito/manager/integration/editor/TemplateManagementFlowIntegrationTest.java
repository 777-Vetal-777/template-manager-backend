package com.itextpdf.dito.manager.integration.editor;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.net.URI;
import java.util.List;

import com.itextpdf.kernel.xmp.impl.Base64;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.web.util.UriComponentsBuilder;

import com.itextpdf.dito.editor.server.common.core.descriptor.TemplateAddDescriptor;
import com.itextpdf.dito.editor.server.common.core.descriptor.TemplateFragmentType;
import com.itextpdf.dito.manager.controller.template.TemplateController;
import com.itextpdf.dito.manager.dto.template.create.TemplateCreateRequestDTO;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;
import com.itextpdf.dito.manager.integration.AbstractIntegrationTest;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionRepository;
import com.itextpdf.dito.manager.repository.instance.InstanceRepository;
import com.itextpdf.dito.manager.repository.stage.StageRepository;
import com.itextpdf.dito.manager.repository.template.TemplateFileRepository;
import com.itextpdf.dito.manager.repository.template.TemplateRepository;
import com.itextpdf.dito.manager.repository.workspace.WorkspaceRepository;

public class TemplateManagementFlowIntegrationTest extends AbstractIntegrationTest {
	
	private static final String WORKSPACE_ID = "c29tZS10ZW1wbGF0ZQ==";
	private static final String TEMPLATE_NAME = "some-template";
	private static final String TEMPLATE_ID = Base64.encode(TEMPLATE_NAME);

	@Autowired
	private TemplateRepository templateRepository;
	@Autowired
	private TemplateFileRepository templateFileRepository;
	@Autowired
	private DataCollectionRepository dataCollectionRepository;
	@Autowired
	private WorkspaceRepository workspaceRepository;
	@Autowired
	private InstanceRepository instanceRepository;
	@Autowired
	private StageRepository stageRepository;

	@BeforeEach
	public void clearDb() {
		templateRepository.deleteAll();
		templateFileRepository.deleteAll();
		dataCollectionRepository.deleteAll();
		workspaceRepository.deleteAll();
		instanceRepository.deleteAll();
		stageRepository.deleteAll();
	}
	
	@Test
	public void templateAddFirstVersionOfTemplateTest() throws Exception {
		// CREATE
		final TemplateAddDescriptor descriptor=  new TemplateAddDescriptor(TEMPLATE_NAME, TemplateFragmentType.STANDARD);
		
		final URI uri = UriComponentsBuilder.fromUriString("/workspace/" + WORKSPACE_ID + "/templates").build()
				.encode().toUri();

		final MockMultipartFile decriptor = new MockMultipartFile("descriptor", "descriptor", "application/json",
				objectMapper.writeValueAsString(descriptor).getBytes());
		final MockMultipartFile data = new MockMultipartFile("data", "data", "application/json",
				"{\"file\":\"data\"}".getBytes());

		mockMvc.perform(MockMvcRequestBuilders.multipart(uri)
				.file(decriptor)
				.file(data)
				.contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		// UPDATE
		final URI uriUpdate = UriComponentsBuilder.fromUriString("/templates/" + TEMPLATE_ID)
				.build().encode().toUri();

		final MockMultipartFile newData = new MockMultipartFile("data", "data", "application/json",
				"{\"file\":\"NewData\"}".getBytes());
		
		final MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart(uriUpdate);
		builder.with(new RequestPostProcessor() {
			@Override
			public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
				request.setMethod("PUT");
				return request;
			}
		});
		
		mockMvc.perform(builder
				.file(decriptor)
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
		assertTrue(templateFileList.size() == 1);
		assertTrue(templateFileList.get(0).getVersion() == 1L);
	}
	
	@Test
	public void templateAddUpdateTest() throws Exception {
		// CREATE
		final TemplateAddDescriptor descriptor=  new TemplateAddDescriptor(TEMPLATE_NAME, TemplateFragmentType.STANDARD);
		
		final URI uri = UriComponentsBuilder.fromUriString("/workspace/" + WORKSPACE_ID + "/templates").build()
				.encode().toUri();

		final MockMultipartFile decriptor = new MockMultipartFile("descriptor", "descriptor", "application/json",
				objectMapper.writeValueAsString(descriptor).getBytes());
		final MockMultipartFile data = new MockMultipartFile("data", "data", "application/json",
				"{\"file\":\"data\"}".getBytes());

		mockMvc.perform(MockMvcRequestBuilders.multipart(uri)
				.file(decriptor)
				.file(data)
				.contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		// UPDATE
		final URI uriUpdate = UriComponentsBuilder.fromUriString("/templates/" + TEMPLATE_ID)
				.build().encode().toUri();

		final MockMultipartFile newDataForFirstUpdate = new MockMultipartFile("data", "data", "application/json",
				"{\"file\":\"NewDataFirst\"}".getBytes());
		
		 MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart(uriUpdate);
		builder.with(new RequestPostProcessor() {
			@Override
			public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
				request.setMethod("PUT");
				return request;
			}
		});
		
	
		mockMvc.perform(builder
				.file(decriptor)
				.file(newDataForFirstUpdate)
				.contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
		templateFileRepository.findAll().forEach(t->System.out.println(new String(t.getData())));
		final MockMultipartFile newDataForSecondUpdate = new MockMultipartFile("data", "data", "application/json",
				"{\"file\":\"NewDataSecond\"}".getBytes());
		builder = MockMvcRequestBuilders.multipart(uriUpdate);
		builder.with(new RequestPostProcessor() {
			@Override
			public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
				request.setMethod("PUT");
				return request;
			}
		});
		mockMvc.perform(builder
				.file(decriptor)
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
		assertTrue(templateFileList.size() == 2);
		assertTrue(templateFileList.get(0).getVersion() == 1L);
		assertTrue(templateFileList.get(1).getVersion() == 2L);
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
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("message").value("Lowest stage has not met development stage requirements"))
				.andReturn();
		assertNotNull(result.getResponse());

	}
}

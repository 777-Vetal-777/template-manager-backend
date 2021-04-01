package com.itextpdf.dito.manager.integration.editor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.dito.manager.dto.resource.ResourceDTO;
import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.integration.editor.mapper.resource.ResourceLeafDescriptorMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.web.util.UriComponentsBuilder;

import com.itextpdf.dito.editor.server.common.core.descriptor.resource.ResourceLeafDescriptor;
import com.itextpdf.dito.editor.server.common.core.descriptor.resource.AbstractResourceFileDescriptor.ImageDescriptor;
import com.itextpdf.dito.manager.controller.resource.ResourceController;
import com.itextpdf.dito.manager.integration.AbstractIntegrationTest;
import com.itextpdf.dito.manager.integration.editor.controller.resource.ResourceManagementController;
import com.itextpdf.dito.manager.repository.resource.ResourceRepository;

class ResourceManagementFlowIntegrationTest extends AbstractIntegrationTest {

	private static final String IMAGE_NAME = "test-image";
	private static final String IMAGE_TYPE = "IMAGE";
	private static final String IMAGE_FILE_NAME = "any-name.png";
	private static final MockMultipartFile IMAGE_FILE_PART = new MockMultipartFile("resource", IMAGE_FILE_NAME,	"text/plain",  readFileBytes("src/test/resources/test-data/resources/random.png"));
	private static final MockMultipartFile IMAGE_TYPE_PART = new MockMultipartFile("type", "type", "text/plain",IMAGE_TYPE.getBytes());
	private static final MockMultipartFile NAME_PART = new MockMultipartFile("name", "name", "text/plain",IMAGE_NAME.getBytes());
	private static final String BASE64_RESOURCE_ID = "eyJuYW1lIjoidGVzdC1pbWFnZSIsInR5cGUiOiJJTUFHRSIsInN1Yk5hbWUiOm51bGx9";
	private static final String WORKSPACE_ID = "c29tZS10ZW1wbGF0ZQ==";

	@Autowired
	private ResourceRepository resourceRepository;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private ResourceLeafDescriptorMapper resourceLeafDescriptorMapper;

	@AfterEach
	public void clearDb() {
		resourceRepository.deleteAll();
	}

	@Test
	void resourceAddUpdateTest() throws Exception {
		// CREATE
		final ResourceLeafDescriptor descriptor=  new ImageDescriptor(BASE64_RESOURCE_ID);
		descriptor.setDisplayName(IMAGE_NAME);
		final URI uri = UriComponentsBuilder.fromUriString(ResourceManagementController.CREATE_RESOURCE_URL).build()
				.encode().toUri();

		final MockMultipartFile descriptorFile = new MockMultipartFile("descriptor", "descriptor", "application/json",
				objectMapper.writeValueAsString(descriptor).getBytes());
		final MockMultipartFile data = new MockMultipartFile("data", "data", "application/json",
				readFileBytes("src/test/resources/test-data/resources/random.png"));

		mockMvc.perform(MockMvcRequestBuilders.multipart(uri)
				.file(descriptorFile)
				.file(data)
				.contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		// UPDATE
		final URI uriUpdate = UriComponentsBuilder
				.fromUriString(ResourceManagementController.CREATE_RESOURCE_URL + "/" + BASE64_RESOURCE_ID)
				.build().encode().toUri();

		final MockMultipartFile newData = new MockMultipartFile("data", "data", MediaType.MULTIPART_FORM_DATA_VALUE,
				readFileBytes("src/test/resources/test-data/resources/random.png"));
		final MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart(uriUpdate);
		builder.with(new RequestPostProcessor() {
			@Override
			public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
				request.setMethod("PUT");
				return request;
			}
		});
		
		mockMvc.perform(builder
				.file(descriptorFile)
				.file(newData)
				.contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
		// CHECK UPDATE
		final MvcResult result = mockMvc.perform(get(uriUpdate)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_OCTET_STREAM))
				.andExpect(status().isOk()).andReturn();
		assertNotNull(result.getResponse());
	}
	
	@Test
	void test_success_createAndGet() throws Exception {
		final URI uri = UriComponentsBuilder.fromUriString(ResourceController.BASE_NAME).build().encode().toUri();
		// Create resource
		mockMvc.perform(MockMvcRequestBuilders.multipart(uri)
				.file(IMAGE_FILE_PART)
				.file(NAME_PART)
				.file(IMAGE_TYPE_PART)
				.contentType(MediaType.MULTIPART_FORM_DATA));

		final URI integrationUri = UriComponentsBuilder.fromUriString(ResourceManagementController.CREATE_RESOURCE_URL)
				.build().encode().toUri();

		// get integrated resource collection
		mockMvc.perform(get(integrationUri)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(BASE64_RESOURCE_ID))
				.andExpect(jsonPath("$[0].displayName").value("test-image"))
				.andExpect(jsonPath("$[0].type").value("image"));

		// Get Integrated Resource By Id
		final URI integratedResourceUri = UriComponentsBuilder
				.fromUriString(ResourceManagementController.CREATE_RESOURCE_URL + "/" + BASE64_RESOURCE_ID).build()
				.encode().toUri();
		mockMvc.perform(get(integratedResourceUri)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.ALL))
				.andExpect(status().isOk())
				.andExpect(header().string(HttpHeaders.CONTENT_TYPE, "image/png"));

		// Get Integrated Resource By workspace Id
		final URI integratedWorkspaceResourceUri = UriComponentsBuilder
				.fromUriString("/workspace/" + WORKSPACE_ID + "/" + ResourceManagementController.CREATE_RESOURCE_URL).build().encode()
				.toUri();
		mockMvc.perform(get(integratedWorkspaceResourceUri)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(BASE64_RESOURCE_ID))
				.andExpect(jsonPath("$[0].displayName").value("test-image"))
				.andExpect(jsonPath("$[0].type").value("image"));

		// Delete Integrated Resource By Id
		final MvcResult result = mockMvc.perform(delete(integratedResourceUri)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		assertNotNull(result.getResponse());
		
	}

	@Test
	void testCreateFontAndGet() throws Exception {
		final String fileName = "font.ttf";
		final MockMultipartFile fontFilePartRegular = new MockMultipartFile("regular", fileName,	"text/plain",  readFileBytes("src/test/resources/test-data/resources/random-regular.ttf"));
		final MockMultipartFile fontFilePartBold = new MockMultipartFile("bold", fileName,	"text/plain",  readFileBytes("src/test/resources/test-data/resources/random-regular.ttf"));
		final MockMultipartFile fontFilePartItalic = new MockMultipartFile("italic", fileName,	"text/plain",  readFileBytes("src/test/resources/test-data/resources/random-regular.ttf"));
		final MockMultipartFile fontFilePartBoldItalic = new MockMultipartFile("bold_italic", fileName,	"text/plain",  readFileBytes("src/test/resources/test-data/resources/random-regular.ttf"));
		final MockMultipartFile fontTypePart = new MockMultipartFile("type", "type", "text/plain", ResourceTypeEnum.FONT.toString().getBytes());
		final MockMultipartFile namePart = new MockMultipartFile("name", "name", "text/plain", "font".getBytes());

		final MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.multipart(ResourceController.BASE_NAME + ResourceController.FONTS_ENDPOINT)
				.file(fontFilePartRegular)
				.file(fontFilePartBold)
				.file(fontFilePartBoldItalic)
				.file(fontFilePartItalic)
				.file(namePart)
				.file(fontTypePart)
				.contentType(MediaType.MULTIPART_FORM_DATA))
				.andExpect(status().isOk())
				.andReturn();

		final ResourceDTO resourceDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ResourceDTO.class);
		assertEquals(ResourceTypeEnum.FONT, resourceDTO.getType());
		final String resourceId = resourceLeafDescriptorMapper.encodeId(resourceDTO.getName(), resourceDTO.getType(), "BOLD");

		mockMvc.perform(get(ResourceManagementController.RESOURCE_URL, resourceId)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.ALL))
				.andExpect(status().isOk())
				.andExpect(header().string(HttpHeaders.CONTENT_TYPE, "font/ttf"));

		mockMvc.perform(get(ResourceManagementController.WORKSPACE_RESOURCES_URL, WORKSPACE_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.ALL))
				.andExpect(status().isOk());
	}

	@ParameterizedTest
	@CsvSource({
			"style.css, src/test/resources/test-data/resources/text-css.css, STYLESHEET, text/css; charset=utf-8",
			"image.png, src/test/resources/test-data/resources/random.png, IMAGE, image/png",
			"svg.svg, src/test/resources/test-data/resources/random.svg, IMAGE, image/svg+xml",
			"svg.png, src/test/resources/test-data/resources/random.svg, IMAGE, application/octet-stream"
	})
	void testCreateResourceAndGetExpectedContentType(final String fileName, final String filePath, final ResourceTypeEnum type, final String expected) throws Exception {
		final MockMultipartFile filePart = new MockMultipartFile("resource", fileName,	"text/plain",  readFileBytes(filePath));
		final MockMultipartFile typePart = new MockMultipartFile("type", "type", "text/plain", type.toString().getBytes());
		final MockMultipartFile namePart = new MockMultipartFile("name", "name", "text/plain", "resource".getBytes());

		final MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.multipart(ResourceController.BASE_NAME)
				.file(filePart)
				.file(namePart)
				.file(typePart)
				.contentType(MediaType.MULTIPART_FORM_DATA))
				.andExpect(status().is2xxSuccessful())
				.andReturn();

		final ResourceDTO resourceDTO = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ResourceDTO.class);
		assertNotNull(resourceDTO.getType());
		final String resourceId = resourceLeafDescriptorMapper.encodeId(resourceDTO.getName(), resourceDTO.getType(), null);

		mockMvc.perform(get(ResourceManagementController.RESOURCE_URL, resourceId)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.ALL))
				.andExpect(status().isOk())
				.andExpect(header().string(HttpHeaders.CONTENT_TYPE, expected));
	}
}

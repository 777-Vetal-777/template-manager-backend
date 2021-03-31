package com.itextpdf.dito.manager.integration.editor;

import static com.itextpdf.dito.manager.controller.datacollection.DataCollectionController.DATA_SAMPLE_ENDPOINT;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import com.itextpdf.kernel.xmp.impl.Base64;

import org.junit.jupiter.api.AfterEach;
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

import com.itextpdf.dito.editor.server.common.core.descriptor.DataSampleDescriptor;
import com.itextpdf.dito.manager.controller.datacollection.DataCollectionController;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionType;
import com.itextpdf.dito.manager.dto.datasample.create.DataSampleCreateRequestDTO;
import com.itextpdf.dito.manager.integration.AbstractIntegrationTest;
import com.itextpdf.dito.manager.integration.editor.controller.data.DataManagementController;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionRepository;
import com.itextpdf.dito.manager.repository.datasample.DataSampleRepository;
import com.itextpdf.dito.manager.service.datacollection.DataCollectionService;

public class DataManagementFlowIntegrationTest extends AbstractIntegrationTest {
	private static final String NOT_EXISTED_SAMPLE = "NotExistedSample";
	private static final String EXISTED_SAMPLE = "name";

	private static final String DATACOLLECTION_NAME = "data-collection-test";
	private static final String DATACOLLECTION_BASE64_ENCODED_NAME = Base64.encode(DATACOLLECTION_NAME);
	private static final String TYPE = "JSON";

	@Autowired
	private DataCollectionService dataCollectionService;
    @Autowired
    private DataCollectionRepository dataCollectionRepository;
    @Autowired
    private DataSampleRepository dataSampleRepository;

	private DataSampleCreateRequestDTO request;

	@AfterEach
	public void clearDb() {
		dataSampleRepository.deleteAll();
		dataCollectionRepository.deleteAll();
	}

	@BeforeEach
	public void init() throws IOException {
		request = objectMapper.readValue(
				new File("src/test/resources/test-data/datasamples/data-sample-create-request.json"),
				DataSampleCreateRequestDTO.class);

		dataCollectionService.create(DATACOLLECTION_NAME, DataCollectionType.valueOf(TYPE),
				"{\"file\":\"data\"}".getBytes(), "datacollection.json", "admin@email.com");
	}

	@Test
	public void dataSampleAddUpdateTest() throws Exception {
		// CREATE
		DataSampleDescriptor dataSampleDescriptor = new DataSampleDescriptor(Base64.encode(EXISTED_SAMPLE));
		dataSampleDescriptor.setDisplayName(EXISTED_SAMPLE);
		List<String> idList = new ArrayList<>();
		idList.add(Base64.encode(DATACOLLECTION_NAME));
		dataSampleDescriptor.setCollectionIdList(idList);
		final URI uri = UriComponentsBuilder.fromUriString(DataManagementController.CREATE_DATA_SAMPLE_URL).build()
				.encode().toUri();

		final MockMultipartFile descriptor = new MockMultipartFile("descriptor", "descriptor", "application/json",
				objectMapper.writeValueAsString(dataSampleDescriptor).getBytes());
		final MockMultipartFile data = new MockMultipartFile("data", "data", "application/json",
				"{\"file\":\"data\"}".getBytes());

		mockMvc.perform(MockMvcRequestBuilders.multipart(uri)
				.file(descriptor)
				.file(data)
				.contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		// UPDATE
		final URI uriUpdate = UriComponentsBuilder
				.fromUriString(DataManagementController.CREATE_DATA_SAMPLE_URL + "/" + Base64.encode(EXISTED_SAMPLE))
				.build().encode().toUri();

		final MockMultipartFile newData = new MockMultipartFile("data", "data", "application/json",
				"{\"file\":\"NewData\"}".getBytes());
		MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart(uriUpdate);
		builder.with(new RequestPostProcessor() {
			@Override
			public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
				request.setMethod("PUT");
				return request;
			}
		});

		mockMvc.perform(builder
				.file(descriptor)
				.file(newData)
				.contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
		// CHECK UPDATE
		final MvcResult result = mockMvc.perform(get(uriUpdate)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_OCTET_STREAM))
				.andExpect(status().isOk())
				.andExpect(jsonPath("file").value("NewData"))
				.andReturn();
		assertNotNull(result.getResponse());
	}

	@Test
	public void dataSampleNotFoundTest() throws Exception {
		final URI uri = UriComponentsBuilder
				.fromUriString(DataManagementController.CREATE_DATA_SAMPLE_URL + "/" + NOT_EXISTED_SAMPLE).build()
				.encode().toUri();
		final MvcResult result = mockMvc.perform(get(uri)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andReturn();
		assertNotNull(result.getResponse());
	}

	@Test
	public void dataSampleGetById() throws Exception {
		// Create Data Sample
		mockMvc.perform(post(DataCollectionController.BASE_NAME + "/" + DATACOLLECTION_BASE64_ENCODED_NAME + DATA_SAMPLE_ENDPOINT)
						.content(objectMapper.writeValueAsString(request))
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isCreated());
		// Get Data Sample By Id
		final URI uri = UriComponentsBuilder
				.fromUriString(DataManagementController.CREATE_DATA_SAMPLE_URL + "/" + Base64.encode(EXISTED_SAMPLE))
				.build().encode().toUri();
		mockMvc.perform(get(uri)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_OCTET_STREAM))
				.andExpect(status().isOk())
				.andExpect(jsonPath("file").value("data"));

		// Get Data Sample Descriptor
		final URI uriDescriptor = UriComponentsBuilder.fromUriString(
				DataManagementController.CREATE_DATA_SAMPLE_URL + "/" + Base64.encode(EXISTED_SAMPLE) + "/descriptor")
				.build().encode().toUri();
		mockMvc.perform(get(uriDescriptor)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("id").value(Base64.encode(EXISTED_SAMPLE)))
				.andExpect(jsonPath("displayName").value("name"))
				.andExpect(jsonPath("collectionIdList[0]").value("ZGF0YS1jb2xsZWN0aW9uLXRlc3Q="));

		// Get Data Sample By Data Collection
		final URI uriForCollection = UriComponentsBuilder
				.fromUriString("/collection/" + DATACOLLECTION_BASE64_ENCODED_NAME).build().encode().toUri();
		mockMvc.perform(get(uriForCollection)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(Base64.encode(EXISTED_SAMPLE)))
				.andExpect(jsonPath("$[0].displayName").value("name.json"))
				.andExpect(jsonPath("$[0].collectionIdList[0]").value("ZGF0YS1jb2xsZWN0aW9uLXRlc3Q="));

		// Delete Data Sample
		final MvcResult result = mockMvc.perform(delete(uri)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("id").value(Base64.encode(EXISTED_SAMPLE)))
				.andReturn();
		assertNotNull(result.getResponse());
	}

	@Test
	void shouldReturnDataCollectionDescriptor() throws Exception {
		final MvcResult result = mockMvc.perform(get(DataManagementController.COLLECTION_DESCRIPTOR_URL, DATACOLLECTION_BASE64_ENCODED_NAME)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("id").value(DATACOLLECTION_BASE64_ENCODED_NAME))
				.andExpect(jsonPath("displayName").value(DATACOLLECTION_NAME))
				.andExpect(jsonPath("defaultSampleId").isEmpty())
				.andReturn();

		assertNotNull(result.getResponse());
	}

	@Test
	void shouldReturnDataCollectionDescriptorWithSampleId() throws Exception {
		final String dataSampleId = Base64.encode(EXISTED_SAMPLE);
		DataSampleDescriptor dataSampleDescriptor = new DataSampleDescriptor(dataSampleId);
		dataSampleDescriptor.setDisplayName(EXISTED_SAMPLE);
		List<String> idList = new ArrayList<>();
		idList.add(Base64.encode(DATACOLLECTION_NAME));
		dataSampleDescriptor.setCollectionIdList(idList);

		final MockMultipartFile descriptor = new MockMultipartFile("descriptor", "descriptor", "application/json", objectMapper.writeValueAsString(dataSampleDescriptor).getBytes());
		final MockMultipartFile data = new MockMultipartFile("data", "data", "application/json", "{\"file\":\"data\"}".getBytes());

		mockMvc.perform(MockMvcRequestBuilders.multipart(DataManagementController.CREATE_DATA_SAMPLE_URL)
				.file(descriptor)
				.file(data)
				.contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		final MvcResult result = mockMvc.perform(get(DataManagementController.COLLECTION_DESCRIPTOR_URL, DATACOLLECTION_BASE64_ENCODED_NAME)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("id").value(DATACOLLECTION_BASE64_ENCODED_NAME))
				.andExpect(jsonPath("displayName").value(DATACOLLECTION_NAME))
				.andExpect(jsonPath("defaultSampleId").value(dataSampleId))
				.andReturn();

		assertNotNull(result.getResponse());
	}

	@Test
	void shouldReturnNotFoundDataCollection() throws Exception {
		final MvcResult result = mockMvc.perform(get(DataManagementController.COLLECTION_DESCRIPTOR_URL, NOT_EXISTED_SAMPLE)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andReturn();

		assertNotNull(result.getResponse());
	}

	@Test
	void shouldReturnBadRequestForDataCollection() throws Exception {
		final MvcResult result = mockMvc.perform(get(DataManagementController.COLLECTION_DESCRIPTOR_URL, "b")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn();

		assertNotNull(result.getResponse());
	}

}

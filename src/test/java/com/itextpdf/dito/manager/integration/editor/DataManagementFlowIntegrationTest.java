package com.itextpdf.dito.manager.integration.editor;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;

import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.datasample.DataSampleEntity;
import com.itextpdf.dito.manager.service.datasample.DataSampleService;
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
	private DataSampleService dataSampleService;
    @Autowired
    private DataCollectionRepository dataCollectionRepository;
    @Autowired
    private DataSampleRepository dataSampleRepository;

	@AfterEach
	public void clearDb() {
		dataSampleRepository.deleteAll();
		dataCollectionRepository.deleteAll();
	}

	@BeforeEach
	public void init() throws IOException {
		dataCollectionService.create(DATACOLLECTION_NAME, DataCollectionType.valueOf(TYPE),
				"{\"file\":\"data\"}".getBytes(), "datacollection.json", "admin@email.com");
	}

	@Test
	public void dataSampleAddUpdateTest() throws Exception {
		final DataCollectionEntity entity = dataCollectionService.get(DATACOLLECTION_NAME);
		assertNotNull(entity);

		// CREATE
		DataSampleDescriptor dataSampleDescriptor = new DataSampleDescriptor(Base64.encode(EXISTED_SAMPLE));
		dataSampleDescriptor.setDisplayName(EXISTED_SAMPLE);
		dataSampleDescriptor.setCollectionIdList(Collections.singletonList(entity.getUuid()));
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
		final DataSampleEntity dataSampleEntity = dataSampleService.get(entity.getName(), EXISTED_SAMPLE);

		final MockMultipartFile newData = new MockMultipartFile("data", "data", "application/json",
				"{\"file\":\"NewData\"}".getBytes());
		MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart(DataManagementController.DATA_SAMPLE_URL, dataSampleEntity.getUuid());
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
		mockMvc.perform(get(DataManagementController.DATA_SAMPLE_URL, dataSampleEntity.getUuid())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_OCTET_STREAM))
				.andExpect(status().isOk())
				.andExpect(jsonPath("file").value("NewData"));
	}

	@Test
	public void dataSampleNotFoundTest() throws Exception {
		final URI uri = UriComponentsBuilder
				.fromUriString(DataManagementController.CREATE_DATA_SAMPLE_URL + "/" + NOT_EXISTED_SAMPLE).build()
				.encode().toUri();
		final MvcResult result = mockMvc.perform(get(uri)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andReturn();
		assertNotNull(result.getResponse());
	}

	@Test
	public void dataSampleGetById() throws Exception {
		final DataSampleCreateRequestDTO request = objectMapper.readValue(
				new File("src/test/resources/test-data/datasamples/data-sample-create-request.json"),
				DataSampleCreateRequestDTO.class);
		assertNotNull(request);
		// Create Data Sample
		mockMvc.perform(post(DataCollectionController.BASE_NAME + DataCollectionController. DATA_COLLECTION_DATA_SAMPLES_WITH_PATH_VARIABLE, DATACOLLECTION_BASE64_ENCODED_NAME)
						.content(objectMapper.writeValueAsString(request))
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
						.andExpect(status().isCreated());
		final DataCollectionEntity entity = dataCollectionService.get(DATACOLLECTION_NAME);
		assertNotNull(entity);
		final DataSampleEntity dataSampleEntity = dataSampleService.get(entity.getName(), EXISTED_SAMPLE);
		// Get Data Sample By Id
		mockMvc.perform(get(DataManagementController.DATA_SAMPLE_URL, dataSampleEntity.getUuid())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_OCTET_STREAM))
				.andExpect(status().isOk())
				.andExpect(jsonPath("file").value("data"));

		// Get Data Sample Descriptor
		mockMvc.perform(get(DataManagementController.DATA_SAMPLE_DESCRIPTOR_URL, dataSampleEntity.getUuid())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("id").value(dataSampleEntity.getUuid()))
				.andExpect(jsonPath("displayName").value("name"))
				.andExpect(jsonPath("collectionIdList[0]").value(entity.getUuid()));

		// Get Data Sample By Data Collection
		mockMvc.perform(get(DataManagementController.COLLECTION_DATA_SAMPLES_URL, entity.getUuid())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(dataSampleEntity.getUuid()))
				.andExpect(jsonPath("$[0].displayName").value("name.json"))
				.andExpect(jsonPath("$[0].collectionIdList[0]").value(entity.getUuid()));

		// Delete Data Sample
		mockMvc.perform(delete(DataManagementController.DATA_SAMPLE_URL, dataSampleEntity.getUuid())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("id").value(dataSampleEntity.getUuid()));
	}

	@Test
	void shouldReturnDataCollectionDescriptor() throws Exception {
		final DataCollectionEntity entity = dataCollectionService.get(DATACOLLECTION_NAME);
		assertNotNull(entity);

		mockMvc.perform(get(DataManagementController.COLLECTION_DESCRIPTOR_URL, entity.getUuid())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("id").value(entity.getUuid()))
				.andExpect(jsonPath("displayName").value(DATACOLLECTION_NAME));
	}

	@Test
	void shouldReturnDataCollectionDescriptorWithSampleId() throws Exception {
		final DataCollectionEntity entity = dataCollectionService.get(DATACOLLECTION_NAME);
		assertNotNull(entity);

		DataSampleDescriptor dataSampleDescriptor = new DataSampleDescriptor(null);
		dataSampleDescriptor.setDisplayName(EXISTED_SAMPLE);
		dataSampleDescriptor.setCollectionIdList(Collections.singletonList(entity.getUuid()));

		final MockMultipartFile descriptor = new MockMultipartFile("descriptor", "descriptor", "application/json", objectMapper.writeValueAsString(dataSampleDescriptor).getBytes());
		final MockMultipartFile data = new MockMultipartFile("data", "data", "application/json", "{\"file\":\"data\"}".getBytes());

		mockMvc.perform(MockMvcRequestBuilders.multipart(DataManagementController.CREATE_DATA_SAMPLE_URL)
				.file(descriptor)
				.file(data)
				.contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		final DataSampleEntity dataSampleEntity = dataSampleService.get(entity.getName(), EXISTED_SAMPLE);

		mockMvc.perform(get(DataManagementController.COLLECTION_DESCRIPTOR_URL, entity.getUuid())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("id").value(entity.getUuid()))
				.andExpect(jsonPath("displayName").value(DATACOLLECTION_NAME));
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
	void shouldReturnNotFoundForDataCollection() throws Exception {
		final MvcResult result = mockMvc.perform(get(DataManagementController.COLLECTION_DESCRIPTOR_URL, "b")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andReturn();

		assertNotNull(result.getResponse());
	}

	@Test
	void shouldFindDataCollectionById() throws Exception {
		final DataCollectionEntity entity = dataCollectionService.get(DATACOLLECTION_NAME);
		assertNotNull(entity);

		mockMvc.perform(get(DataManagementController.COLLECTION_DATA_STRUCTURE_URL, entity.getUuid())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

}

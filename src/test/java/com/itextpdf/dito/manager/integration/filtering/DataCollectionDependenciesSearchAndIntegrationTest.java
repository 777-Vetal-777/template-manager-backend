package com.itextpdf.dito.manager.integration.filtering;

import com.itextpdf.dito.manager.dto.datacollection.DataCollectionType;
import com.itextpdf.dito.manager.entity.TemplateTypeEnum;
import com.itextpdf.dito.manager.integration.AbstractIntegrationTest;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionFileRepository;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionRepository;
import com.itextpdf.dito.manager.repository.template.TemplateRepository;
import com.itextpdf.dito.manager.service.datacollection.DataCollectionService;
import com.itextpdf.dito.manager.service.template.TemplateService;
import com.itextpdf.kernel.xmp.impl.Base64;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import static com.itextpdf.dito.manager.controller.datacollection.DataCollectionController.BASE_NAME;
import static com.itextpdf.dito.manager.controller.datacollection.DataCollectionController.DATA_COLLECTION_DEPENDENCIES_WITH_PATH_VARIABLE_PAGEABLE;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DataCollectionDependenciesSearchAndIntegrationTest extends AbstractIntegrationTest implements FilterAndSearchTest {

    public static final String DATA_COLLECTION_WITH_ONE_DEPENDENCY_NAME = "test_filter_data_collection";
    public static final String DATA_COLLECTION_WITH_ONE_DEPENDENCY_NAME_ENCODED = Base64.encode(DATA_COLLECTION_WITH_ONE_DEPENDENCY_NAME);
    public static final String DATA_COLLECTION_WITH_TWO_DEPENDENCIES_NAME = "test_filter_data_collection2";
    public static final String DATA_COLLECTION_WITH_TWO_DEPENDENCIES_NAME_ENCODED = Base64.encode(DATA_COLLECTION_WITH_TWO_DEPENDENCIES_NAME);
    public static final String DATA_COLLECTION_WITHOUT_DEPENDENCIES_NAME = "test_filter_data_collection3";
    public static final String DATA_COLLECTION_WITHOUT_DEPENDENCIES_NAME_ENCODED = Base64.encode(DATA_COLLECTION_WITHOUT_DEPENDENCIES_NAME);
    public static final String TEMPLATE1_NAME = "test_filter_template_name";
    public static final String TEMPLATE2_NAME = "test_filter_template2_name";
    public static final String TEMPLATE3_NAME = "test_filter_template3_name";
    public static final String USER_EMAIL = "admin@email.com";
    public static final String JSON_DATA = "{\"file\":\"data\"}";
    public static final String FILENAME_JSON = "filename.json";

    @Autowired
    private DataCollectionService dataCollectionService;

    @Autowired
    private TemplateService templateService;

    @Autowired
    private TemplateRepository templateRepository;

    @Autowired
    private DataCollectionRepository dataCollectionRepository;

    @BeforeEach
    void init() throws Exception {
        dataCollectionService.create(DATA_COLLECTION_WITH_ONE_DEPENDENCY_NAME, DataCollectionType.JSON, JSON_DATA.getBytes(), FILENAME_JSON, USER_EMAIL);
        dataCollectionService.create(DATA_COLLECTION_WITHOUT_DEPENDENCIES_NAME, DataCollectionType.JSON, JSON_DATA.getBytes(), FILENAME_JSON, USER_EMAIL);
        dataCollectionService.create(DATA_COLLECTION_WITH_TWO_DEPENDENCIES_NAME, DataCollectionType.JSON, JSON_DATA.getBytes(), FILENAME_JSON, USER_EMAIL);

        templateService.create(TEMPLATE1_NAME, TemplateTypeEnum.FOOTER, DATA_COLLECTION_WITH_ONE_DEPENDENCY_NAME, USER_EMAIL);
        templateService.create(TEMPLATE2_NAME, TemplateTypeEnum.HEADER, DATA_COLLECTION_WITH_TWO_DEPENDENCIES_NAME, USER_EMAIL);
        templateService.create(TEMPLATE3_NAME, TemplateTypeEnum.STANDARD, DATA_COLLECTION_WITH_TWO_DEPENDENCIES_NAME, USER_EMAIL);

        dataCollectionService.createNewVersion(DATA_COLLECTION_WITH_ONE_DEPENDENCY_NAME, DataCollectionType.JSON, JSON_DATA.getBytes(), "any-name.json", USER_EMAIL, "");
    }

    @AfterEach
    void destroy() {
        templateRepository.delete(templateRepository.findByName(TEMPLATE1_NAME).get());
        templateRepository.delete(templateRepository.findByName(TEMPLATE2_NAME).get());
        templateRepository.delete(templateRepository.findByName(TEMPLATE3_NAME).get());

        dataCollectionRepository.delete(dataCollectionRepository.findByName(DATA_COLLECTION_WITH_ONE_DEPENDENCY_NAME).get());
        dataCollectionRepository.delete(dataCollectionRepository.findByName(DATA_COLLECTION_WITH_TWO_DEPENDENCIES_NAME).get());
        dataCollectionRepository.delete(dataCollectionRepository.findByName(DATA_COLLECTION_WITHOUT_DEPENDENCIES_NAME).get());
    }

    @Override
    @Test
    public void test_filtering() throws Exception {
        mockMvc.perform(get(BASE_NAME + DATA_COLLECTION_DEPENDENCIES_WITH_PATH_VARIABLE_PAGEABLE, DATA_COLLECTION_WITH_ONE_DEPENDENCY_NAME_ENCODED)
                .param("version", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));

        mockMvc.perform(get(BASE_NAME + DATA_COLLECTION_DEPENDENCIES_WITH_PATH_VARIABLE_PAGEABLE, DATA_COLLECTION_WITH_ONE_DEPENDENCY_NAME_ENCODED)
                .param("version", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));

        mockMvc.perform(get(BASE_NAME + DATA_COLLECTION_DEPENDENCIES_WITH_PATH_VARIABLE_PAGEABLE, DATA_COLLECTION_WITHOUT_DEPENDENCIES_NAME_ENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));

        mockMvc.perform(get(BASE_NAME + DATA_COLLECTION_DEPENDENCIES_WITH_PATH_VARIABLE_PAGEABLE, DATA_COLLECTION_WITHOUT_DEPENDENCIES_NAME_ENCODED)
                .param("version", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));

        final MvcResult result = mockMvc.perform(get(BASE_NAME + DATA_COLLECTION_DEPENDENCIES_WITH_PATH_VARIABLE_PAGEABLE, DATA_COLLECTION_WITH_TWO_DEPENDENCIES_NAME_ENCODED)
                .param("version", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2))).andReturn();
        assertNotNull(result.getResponse());
    }

    @Override
    @Test
    public void test_searchAndFiltering() throws Exception {
        mockMvc.perform(get(BASE_NAME + DATA_COLLECTION_DEPENDENCIES_WITH_PATH_VARIABLE_PAGEABLE, DATA_COLLECTION_WITH_TWO_DEPENDENCIES_NAME_ENCODED)
                .param("search", "template"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)));

        mockMvc.perform(get(BASE_NAME + DATA_COLLECTION_DEPENDENCIES_WITH_PATH_VARIABLE_PAGEABLE, DATA_COLLECTION_WITH_TWO_DEPENDENCIES_NAME_ENCODED)
                .param("version", "1")
                .param("search", "2_name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));

        final MvcResult result = mockMvc.perform(get(BASE_NAME + DATA_COLLECTION_DEPENDENCIES_WITH_PATH_VARIABLE_PAGEABLE, DATA_COLLECTION_WITH_TWO_DEPENDENCIES_NAME_ENCODED)
                .param("version", "1")
                .param("search", "Harry"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0))).andReturn();
        assertNotNull(result.getResponse());
    }

    @Override
    @Test
    public void test_sortWithSearch() throws Exception {
        for (String field : DataCollectionFileRepository.SUPPORTED_DEPENDENCY_SORT_FIELDS) {
            final MvcResult result = mockMvc.perform(get(BASE_NAME + DATA_COLLECTION_DEPENDENCIES_WITH_PATH_VARIABLE_PAGEABLE, DATA_COLLECTION_WITH_TWO_DEPENDENCIES_NAME_ENCODED)
                    .param("search", "template")
                    .param("sort", field))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(2))).andReturn();
            assertNotNull(result.getResponse());
        }
    }

    @Override
    @Test
    public void test_sortWithFiltering() throws Exception {
        for (String field : DataCollectionFileRepository.SUPPORTED_DEPENDENCY_SORT_FIELDS) {
            mockMvc.perform(get(BASE_NAME + DATA_COLLECTION_DEPENDENCIES_WITH_PATH_VARIABLE_PAGEABLE, DATA_COLLECTION_WITH_TWO_DEPENDENCIES_NAME_ENCODED)
                    .param("version", "1")
                    .param("sort", field))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(2)));
        }
        for (String field : DataCollectionFileRepository.SUPPORTED_DEPENDENCY_SORT_FIELDS) {
            final MvcResult result = mockMvc.perform(get(BASE_NAME + DATA_COLLECTION_DEPENDENCIES_WITH_PATH_VARIABLE_PAGEABLE, DATA_COLLECTION_WITH_TWO_DEPENDENCIES_NAME_ENCODED)
                    .param("version", "1")
                    .param("sort", field)
                    .param("sort", "desc"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(2))).andReturn();
            assertNotNull(result.getResponse());
        }
    }
}

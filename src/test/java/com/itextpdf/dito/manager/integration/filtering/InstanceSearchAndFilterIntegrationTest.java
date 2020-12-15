package com.itextpdf.dito.manager.integration.filtering;

import com.itextpdf.dito.manager.controller.instance.InstanceController;
import com.itextpdf.dito.manager.entity.InstanceEntity;
import com.itextpdf.dito.manager.integration.AbstractIntegrationTest;
import com.itextpdf.dito.manager.repository.instance.InstanceRepository;
import com.itextpdf.dito.manager.repository.role.RoleRepository;
import com.itextpdf.dito.manager.repository.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class InstanceSearchAndFilterIntegrationTest extends AbstractIntegrationTest implements FilterAndSearchTest {

    @Autowired
    private InstanceRepository instanceRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    private InstanceEntity instanceEntity;
    
    @BeforeEach
    public void init(){
        instanceEntity = new InstanceEntity();
        instanceEntity.setName("test-instance");
        instanceEntity.setSocket("socket-name");
        instanceEntity.setCreatedBy(userRepository.findByEmail("admin@email.com").orElseThrow());
        instanceRepository.save(instanceEntity);
    }
    
    @AfterEach
    public void tearDown(){
        instanceRepository.delete(instanceEntity);
    }

    @Override
    @Test
    public void test_filtering() throws Exception {
        mockMvc.perform(get(InstanceController.BASE_NAME)
                .param("name", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is(instanceEntity.getName())));

        mockMvc.perform(get(InstanceController.BASE_NAME)
                .param("socket", "Socket"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is(instanceEntity.getName())));

        mockMvc.perform(get(InstanceController.BASE_NAME)
                .param("createdOn", "01/01/1970")
                .param("createdOn", "01/01/1980"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    @Override
    @Test
    public void test_searchAndFiltering() throws Exception {
        mockMvc.perform(get(InstanceController.BASE_NAME)
                .param("name", "instance")
                .param("search", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is(instanceEntity.getName())));

        mockMvc.perform(get(InstanceController.BASE_NAME)
                .param("name", "instance")
                .param("search", "not-existing-user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    @Override
    @Test
    public void test_sortWithSearch() throws Exception {
        for (String field : InstanceRepository.SUPPORTED_SORT_FIELDS) {
            mockMvc.perform(get(InstanceController.BASE_NAME)
                    .param("sort", field)
                    .param("search", "not-existing-user"))
                    .andExpect(status().isOk());
        }
    }

    @Override
    @Test
    public void test_sortWithFiltering() throws Exception {
        for (String field : InstanceRepository.SUPPORTED_SORT_FIELDS) {
            mockMvc.perform(get(InstanceController.BASE_NAME)
                    .param("sort", field)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }
    }
}

package com.itextpdf.dito.manager.integration.crud;

import com.itextpdf.dito.manager.controller.instance.InstanceController;
import com.itextpdf.dito.manager.dto.instance.create.InstancesRememberRequestDTO;
import com.itextpdf.dito.manager.dto.instance.update.InstanceUpdateRequestDTO;
import com.itextpdf.dito.manager.entity.InstanceEntity;
import com.itextpdf.dito.manager.integration.AbstractIntegrationTest;
import com.itextpdf.dito.manager.repository.instance.InstanceRepository;
import com.itextpdf.dito.manager.repository.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.io.File;
import java.util.Base64;
import java.util.Date;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class InstanceFlowIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private InstanceRepository instanceRepository;
    @Autowired
    private UserRepository userRepository;

    @AfterEach
    public void tearDown() {
        instanceRepository.deleteAll();
    }

    @Test
    public void test_ping() throws Exception {
        final String encodedSocketName = new String(Base64.getEncoder().encode("localhost:9999".getBytes()));
        mockMvc.perform(get(InstanceController.BASE_NAME + InstanceController.INSTANCE_STATUS_ENDPOINT, encodedSocketName))
                .andExpect(status().isBadGateway());
        assertTrue(instanceRepository.findByName(encodedSocketName).isEmpty());
    }

    @Test
    public void test_remember_success() throws Exception {
        final InstancesRememberRequestDTO request = objectMapper.readValue(new File("src/test/resources/test-data/instances/instances-remember-request.json"), InstancesRememberRequestDTO.class);

        mockMvc.perform(post(InstanceController.BASE_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].socket", is(request.getInstances().get(0).getSocket())));

        mockMvc.perform(post(InstanceController.BASE_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
        assertTrue(instanceRepository.findByName(request.getInstances().get(0).getSocket()).isEmpty());
    }

    @Test
    public void test_forget() throws Exception {
        InstanceEntity instanceEntity = createInstanceForTest("instance-to-forget");
        final String encodedSocketName = new String(Base64.getEncoder().encode(instanceEntity.getName().getBytes()));

        mockMvc.perform(delete(InstanceController.BASE_NAME + InstanceController.INSTANCE_NAME_ENDPOINT_WITH_PATH_VARIABLE, encodedSocketName))
                .andExpect(status().isOk());
    }

    @Test
    public void test_list() throws Exception {
        mockMvc.perform(get(InstanceController.BASE_NAME))
                .andExpect(status().isOk());
        assertTrue(!instanceRepository.findAll().isEmpty());
    }

    @Test
    public void update() throws Exception{
        InstanceEntity instanceEntity = createInstanceForTest("instance-to-update");
        final String encodedSocketName = new String(Base64.getEncoder().encode(instanceEntity.getName().getBytes()));
        final InstanceUpdateRequestDTO request = objectMapper.readValue(new File("src/test/resources/test-data/instances/instance-update-request.json"), InstanceUpdateRequestDTO.class);

        mockMvc.perform(patch(InstanceController.BASE_NAME + InstanceController.INSTANCE_NAME_ENDPOINT_WITH_PATH_VARIABLE, encodedSocketName).content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(request.getName())))
                .andExpect(jsonPath("$.socket", is(request.getSocket())));
    }

    private InstanceEntity createInstanceForTest(String name){
        InstanceEntity instanceEntity = new InstanceEntity();
        instanceEntity.setName(name);
        instanceEntity.setSocket("localhost:9999");
        instanceEntity.setCreatedBy(userRepository.findByEmail("admin@email.com").orElseThrow());
        instanceEntity.setCreatedOn(new Date());
        return instanceRepository.save(instanceEntity);
    }
}

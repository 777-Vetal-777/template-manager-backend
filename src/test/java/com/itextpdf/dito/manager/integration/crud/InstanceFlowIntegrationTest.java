package com.itextpdf.dito.manager.integration.crud;

import com.itextpdf.dito.manager.component.client.instance.InstanceHealthChecker;
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
import org.springframework.test.web.servlet.MvcResult;

import java.io.File;
import java.util.Base64;
import java.util.Date;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class InstanceFlowIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private InstanceRepository instanceRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private InstanceHealthChecker instanceHealthChecker;

    @AfterEach
    public void tearDown() {
        instanceRepository.deleteAll();
    }

    @Test
    void test_ping() throws Exception {
        final String encodedSocketName = new String(Base64.getEncoder().encode("localhost:9999".getBytes()));
        mockMvc.perform(get(InstanceController.BASE_NAME + InstanceController.INSTANCE_STATUS_ENDPOINT, encodedSocketName))
                .andExpect(status().isGatewayTimeout());
        assertTrue(instanceRepository.findByName(encodedSocketName).isEmpty());
    }

    @Test
    void shouldThrowInstanceAlreadyExist() throws Exception {
        final InstancesRememberRequestDTO request = objectMapper.readValue(new File("src/test/resources/test-data/instances/instances-remember-request.json"), InstancesRememberRequestDTO.class);
        createInstanceForTest(request.getInstances().get(0).getName());

        mockMvc.perform(post(InstanceController.BASE_NAME)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void test_remember_success() throws Exception {
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
    void test_forget() throws Exception {
        InstanceEntity instanceEntity = createInstanceForTest("instance-to-forget");
        final String encodedSocketName = new String(Base64.getEncoder().encode(instanceEntity.getName().getBytes()));

        mockMvc.perform(delete(InstanceController.BASE_NAME + InstanceController.INSTANCE_NAME_ENDPOINT_WITH_PATH_VARIABLE, encodedSocketName))
                .andExpect(status().isOk());
    }

    @Test
    void test_list() throws Exception {
        mockMvc.perform(get(InstanceController.BASE_NAME))
                .andExpect(status().isOk());
        assertFalse(instanceRepository.findAll().isEmpty());
    }

    @Test
    void update() throws Exception{
        InstanceEntity instanceEntity = createInstanceForTest("instance-to-update");
        final String encodedSocketName = new String(Base64.getEncoder().encode(instanceEntity.getName().getBytes()));
        final InstanceUpdateRequestDTO request = objectMapper.readValue(new File("src/test/resources/test-data/instances/instance-update-request.json"), InstanceUpdateRequestDTO.class);

        mockMvc.perform(patch(InstanceController.BASE_NAME + InstanceController.INSTANCE_NAME_ENDPOINT_WITH_PATH_VARIABLE, encodedSocketName).content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(request.getName())))
                .andExpect(jsonPath("$.socket", is(request.getSocket())));

        final InstanceEntity newInstance = new InstanceEntity();
        newInstance.setName("new name");
        newInstance.setSocket("localhost:9998");
        newInstance.setCreatedBy(userRepository.findByEmail("admin@email.com").orElseThrow());
        newInstance.setCreatedOn(new Date());
        instanceRepository.save(newInstance);

        //should throw instance already exist by name
        final String encodedNewName = encodeStringToBase64(request.getName());
        request.setName(newInstance.getName());
        mockMvc.perform(patch(InstanceController.BASE_NAME + InstanceController.INSTANCE_NAME_ENDPOINT_WITH_PATH_VARIABLE, encodedNewName).content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());

        //should throw instance exist by socket
        request.setName("new name wow");
        request.setSocket(newInstance.getSocket());
        final MvcResult mvcResult = mockMvc.perform(patch(InstanceController.BASE_NAME + InstanceController.INSTANCE_NAME_ENDPOINT_WITH_PATH_VARIABLE, encodedNewName)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andReturn();
        assertNotNull(mvcResult.getResponse());
    }

    @Test
    void testUpdateActiveProperty() throws Exception {
        assertDoesNotThrow(() -> instanceHealthChecker.check());

        mockMvc.perform(get(InstanceController.BASE_NAME + InstanceController.STATUS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("count").value(1))
                .andExpect(jsonPath("need_attention").value(1));
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

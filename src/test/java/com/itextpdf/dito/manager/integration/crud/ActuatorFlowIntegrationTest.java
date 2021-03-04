package com.itextpdf.dito.manager.integration.crud;

import com.itextpdf.dito.manager.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ActuatorFlowIntegrationTest extends AbstractIntegrationTest {

    public static final String ACTUATOR_PATH = "/actuator";
    public static final String ACTUATOR_INFO_PATH = ACTUATOR_PATH + "/info";
    public static final String ACTUATOR_HEALTH_PATH = ACTUATOR_PATH + "/health";
    public static final String ACTUATOR_ENV_PATH = ACTUATOR_PATH + "/env";

    @Test
    void testInfoEndPoint() throws Exception {
        final MvcResult result = mockMvc.perform(get(ACTUATOR_INFO_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("build.version").isNotEmpty())
                .andExpect(jsonPath("build.name").isNotEmpty())
                .andExpect(jsonPath("build.time").isNotEmpty())
                .andExpect(jsonPath("licenses").isArray())
                .andExpect(jsonPath("dependencies").isArray())
                .andReturn();
        assertNotNull(result.getResponse());
    }

    @Test
    void testHealthEndPoint() throws Exception {
        final MvcResult result = mockMvc.perform(get(ACTUATOR_HEALTH_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("status").isNotEmpty())
                .andExpect(jsonPath("components.db").isNotEmpty())
                .andExpect(jsonPath("components.diskSpace").isNotEmpty())
                .andExpect(jsonPath("components.ping").isNotEmpty())
                .andExpect(jsonPath("components.ping.status").value("UP"))
                .andReturn();
        assertNotNull(result.getResponse());
    }

    @Test
    void testEnvEndPoint() throws Exception {
        final MvcResult result = mockMvc.perform(get(ActuatorFlowIntegrationTest.ACTUATOR_ENV_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("activeProfiles", containsInAnyOrder("integration-test")))
                .andExpect(jsonPath("propertySources").isArray())
                .andReturn();
        assertNotNull(result.getResponse());
    }


}

package com.itextpdf.dito.manager.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;

import java.net.URI;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

/**
 * Base class for integration tests.
 * Integration tests use in-memory H2 database initialized by application Liquibase scripts.
 * All properties should be set up in integration-test Spring profile.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integration-test")
@WithMockUser(username = "admin@email.com", password = "admin@email.com")
public abstract class AbstractIntegrationTest {
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected MockMvc mockMvc;

    protected ResultActions performPostFilesInteraction(URI uri, MockMultipartFile... files) throws Exception {
        MockMultipartHttpServletRequestBuilder multipartRequestBuilder = multipart(uri);
        for (int i = 0; i < files.length; i++) {
            multipartRequestBuilder = multipartRequestBuilder.file(files[i]);
        }
        return this.mockMvc.perform(multipartRequestBuilder);
    }
}

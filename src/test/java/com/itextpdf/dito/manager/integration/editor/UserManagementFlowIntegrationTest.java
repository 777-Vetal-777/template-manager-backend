package com.itextpdf.dito.manager.integration.editor;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.UriComponentsBuilder;

import com.itextpdf.dito.manager.integration.AbstractIntegrationTest;
import com.itextpdf.dito.manager.integration.editor.controller.user.UserManagementController;

public class UserManagementFlowIntegrationTest extends AbstractIntegrationTest {
	
	@Test
	public void dataSampleNotFoundTest() throws Exception {
		final URI uri = UriComponentsBuilder
				.fromUriString(UserManagementController.CURRENT).build()
				.encode().toUri();
		final MvcResult result = mockMvc.perform(get(uri)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("firstName").value("admin"))
				.andExpect(jsonPath("lastName").value("admin"))
				.andExpect(jsonPath("email").value("admin@email.com"))
				.andReturn();
		assertNotNull(result.getResponse());
	}
	
}

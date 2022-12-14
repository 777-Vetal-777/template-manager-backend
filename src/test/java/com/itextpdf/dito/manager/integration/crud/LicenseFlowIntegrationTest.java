package com.itextpdf.dito.manager.integration.crud;

import com.itextpdf.dito.manager.component.mapper.license.impl.DitoLicenseInfoHelper;
import com.itextpdf.dito.manager.component.mapper.license.impl.LicenseMapperImpl;
import com.itextpdf.dito.manager.controller.workspace.WorkspaceController;
import com.itextpdf.dito.manager.integration.AbstractIntegrationTest;
import com.itextpdf.dito.manager.repository.instance.InstanceRepository;
import com.itextpdf.dito.manager.repository.license.LicenseRepository;
import com.itextpdf.dito.manager.repository.workspace.WorkspaceRepository;
import com.itextpdf.dito.manager.service.license.impl.LicenseServiceImpl;
import com.itextpdf.dito.sdk.license.DitoLicenseException;
import com.itextpdf.kernel.xmp.impl.Base64;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class LicenseFlowIntegrationTest  extends AbstractIntegrationTest {

	private static final String WORKSPACE_NAME = "workspace-test";
    private static final String WORKSPACE_BASE64_ENCODED_NAME = Base64.encode(WORKSPACE_NAME);
    
    @Autowired
    private LicenseRepository licenseRepository;
    
    @SpyBean
    LicenseServiceImpl licenseService;  
    
    @SpyBean
    LicenseMapperImpl licenseMapper;
   
    @MockBean
    DitoLicenseInfoHelper ditoHelper;
    
    @Autowired
    private WorkspaceRepository workspaceRepository;

    @Autowired
    private InstanceRepository instanceRepository;
    
    @AfterEach
    public void clearDb() {
        workspaceRepository.deleteAll();
        instanceRepository.deleteAll();
    	licenseRepository.deleteAll();
    }
    

	@BeforeEach
    void tearUp() throws Exception {
		Mockito.when(licenseMapper.getDitoHelper(Files.readAllBytes(Path.of("src/test/resources/test-data/license/volume-andersen.xml")))).thenReturn(ditoHelper);
    }
 
	@Test
	public void test_success_upload() throws Exception {
	
		Mockito.doNothing().when(licenseService).checkDitoLicense(ArgumentMatchers.<byte[]>any());
		
		Mockito.when(ditoHelper.getExpirationDate()).thenReturn(new Date());
		Mockito.when(ditoHelper.getLimits()).thenReturn("15");
		Mockito.when(ditoHelper.getRemainingEvents()).thenReturn(10L);
		Mockito.when(ditoHelper.getType()).thenReturn("Limited");
		
		final MockMultipartFile file = new MockMultipartFile("license", "volume-andersen.xml", "text/xml",
				Files.readAllBytes(Paths.get("src/test/resources/test-data/license/volume-andersen.xml")));
		final URI uri = UriComponentsBuilder
				.fromUriString(WorkspaceController.BASE_NAME + "/" + WORKSPACE_BASE64_ENCODED_NAME + "/license").build()
				.encode().toUri();

		mockMvc.perform(MockMvcRequestBuilders.multipart(uri).file(file).contentType(MediaType.MULTIPART_FORM_DATA))
				.andExpect(status().isOk())
				.andExpect(jsonPath("expirationDate").isNotEmpty())
				.andExpect(jsonPath("volumeUsed").isNotEmpty())
				.andExpect(jsonPath("fileName").value(file.getOriginalFilename()))
				.andExpect(jsonPath("volumeLimit").isNotEmpty()).andExpect(jsonPath("isUnlimited").isNotEmpty());

	}

	@Test
	public void test_fail_upload() throws Exception {

		Mockito.doThrow(DitoLicenseException.class).when(licenseService)
				.checkDitoLicense(ArgumentMatchers.<byte[]>any());
		final MockMultipartFile file = new MockMultipartFile("license", "volume-andersen.xml", "text/xml",
				Files.readAllBytes(Paths.get("src/test/resources/test-data/license/volume-andersen.xml")));
		final URI uri = UriComponentsBuilder
				.fromUriString(WorkspaceController.BASE_NAME + "/" + WORKSPACE_BASE64_ENCODED_NAME + "/license").build()
				.encode().toUri();

		mockMvc.perform(MockMvcRequestBuilders.multipart(uri).file(file).contentType(MediaType.MULTIPART_FORM_DATA))
				.andExpect(status().isBadRequest());

	}
	
	@Test
	public void test_subscription_plan() throws Exception {

		Mockito.when(ditoHelper.getExpirationDate()).thenReturn(new Date());
		Mockito.when(ditoHelper.getLimits()).thenReturn("50");
		Mockito.when(ditoHelper.getRemainingEvents()).thenReturn(30L);
		Mockito.when(ditoHelper.getType()).thenReturn("Limited");

		final MockMultipartFile file = new MockMultipartFile("license", "volume-andersen.xml", "text/xml",
				Files.readAllBytes(Paths.get("src/test/resources/test-data/license/volume-andersen.xml")));
		final URI uri = UriComponentsBuilder
				.fromUriString(WorkspaceController.BASE_NAME + "/" + WORKSPACE_BASE64_ENCODED_NAME + "/license").build()
				.encode().toUri();

		mockMvc.perform(MockMvcRequestBuilders.multipart(uri).file(file).contentType(MediaType.MULTIPART_FORM_DATA))
				.andExpect(status().isOk());

		final URI uriGet = UriComponentsBuilder
				.fromUriString(WorkspaceController.BASE_NAME + "/" + WORKSPACE_BASE64_ENCODED_NAME + "/license").build()
				.encode().toUri();

		mockMvc.perform(get(uriGet)).andExpect(status().isOk()).andExpect(jsonPath("expirationDate").isNotEmpty())
				.andExpect(jsonPath("volumeUsed").value(20))
				.andExpect(jsonPath("fileName").value("volume-andersen.xml"))
				.andExpect(jsonPath("volumeLimit").value(50))
				.andExpect(jsonPath("isUnlimited").value(false));
		assertFalse(licenseRepository.findAll().isEmpty());

	}

}
package com.itextpdf.dito.manager.component.client.instance;

import com.itextpdf.dito.manager.component.client.instance.impl.InstanceClientImpl;
import com.itextpdf.dito.manager.controller.instance.InstanceController;
import com.itextpdf.dito.manager.dto.instance.register.InstanceRegisterResponseDTO;
import com.itextpdf.dito.manager.repository.instance.InstanceRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithUserDetails("admin@email.com")
@ActiveProfiles("integration-test")
@ExtendWith(MockitoExtension.class)
public class InstanceClientImplTest {
    private final static String INSTANCE_NAME = "INSTANCE_NAME";
    private final static String SOCKET_EXAMPLE = "http://asd10.10.15.15:9092";
    private final static String ENCODED_SOCKET_EXAMPLE = Base64.getEncoder().encodeToString(SOCKET_EXAMPLE.getBytes(StandardCharsets.UTF_8));

    @Autowired
    private InstanceRepository instanceRepository;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WebClient webClientMock;

    @SuppressWarnings("rawtypes")
    @MockBean
    private WebClient.RequestHeadersSpec requestHeadersSpecMock;

    @SuppressWarnings("rawtypes")
    @MockBean
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock;

    @MockBean
    private WebClient.ResponseSpec responseSpecMock;

    @InjectMocks
    private InstanceClientImpl instanceClient;

    @AfterEach
    public void cleanUp() {
        instanceRepository.deleteAll();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldSendPingInstance() throws Exception {
        final InstanceRegisterResponseDTO post = new InstanceRegisterResponseDTO();
        post.setToken("randToken");
        when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(anyString())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(ArgumentMatchers.<Class<InstanceRegisterResponseDTO>>notNull())).thenReturn(Mono.just(post));
        final MvcResult result = mockMvc.perform(get(InstanceController.BASE_NAME + InstanceController.INSTANCE_STATUS_ENDPOINT, ENCODED_SOCKET_EXAMPLE)).andExpect(status().isOk()).andReturn();
        assertNotNull(result.getResponse());
        mockMvc.perform(get(InstanceController.BASE_NAME + InstanceController.INSTANCE_STATUS_ENDPOINT, ENCODED_SOCKET_EXAMPLE)).andExpect(status().isOk());
    }
}

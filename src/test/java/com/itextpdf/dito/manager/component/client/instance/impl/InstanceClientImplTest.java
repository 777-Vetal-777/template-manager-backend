package com.itextpdf.dito.manager.component.client.instance.impl;

import com.itextpdf.dito.manager.dto.instance.register.InstanceErrorResponseDTO;
import com.itextpdf.dito.manager.dto.instance.register.InstanceRegisterResponseDTO;
import com.itextpdf.dito.manager.dto.template.deployment.TemplateDeploymentDTO;
import com.itextpdf.dito.manager.dto.template.deployment.TemplateDescriptorDTO;
import com.itextpdf.dito.manager.exception.instance.NotReachableInstanceException;
import com.itextpdf.dito.manager.exception.instance.deployment.SdkInstanceException;
import com.itextpdf.dito.manager.repository.instance.InstanceRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@WithUserDetails("admin@email.com")
@ActiveProfiles("integration-test")
@ExtendWith(MockitoExtension.class)
public class InstanceClientImplTest {
    private final static String SOCKET_EXAMPLE = "http://asd10.10.15.15:9092";

    @Autowired
    private InstanceRepository instanceRepository;

    @MockBean
    private WebClient webClientMock;

    // ! Don't change order of WebClient' mocks !

    @SuppressWarnings("rawtypes")
    @MockBean
    private WebClient.RequestHeadersSpec requestHeadersSpecMock;

    @SuppressWarnings("rawtypes")
    @MockBean
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock;

    @MockBean
    private WebClient.RequestBodySpec requestBodySpecMock;

    @MockBean
    private WebClient.RequestBodyUriSpec requestBodyUriSpecMock;

    @MockBean
    private WebClient.ResponseSpec responseSpecMock;

    @Autowired
    private InstanceClientImpl instanceClient;

    @AfterEach
    public void cleanUp() {
        instanceRepository.deleteAll();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldSendPingInstance() {
        // given
        final InstanceRegisterResponseDTO post = new InstanceRegisterResponseDTO();
        post.setToken("randToken");
        when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(anyString())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(ArgumentMatchers.<Class<InstanceRegisterResponseDTO>>notNull())).thenReturn(Mono.just(post));

        // when
        assertDoesNotThrow(() -> {instanceClient.ping(SOCKET_EXAMPLE);});

        // then no errors occurred
    }

    @Test
    public void shouldThrowExceptionOnPingEmptySocket() {
        assertThrows(
                IllegalArgumentException.class,
                () -> {
                    instanceClient.ping("");
                }
        );
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldRegisterInstance() {
        // given
        final InstanceRegisterResponseDTO mockedResponseDto = new InstanceRegisterResponseDTO();
        mockedResponseDto.setToken("randToken");

        when(webClientMock.post()).thenReturn(requestBodyUriSpecMock);
        when(requestBodyUriSpecMock.uri(anyString())).thenReturn(requestBodySpecMock);
        when(requestBodySpecMock.header(any(), any())).thenReturn(requestBodySpecMock);
        when(requestBodySpecMock.headers(any())).thenReturn(requestBodySpecMock);
        when(requestBodySpecMock.bodyValue(any())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.onStatus(any(), any())).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(ArgumentMatchers.<Class<InstanceRegisterResponseDTO>>notNull()))
                .thenReturn(Mono.just(mockedResponseDto));

        // when
        final InstanceRegisterResponseDTO responseDTO = instanceClient.register(
                SOCKET_EXAMPLE, "", "");

        // then
        assertNotNull(responseDTO);
        assertEquals("randToken", responseDTO.getToken());
    }

    @Test
    public void shouldThrowExceptionOnRegisterEmptySocket() {
        // given
        when(webClientMock.post()).thenReturn(requestBodyUriSpecMock);
        when(requestBodyUriSpecMock.uri(anyString())).thenThrow(new IllegalArgumentException());

        // expected
        assertThrows(
                NotReachableInstanceException.class,
                // when
                () -> {
                    instanceClient.register("", "", "");
                }
        );
    }

    @SuppressWarnings("unchecked")
    @Test
    void shouldUnregisterInstance() {
        // given
        when(webClientMock.post()).thenReturn(requestBodyUriSpecMock);
        when(requestBodyUriSpecMock.uri(anyString())).thenReturn(requestBodySpecMock);
        when(requestBodySpecMock.header(any(), any())).thenReturn(requestBodySpecMock);
        when(requestBodySpecMock.headers(any())).thenReturn(requestBodySpecMock);
        when(requestBodySpecMock.bodyValue(any())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.onStatus(any(), any())).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(ArgumentMatchers.<Class<Void>>any())).thenReturn(Mono.empty());

        // when
        assertDoesNotThrow(() -> instanceClient.unregister(SOCKET_EXAMPLE, "randToken"));

        // then no errors occurred
    }

    @SuppressWarnings("unchecked")
    @Test
    void shouldPromoteTemplateToInstance() {
        //given
        final TemplateDescriptorDTO descriptorDTO = new TemplateDescriptorDTO();
        descriptorDTO.setTemplateName("template_name_example");
        descriptorDTO.setAlias("alias_example");
        descriptorDTO.setVersion("version_example");

        final TemplateDeploymentDTO mockedResponseDto = new TemplateDeploymentDTO();
        mockedResponseDto.setAlias("alias_example");
        mockedResponseDto.setVersion("version_example");

        File projectFile = Mockito.mock(File.class);

        when(webClientMock.post()).thenReturn(requestBodyUriSpecMock);
        when(requestBodyUriSpecMock.uri(anyString())).thenReturn(requestBodySpecMock);
        when(requestBodySpecMock.headers(any())).thenReturn(requestBodySpecMock);
        when(requestBodySpecMock.contentType(any())).thenReturn(requestBodySpecMock);
        when(requestBodySpecMock.body(any())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.onStatus(any(), any())).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(ArgumentMatchers.<Class<TemplateDeploymentDTO>>notNull()))
                .thenReturn(Mono.just(mockedResponseDto));

        // when
        final TemplateDeploymentDTO responseDTO = instanceClient.promoteTemplateToInstance(
                "randToken",
                SOCKET_EXAMPLE,
                descriptorDTO,
                projectFile);

        // then
        assertNotNull(responseDTO);
        assertEquals("alias_example", responseDTO.getAlias());
        assertEquals("version_example", responseDTO.getVersion());
    }

    @SuppressWarnings("unchecked")
    @Test
    void shouldRemoveTemplateFromInstance() {
        //given
        final TemplateDeploymentDTO mockedResponseDto = new TemplateDeploymentDTO();
        mockedResponseDto.setAlias("alias_example");
        mockedResponseDto.setVersion("version_example");

        when(webClientMock.delete()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(anyString())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.headers(any())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.onStatus(any(), any())).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(ArgumentMatchers.<Class<TemplateDeploymentDTO>>notNull()))
                .thenReturn(Mono.just(mockedResponseDto));

        // when
        final TemplateDeploymentDTO responseDTO = instanceClient.removeTemplateFromInstance(
                "randToken",
                SOCKET_EXAMPLE,
                "alias_example");

        // then
        assertNotNull(responseDTO);
        assertEquals("alias_example", responseDTO.getAlias());
        assertEquals("version_example", responseDTO.getVersion());
    }

    @Test
    void shouldProcessInstanceError() {
        // given
        final InstanceErrorResponseDTO mockedResponseDto = new InstanceErrorResponseDTO();
        mockedResponseDto.setCode(999);
        mockedResponseDto.setMessage("error_message_example");

        ClientResponse clientResponseMock = Mockito.mock(ClientResponse.class);
        when(clientResponseMock.bodyToMono(ArgumentMatchers.<Class<InstanceErrorResponseDTO>>notNull()))
                .thenReturn(Mono.just(mockedResponseDto));

        // expected
        assertThrows(
                SdkInstanceException.class,
                // when
                () -> {
                    Mono<? extends Throwable> result = instanceClient.processInstanceError(clientResponseMock, SOCKET_EXAMPLE, "error_title: ");
                    result.block();
                }
        );
    }
}

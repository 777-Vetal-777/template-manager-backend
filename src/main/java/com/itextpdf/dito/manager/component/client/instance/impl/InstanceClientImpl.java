package com.itextpdf.dito.manager.component.client.instance.impl;

import com.itextpdf.dito.manager.component.client.instance.InstanceClient;
import com.itextpdf.dito.manager.dto.instance.register.InstanceErrorResponseDTO;
import com.itextpdf.dito.manager.dto.instance.register.InstanceRegisterRequestDTO;
import com.itextpdf.dito.manager.dto.instance.register.InstanceRegisterResponseDTO;
import com.itextpdf.dito.manager.dto.template.deployment.TemplateDeploymentDTO;
import com.itextpdf.dito.manager.dto.template.deployment.TemplateDescriptorDTO;
import com.itextpdf.dito.manager.exception.instance.NotReachableInstanceException;
import com.itextpdf.dito.manager.exception.instance.deployment.SdkInstanceException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.File;
import java.time.Duration;

@Component
public class InstanceClientImpl implements InstanceClient {

    private static final Logger log = LogManager.getLogger(InstanceClientImpl.class);

    //uri for validating iText SDK application status
    private static final String INSTANCE_API_STATUS_URL = "/api/status";
    private static final String INSTANCE_REGISTER_ENDPOINT = "/api/admin/register";
    private static final String INSTANCE_UNREGISTER_ENDPOINT = "/api/admin/unregister";
    private static final String INSTANCE_DEPLOYMENT_ENDPOINT = "/api/deployments";

    private static final String WORKSPACE_ALIAS = "Template Manager";

    private static final Long INSTANCE_AVAILABILITY_TIMEOUT_IN_SECONDS = 1L;

    private final WebClient webClient;

    public InstanceClientImpl() {
        webClient = WebClient.create();
    }

    @Override
    public void ping(final String socket) {
        if (StringUtils.isEmpty(socket)) {
            throw new IllegalArgumentException("Instance's socket must be presented.");
        }
        final String instanceStatusUrl = new StringBuilder().append(socket).append(INSTANCE_API_STATUS_URL).toString();
        try {
            webClient.get()
                    .uri(instanceStatusUrl)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(Duration.ofSeconds(INSTANCE_AVAILABILITY_TIMEOUT_IN_SECONDS));
        } catch (Exception exception) {
            log.warn(exception);
            throw new NotReachableInstanceException(socket);
        }
    }

    @Override
    public InstanceRegisterResponseDTO register(final String instanceSocket) {
        final String instanceRegisterUrl = new StringBuilder().append(instanceSocket).append(INSTANCE_REGISTER_ENDPOINT).toString();
        final InstanceRegisterRequestDTO instanceRegisterRequestDTO = new InstanceRegisterRequestDTO();
        instanceRegisterRequestDTO.setSubject(WORKSPACE_ALIAS);
        try {
            final Mono<InstanceRegisterResponseDTO> response = WebClient.create()
                    .post()
                    .uri(instanceRegisterUrl)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(instanceRegisterRequestDTO)
                    .retrieve()
                    .onStatus(HttpStatus::isError, clientResponse -> {
                        final Mono<InstanceErrorResponseDTO> errorMessage = clientResponse.bodyToMono(InstanceErrorResponseDTO.class);
                        return errorMessage.flatMap(message -> {
                            log.warn(message.getMessage());
                            final String errorText = "Failed to register API instance: ";
                            throw new SdkInstanceException(errorText, instanceSocket, message.getCode(), message.getMessage());
                        });
                    })
                    .bodyToMono(InstanceRegisterResponseDTO.class);
            return response.block();
        } catch (IllegalArgumentException e) {
            throw new NotReachableInstanceException(instanceSocket);
        }
    }

    @Override
    public void unregister(final String instanceSocket, final String instanceToken) {
        final String instanceUnregisterUrl = new StringBuilder().append(instanceSocket).append(INSTANCE_UNREGISTER_ENDPOINT).toString();
        final InstanceRegisterRequestDTO instanceRegisterRequestDTO = new InstanceRegisterRequestDTO();
        instanceRegisterRequestDTO.setSubject(WORKSPACE_ALIAS);
        final Mono<Void> response = WebClient.create()
                .post()
                .uri(instanceUnregisterUrl)
                .headers(h -> h.setBearerAuth(instanceToken))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(instanceRegisterRequestDTO)
                .retrieve()
                .onStatus(HttpStatus::isError, clientResponse -> {
                    final Mono<InstanceErrorResponseDTO> errorMessage = clientResponse.bodyToMono(InstanceErrorResponseDTO.class);
                    return errorMessage.flatMap(message -> {
                        log.warn(message.getMessage());
                        final String errorText = "Failed to unregister API instance: ";
                        throw new SdkInstanceException(errorText, instanceSocket, message.getCode(), message.getMessage());
                    });
                })
                .bodyToMono(Void.class);
        response.block();
    }

    @Override
    public TemplateDeploymentDTO promoteTemplateToInstance(final String instanceRegisterToken,
                                                           final String instanceSocket,
                                                           final TemplateDescriptorDTO descriptorDTO,
                                                           final File templateProject) {
        final String forceDeployParam = "?forceReplace=true";
        final String instanceDeploymentUrl = new StringBuilder().append(instanceSocket)
                .append(INSTANCE_DEPLOYMENT_ENDPOINT).append(forceDeployParam).toString();
        final Mono<TemplateDeploymentDTO> response = WebClient.create()
                .post()
                .uri(instanceDeploymentUrl)
                .headers(h -> h.setBearerAuth(instanceRegisterToken))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(fromFile(descriptorDTO, templateProject)))
                .retrieve()
                .onStatus(HttpStatus::isError, clientResponse -> {
                    final Mono<InstanceErrorResponseDTO> errorMessage = clientResponse.bodyToMono(InstanceErrorResponseDTO.class);
                    return errorMessage.flatMap(message -> {
                        log.warn(message.getMessage());
                        final String errorText = "Failed to promote template to API instance: ";
                        throw new SdkInstanceException(errorText, instanceSocket, message.getCode(), message.getMessage());
                    });
                })
                .bodyToMono(TemplateDeploymentDTO.class);
        return response.block();
    }

    @Override
    public TemplateDeploymentDTO removeTemplateFromInstance(final String instanceRegisterToken,
                                                            final String instanceSocket,
                                                            final String templateAlias) {
        final String instanceDeploymentUrl = new StringBuilder().append(instanceSocket)
                .append(INSTANCE_DEPLOYMENT_ENDPOINT)
                .append("/")
                .append(templateAlias)
                .toString();
        final Mono<TemplateDeploymentDTO> response = WebClient.create()
                .delete()
                .uri(instanceDeploymentUrl)
                .headers(h -> h.setBearerAuth(instanceRegisterToken))
                .retrieve()
                .onStatus(HttpStatus::isError, clientResponse -> {
                    final Mono<InstanceErrorResponseDTO> errorMessage = clientResponse.bodyToMono(InstanceErrorResponseDTO.class);
                    return errorMessage.flatMap(message -> {
                        log.warn(message.getMessage());
                        final String errorText = "Failed to un-deploy template from API instance: ";
                        throw new SdkInstanceException(errorText, instanceSocket, message.getCode(), message.getMessage());
                    });
                })
                .bodyToMono(TemplateDeploymentDTO.class);
        return response.block();
    }

    private MultiValueMap<String, HttpEntity<?>> fromFile(final TemplateDescriptorDTO templateDescriptorDTO,
                                                          final File templateZipFile) {
        final MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("template_project", new FileSystemResource(templateZipFile), MediaType.APPLICATION_OCTET_STREAM);
        builder.part("descriptor", templateDescriptorDTO, MediaType.APPLICATION_JSON);
        return builder.build();
    }
}

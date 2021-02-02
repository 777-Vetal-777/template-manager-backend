package com.itextpdf.dito.manager.component.client.instance.impl;

import com.itextpdf.dito.manager.component.client.instance.InstanceClient;
import com.itextpdf.dito.manager.dto.instance.register.InstanceRegisterRequestDTO;
import com.itextpdf.dito.manager.dto.instance.register.InstanceRegisterResponseDTO;
import com.itextpdf.dito.manager.entity.WorkspaceEntity;
import com.itextpdf.dito.manager.exception.instance.NotReachableInstanceException;
import com.itextpdf.dito.manager.repository.workspace.WorkspaceRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Component
public class InstanceClientImpl implements InstanceClient {

    private static final Logger log = LogManager.getLogger(InstanceClientImpl.class);

    //uri for validating iText SDK appilcation status
    private static final String INSTANCE_API_STATUS_URL = "/api/status";

    private static final String INSTANCE_REGISTER_ENDPOINT = "/api/admin/register";
    private static final String INSTANCE_UNREGISTER_ENDPOINT = "/api/admin/unregister";

    private static final Long INSTANCE_AVAILABILITY_TIMEOUT_IN_SECONDS = 1L;

    private final WebClient webClient;

    private final WorkspaceRepository workspaceRepository;

    public InstanceClientImpl(final WorkspaceRepository workspaceRepository) {
        this.workspaceRepository = workspaceRepository;
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
        instanceRegisterRequestDTO.setSubject(getCurrentWorkspaceName());
        final Mono<InstanceRegisterResponseDTO> response = WebClient.create()
                .post()
                .uri(instanceRegisterUrl)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(instanceRegisterRequestDTO)
                .exchange()
                .flatMap(clientResponse -> {
                    //Error handling
                    if (clientResponse.statusCode().isError()) {
                        return clientResponse.createException().flatMap(Mono::error);
                    }
                    return clientResponse.bodyToMono(InstanceRegisterResponseDTO.class);
                });
        return response.block();
    }

    @Override
    public void unregister(final String instanceSocket, final String instanceToken) {
        final String instanceUnregisterUrl = new StringBuilder().append(instanceSocket).append(INSTANCE_UNREGISTER_ENDPOINT).toString();
        final InstanceRegisterRequestDTO instanceRegisterRequestDTO = new InstanceRegisterRequestDTO();
        instanceRegisterRequestDTO.setSubject(getCurrentWorkspaceName());
        final Mono<Void> response = WebClient.create()
                .post()
                .uri(instanceUnregisterUrl)
                .headers(h -> h.setBearerAuth(instanceToken))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(instanceRegisterRequestDTO)
                .exchange()
                .flatMap(clientResponse -> {
                    //Error handling
                    if (clientResponse.statusCode().isError()) {
                        return clientResponse.createException().flatMap(Mono::error);
                    }
                    return clientResponse.bodyToMono(Void.class);
                });
        response.block();
    }

    private String getCurrentWorkspaceName() {
        final List<WorkspaceEntity> workspaceEntityList = workspaceRepository.findAll();
        if (CollectionUtils.isEmpty(workspaceEntityList)) {
            throw new IllegalArgumentException("No default workspace was set");
        }
        return workspaceEntityList.get(0).getName();
    }
}

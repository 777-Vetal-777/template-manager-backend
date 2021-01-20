package com.itextpdf.dito.manager.component.client.instance.impl;

import com.itextpdf.dito.manager.component.client.instance.InstanceClient;
import com.itextpdf.dito.manager.exception.instance.NotReachableInstanceException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Component
public class InstanceClientImpl implements InstanceClient {

    private static final Logger log = LogManager.getLogger(InstanceClientImpl.class);

    //uri for validating iText SDK appilcation status
    private static final String API_STATUS_URL = "/api/status";
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
        final String instanceStatusUrl = new StringBuilder().append(socket).append(API_STATUS_URL).toString();
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
}

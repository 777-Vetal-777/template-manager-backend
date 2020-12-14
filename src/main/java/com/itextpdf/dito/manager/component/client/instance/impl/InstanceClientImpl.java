package com.itextpdf.dito.manager.component.client.instance.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

public class InstanceClientImpl {

    private final WebClient webClient;

    public InstanceClientImpl(final String socket) {
        if (StringUtils.isNoneEmpty(socket)) {
            webClient = WebClient.builder().baseUrl(socket).build();
        } else {
            throw new IllegalArgumentException("Instance's socket must be presented.");
        }
    }

    public void ping() {
        // TODO: ping instance's API
    }
}

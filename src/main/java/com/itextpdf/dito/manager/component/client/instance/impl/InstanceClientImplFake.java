package com.itextpdf.dito.manager.component.client.instance.impl;

import com.itextpdf.dito.manager.component.client.instance.InstanceClient;
import com.itextpdf.dito.manager.exception.instance.NotReachableInstanceException;

import reactor.util.StringUtils;

public class InstanceClientImplFake implements InstanceClient {
    // Only this socket will generate bad gateway exception.
    private static final String FAKE_SOCKET = "localhost:9999";

    private boolean badGateway;

    public InstanceClientImplFake(final String socket) {
        if (StringUtils.isEmpty(socket) || FAKE_SOCKET.equals(socket)) {
            badGateway = true;
        }
    }

    @Override
    public void ping() {
        if (badGateway) {
            throw new NotReachableInstanceException();
        }
    }
}

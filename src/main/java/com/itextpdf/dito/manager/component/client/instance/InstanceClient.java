package com.itextpdf.dito.manager.component.client.instance;

import com.itextpdf.dito.manager.dto.instance.register.InstanceRegisterResponseDTO;

public interface InstanceClient {
    void ping(String socket);

    InstanceRegisterResponseDTO register(String instanceSocket);

    void unregister(String instanceSocket, String token);
}

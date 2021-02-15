package com.itextpdf.dito.manager.component.client.instance;

import com.itextpdf.dito.manager.dto.instance.register.InstanceRegisterResponseDTO;
import com.itextpdf.dito.manager.dto.template.deployment.TemplateDeploymentDTO;
import com.itextpdf.dito.manager.dto.template.deployment.TemplateDescriptorDTO;

import java.io.File;

public interface InstanceClient {
    void ping(String socket);

    InstanceRegisterResponseDTO register(String instanceSocket);

    void unregister(String instanceSocket, String token);

    TemplateDeploymentDTO promoteTemplateToInstance(String instanceRegisterToken, String instanceSocket, TemplateDescriptorDTO descriptorDTO,File templateProject);

    TemplateDeploymentDTO removeTemplateFromInstance(String instanceRegisterToken, String instanceSocket, String templateAlias);

}

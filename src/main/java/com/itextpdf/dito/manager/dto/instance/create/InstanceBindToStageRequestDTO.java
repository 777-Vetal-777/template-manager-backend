package com.itextpdf.dito.manager.dto.instance.create;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;

public class InstanceBindToStageRequestDTO {
    @NotBlank
    @Schema(example = "My-workspace")
    String workspaceName;
    @NotBlank
    @Schema(example = "Default-instance")
    String instanceName;

    public String getWorkspaceName() {
        return workspaceName;
    }

    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }
}

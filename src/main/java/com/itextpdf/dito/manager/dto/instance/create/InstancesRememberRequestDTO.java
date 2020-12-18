package com.itextpdf.dito.manager.dto.instance.create;


import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

public class InstancesRememberRequestDTO {
    @NotEmpty
    private List<@Valid InstanceRememberRequestDTO> instances;

    public List<InstanceRememberRequestDTO> getInstances() {
        return instances;
    }

    public void setInstances(List<InstanceRememberRequestDTO> instances) {
        this.instances = instances;
    }
}

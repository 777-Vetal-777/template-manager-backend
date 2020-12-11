package com.itextpdf.dito.manager.dto.instance.create;


import com.itextpdf.dito.manager.dto.instance.InstanceDTO;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

public class InstanceRememberRequestDTO {
    @NotEmpty
    private List<@Valid InstanceDTO> instances;

    public List<InstanceDTO> getInstances() {
        return instances;
    }

    public void setInstances(List<InstanceDTO> instances) {
        this.instances = instances;
    }
}

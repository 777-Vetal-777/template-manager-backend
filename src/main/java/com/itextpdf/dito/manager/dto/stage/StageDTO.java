package com.itextpdf.dito.manager.dto.stage;

import com.itextpdf.dito.manager.dto.instance.InstanceDTO;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

public class StageDTO {
    @NotBlank
    private String name;
    @NotEmpty
    private List<@Valid InstanceDTO> instances;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<InstanceDTO> getInstances() {
        return instances;
    }

    public void setInstances(List<InstanceDTO> instances) {
        this.instances = instances;
    }
}

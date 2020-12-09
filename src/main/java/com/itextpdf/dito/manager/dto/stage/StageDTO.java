package com.itextpdf.dito.manager.dto.stage;

import com.itextpdf.dito.manager.dto.instance.InstanceDTO;

import java.util.Set;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

public class StageDTO {
    @NotBlank
    private String name;
    @NotEmpty
    private Set<InstanceDTO> instances;
    private Set<StageDTO> stages;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<InstanceDTO> getInstances() {
        return instances;
    }

    public void setInstances(Set<InstanceDTO> instances) {
        this.instances = instances;
    }

    public Set<StageDTO> getStages() {
        return stages;
    }

    public void setStages(Set<StageDTO> stages) {
        this.stages = stages;
    }
}

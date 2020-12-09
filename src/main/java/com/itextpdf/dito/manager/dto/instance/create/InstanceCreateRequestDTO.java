package com.itextpdf.dito.manager.dto.instance.create;


import com.itextpdf.dito.manager.dto.instance.InstanceDTO;
import org.springframework.lang.NonNull;

import java.util.List;

public class InstanceCreateRequestDTO {
    @NonNull
    private List<InstanceDTO> instances;

    public List<InstanceDTO> getInstances() {
        return instances;
    }

    public void setInstances(List<InstanceDTO> instances) {
        this.instances = instances;
    }
}

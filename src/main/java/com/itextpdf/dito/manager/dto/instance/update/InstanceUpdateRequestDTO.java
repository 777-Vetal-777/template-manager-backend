package com.itextpdf.dito.manager.dto.instance.update;

import javax.validation.constraints.NotBlank;

public class InstanceUpdateRequestDTO {
    @NotBlank
    private String name;
    @NotBlank
    private String socket;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSocket() {
        return socket;
    }

    public void setSocket(String socket) {
        this.socket = socket;
    }

    @Override
    public String toString() {
        return "InstanceUpdateRequestDTO{" +
                "name='" + name + '\'' +
                ", socket='" + socket + '\'' +
                '}';
    }
}

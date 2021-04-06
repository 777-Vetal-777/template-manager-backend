package com.itextpdf.dito.manager.dto.instance.update;

import javax.validation.constraints.NotBlank;

public class InstanceUpdateRequestDTO {
    @NotBlank
    private String name;
    @NotBlank
    private String socket;
    private String headerName;
    private String headerValue;

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

    public String getHeaderValue() {
        return headerValue;
    }

    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }

    public String getHeaderName() {
        return headerName;
    }

    public void setHeaderValue(String headerValue) {
        this.headerValue = headerValue;
    }

    @Override
    public String toString() {
        return "InstanceUpdateRequestDTO{" +
                "name='" + name + '\'' +
                ", socket='" + socket + '\'' +
                ", header name='" + headerName + '\'' +
                ", header name='" + headerValue + '\'' +
                '}';
    }
}

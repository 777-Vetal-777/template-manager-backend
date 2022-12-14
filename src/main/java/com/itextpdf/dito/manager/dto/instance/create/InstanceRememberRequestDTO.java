package com.itextpdf.dito.manager.dto.instance.create;

import javax.validation.constraints.NotBlank;

public class InstanceRememberRequestDTO extends InstanceHeaderRequestDTO {
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
		return "InstanceRememberRequestDTO [name=" + name + ", socket=" + socket + ", headerName=" + getHeaderName()
				+ ", headerValue=" + getHeaderValue() + "]";
	}

}

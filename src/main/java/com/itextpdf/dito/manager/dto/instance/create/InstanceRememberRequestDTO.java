package com.itextpdf.dito.manager.dto.instance.create;

import javax.validation.constraints.NotBlank;

public class InstanceRememberRequestDTO {
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

	public String getHeaderName() {
		return headerName;
	}

	public void setHeaderName(String headerName) {
		this.headerName = headerName;
	}

	public String getHeaderValue() {
		return headerValue;
	}

	public void setHeaderValue(String headerValue) {
		this.headerValue = headerValue;
	}

	@Override
	public String toString() {
		return "InstanceRememberRequestDTO [name=" + name + ", socket=" + socket + ", headerName=" + headerName
				+ ", headerValue=" + headerValue + "]";
	}

}

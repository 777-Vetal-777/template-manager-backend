package com.itextpdf.dito.manager.dto.instance.create;

public class InstanceHeaderRequestDTO {
    private String headerName;
    private String headerValue;
    
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
		return "InstanceHeaderRequestDTO [headerName=" + headerName + ", headerValue=" + headerValue + "]";
	}

}

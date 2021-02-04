package com.itextpdf.dito.manager.dto.license;

import java.util.Date;

public class LicenseDTO {
    private String type;
    private Date expirationDate;
    private Long volumeLeft;
    private String volumeLimit;
    private String fileName;
    
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Date getExpirationDate() {
		return expirationDate;
	}
	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}
	public Long getVolumeLeft() {
		return volumeLeft;
	}
	public void setVolumeLeft(Long volumeLeft) {
		this.volumeLeft = volumeLeft;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getVolumeLimit() {
		return volumeLimit;
	}
	public void setVolumeLimit(String volumeLimit) {
		this.volumeLimit = volumeLimit;
	}
    
}
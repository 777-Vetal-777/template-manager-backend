package com.itextpdf.dito.manager.dto.license;

import java.util.Date;

public class LicenseDTO {
    private String type;
    private Date expirationDate;
    private Long volumeUsed;
    private Long volumeLimit;
    private String fileName;
    private Boolean isUnlimited;
    
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
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public Long getVolumeUsed() {
		return volumeUsed;
	}
	public void setVolumeUsed(Long volumeUsed) {
		this.volumeUsed = volumeUsed;
	}
	public Long getVolumeLimit() {
		return volumeLimit;
	}
	public void setVolumeLimit(Long volumeLimit) {
		this.volumeLimit = volumeLimit;
	}
	public Boolean getIsUnlimited() {
		return isUnlimited;
	}
	public void setIsUnlimited(Boolean isUnlimited) {
		this.isUnlimited = isUnlimited;
	}
    
}
package com.itextpdf.dito.manager.filter.datasample;

import java.util.List;

public class DataSampleFilter {

    private String name;
    private String modifiedBy;
    private List<String> modifiedOn;
    private String comment;
    private List<Boolean> isDefault;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public List<String> getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(List<String> modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

	public List<Boolean> getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(List<Boolean> isDefault) {
		this.isDefault = isDefault;
	}

}

package com.itextpdf.dito.manager.model.template.dtm.datasample;

import com.itextpdf.dito.manager.model.template.dtm.AbstractDtmItemVersionDescriptorModel;

public class DtmDataSampleVersionDescriptorModel extends AbstractDtmItemVersionDescriptorModel {
    private String fileName;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}

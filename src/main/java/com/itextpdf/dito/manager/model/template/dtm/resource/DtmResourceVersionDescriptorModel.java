package com.itextpdf.dito.manager.model.template.dtm.resource;

import com.itextpdf.dito.manager.model.template.dtm.AbstractDtmItemVersionDescriptorModel;
import com.itextpdf.dito.manager.model.template.dtm.DtmUsedInDescriptorModel;

import java.util.List;
import java.util.Map;

public class DtmResourceVersionDescriptorModel extends AbstractDtmItemVersionDescriptorModel {
    private String alias;
    private String fileName;
    private List<DtmUsedInDescriptorModel> usedIn;
    private Map<String, DtmFontFaceDescriptorModel> fontFaces;

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<DtmUsedInDescriptorModel> getUsedIn() {
        return usedIn;
    }

    public void setUsedIn(List<DtmUsedInDescriptorModel> usedIn) {
        this.usedIn = usedIn;
    }

    public Map<String, DtmFontFaceDescriptorModel> getFontFaces() {
        return fontFaces;
    }

    public void setFontFaces(Map<String, DtmFontFaceDescriptorModel> fontFaces) {
        this.fontFaces = fontFaces;
    }
}

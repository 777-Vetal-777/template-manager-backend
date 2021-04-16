package com.itextpdf.dito.manager.model.template.dtm.datacollection;

import com.itextpdf.dito.manager.dto.datacollection.DataCollectionType;
import com.itextpdf.dito.manager.model.template.dtm.AbstractDtmItemDescriptorModel;

import java.util.List;

public class DtmDataCollectionDescriptorModel extends AbstractDtmItemDescriptorModel<DtmDataCollectionVersionDescriptorModel> {
    private DataCollectionType type;
    private List<String> samples;

    public DataCollectionType getType() {
        return type;
    }

    public void setType(DataCollectionType type) {
        this.type = type;
    }

    public List<String> getSamples() {
        return samples;
    }

    public void setSamples(List<String> samples) {
        this.samples = samples;
    }
}

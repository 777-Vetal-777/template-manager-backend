package com.itextpdf.dito.manager.model.template.dtm.datacollection;

import com.itextpdf.dito.manager.model.template.dtm.datasample.DtmDataSampleVersionDescriptorModel;

import java.util.List;

public class DtmDataCollectionVersionDescriptorModel extends DtmDataSampleVersionDescriptorModel {
    private List<DtmDataCollectionUsedInDescriptorModel> usedIn;

    public List<DtmDataCollectionUsedInDescriptorModel> getUsedIn() {
        return usedIn;
    }

    public void setUsedIn(List<DtmDataCollectionUsedInDescriptorModel> usedIn) {
        this.usedIn = usedIn;
    }
}

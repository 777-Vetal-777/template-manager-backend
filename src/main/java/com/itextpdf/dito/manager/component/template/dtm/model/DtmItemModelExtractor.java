package com.itextpdf.dito.manager.component.template.dtm.model;

import com.itextpdf.dito.manager.model.template.dtm.DtmFileDescriptorModel;
import com.itextpdf.dito.manager.model.template.dtm.ItemType;
import com.itextpdf.dito.manager.model.template.dtm.context.DtmFileExportContext;

public interface DtmItemModelExtractor {
    ItemType getType();
    DtmFileDescriptorModel extract(DtmFileExportContext context, DtmFileDescriptorModel model);
    default int getPriority() {return 50;}
}

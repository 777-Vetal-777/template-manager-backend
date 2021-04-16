package com.itextpdf.dito.manager.component.template.dtm.model;

import com.itextpdf.dito.manager.model.template.dtm.DtmFileDescriptorModel;
import com.itextpdf.dito.manager.model.template.dtm.context.DtmFileExportContext;

public interface DtmModelExtractor {
    void extract(DtmFileExportContext context, DtmFileDescriptorModel model);
}

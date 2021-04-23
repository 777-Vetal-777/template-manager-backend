package com.itextpdf.dito.manager.component.template.dtm.read;

import com.itextpdf.dito.manager.model.template.dtm.DtmFileDescriptorModel;
import com.itextpdf.dito.manager.model.template.dtm.ItemType;
import com.itextpdf.dito.manager.model.template.dtm.context.DtmFileImportContext;

import java.nio.file.Path;

public interface DtmFileItemReader {
    void read(DtmFileImportContext context, DtmFileDescriptorModel model, Path basePath);
    ItemType getType();
    default int getPriority() { return 50; }
}

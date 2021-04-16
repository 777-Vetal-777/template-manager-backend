package com.itextpdf.dito.manager.component.template.dtm.file;

import com.itextpdf.dito.manager.model.template.dtm.ItemType;
import com.itextpdf.dito.manager.model.template.dtm.context.DtmFileExportContext;

import java.io.IOException;
import java.nio.file.Path;

public interface DtmItemFileExtractor {
    ItemType getType();
    Path extract(Path directory, DtmFileExportContext context) throws IOException;
}

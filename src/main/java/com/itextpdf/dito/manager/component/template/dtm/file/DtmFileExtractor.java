package com.itextpdf.dito.manager.component.template.dtm.file;

import com.itextpdf.dito.manager.model.template.dtm.context.DtmFileExportContext;

import java.io.IOException;
import java.nio.file.Path;

public interface DtmFileExtractor {
    void extractFiles(Path directory, DtmFileExportContext context) throws IOException;
}

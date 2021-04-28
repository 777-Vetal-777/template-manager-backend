package com.itextpdf.dito.manager.component.template.dtm.read;

import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.model.template.dtm.DtmFileDescriptorModel;
import com.itextpdf.dito.manager.model.template.dtm.context.DtmFileImportContext;

import java.nio.file.Path;
import java.util.List;

public interface DtmFileReader {
    List<TemplateEntity> read(DtmFileImportContext context, DtmFileDescriptorModel model, Path basePath);
}

package com.itextpdf.dito.manager.service.resource;

import com.itextpdf.dito.manager.dto.template.setting.TemplateImportNameModel;
import com.itextpdf.dito.manager.model.template.duplicates.DuplicatesList;

import java.io.IOException;
import java.util.Map;

public interface EmbeddedResourceImportService {
    byte[] importEmbedded(byte[] templateBody, String fileName, Map<String, TemplateImportNameModel> settings, DuplicatesList duplicatesList, String email) throws IOException;
}

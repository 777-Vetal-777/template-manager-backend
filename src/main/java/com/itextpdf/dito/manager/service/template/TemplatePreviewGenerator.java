package com.itextpdf.dito.manager.service.template;

import java.io.ByteArrayOutputStream;

public interface TemplatePreviewGenerator {
    ByteArrayOutputStream generatePreview(String templateName, String dataSampleName);
}

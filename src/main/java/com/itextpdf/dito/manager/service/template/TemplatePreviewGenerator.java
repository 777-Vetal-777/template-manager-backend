package com.itextpdf.dito.manager.service.template;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

public interface TemplatePreviewGenerator {
    ByteArrayOutputStream generatePreview(String templateName, String dataSampleName);
}

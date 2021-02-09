package com.itextpdf.dito.manager.service.template;

import java.io.OutputStream;

public interface TemplatePreviewGenerator {
    OutputStream generatePreview(String templateName, String dataSampleName);
}

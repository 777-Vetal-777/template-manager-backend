package com.itextpdf.dito.manager.service.template;

import java.io.OutputStream;

public interface TemplatePreviewGenerator {
    OutputStream generate(String templateName);
}

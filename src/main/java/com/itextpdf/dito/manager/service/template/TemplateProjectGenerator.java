package com.itextpdf.dito.manager.service.template;

import com.itextpdf.dito.manager.entity.template.TemplateEntity;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;

public interface TemplateProjectGenerator {
    File generateZipByTemplateName(TemplateEntity templateEntity);

    //TODO remove this method in future or make it private
    void createFile(String templateName, String fileName, byte[] file,
                    Map<String, Path> folders);
}

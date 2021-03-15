package com.itextpdf.dito.manager.util.cssimport;

import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.dto.template.setting.TemplateImportNameModel;
import com.itextpdf.dito.manager.model.template.duplicates.DuplicatesList;

import java.util.Map;

public class StyleTagRenamingContext {
    private final String fileName;
    private final Map<String, TemplateImportNameModel> renamingSettings;
    private final String userEmail;

    private final DuplicatesList duplicatesList;

    public StyleTagRenamingContext(final String fileName,
                                   final Map<String, TemplateImportNameModel> renamingSettings,
                                   final DuplicatesList duplicatesList,
                                   final String email) {
        this.renamingSettings = renamingSettings;
        this.fileName = fileName;
        this.duplicatesList = duplicatesList;
        this.userEmail = email;
    }

    public DuplicatesList getDuplicatesList() {
        return duplicatesList;
    }

    public ResourceTypeEnum getType() {
        return ResourceTypeEnum.STYLESHEET;
    }

    public String getEmail() {
        return userEmail;
    }

    public String getFileName() {
        return fileName;
    }

    public Map<String, TemplateImportNameModel> getRenamingSettings() {
        return renamingSettings;
    }
}

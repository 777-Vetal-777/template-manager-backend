package com.itextpdf.dito.manager.exception.template;

import com.itextpdf.dito.manager.model.template.duplicates.DuplicatesList;
import com.itextpdf.dito.manager.dto.template.setting.TemplateDuplicateNameDTO;

import java.util.List;

public class TemplateImportHasDuplicateNamesException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final transient List<TemplateDuplicateNameDTO> duplicates;

    public TemplateImportHasDuplicateNamesException(final String message, final DuplicatesList duplicates) {
        super(message);
        this.duplicates = duplicates.getDuplicates();
    }

    public List<TemplateDuplicateNameDTO> getDuplicates() {
        return duplicates;
    }
}

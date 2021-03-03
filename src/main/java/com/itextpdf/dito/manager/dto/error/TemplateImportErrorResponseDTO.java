package com.itextpdf.dito.manager.dto.error;

import com.itextpdf.dito.manager.dto.template.setting.TemplateDuplicateNameDTO;

import java.util.List;

public class TemplateImportErrorResponseDTO extends ErrorResponseDTO {

    private final List<TemplateDuplicateNameDTO> duplicates;

    public TemplateImportErrorResponseDTO(String message, final List<TemplateDuplicateNameDTO> duplicates) {
        super(message);
        this.duplicates = duplicates;
    }

    public List<TemplateDuplicateNameDTO> getDuplicates() {
        return duplicates;
    }
}

package com.itextpdf.dito.manager.dto.template.list;

import com.itextpdf.dito.manager.dto.template.TemplateDTO;

import java.util.List;

public class TemplateListResponseDTO {
    private List<TemplateDTO> templates;

    public TemplateListResponseDTO() {

    }

    public TemplateListResponseDTO(List<TemplateDTO> templates) {
        this.templates = templates;
    }

    public List<TemplateDTO> getTemplates() {
        return templates;
    }

    public void setTemplates(List<TemplateDTO> templates) {
        this.templates = templates;
    }
}

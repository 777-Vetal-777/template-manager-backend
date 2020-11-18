package com.itextpdf.dito.manager.dto.template.type;

import java.util.List;

public class TemplateTypeListResponseDTO {
    private List<String> types;

    public TemplateTypeListResponseDTO() {

    }

    public TemplateTypeListResponseDTO(List<String> types) {
        this.types = types;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }
}

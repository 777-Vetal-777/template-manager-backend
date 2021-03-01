package com.itextpdf.dito.manager.dto.template.create;

import com.itextpdf.dito.manager.entity.TemplateTypeEnum;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

public class TemplateCreateRequestDTO {
    @NotBlank
    @Schema(example = "My-template")
    private String name;
    @NotNull
    @Schema(example = "STANDARD")
    private TemplateTypeEnum type;
    @Schema(example = "some-data-collection")
    private String dataCollectionName;

    private List<TemplatePartDTO> templateParts;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TemplateTypeEnum getType() {
        return type;
    }

    public void setType(TemplateTypeEnum type) {
        this.type = type;
    }

    public String getDataCollectionName() {
        return dataCollectionName;
    }

    public void setDataCollectionName(String dataCollectionName) {
        this.dataCollectionName = dataCollectionName;
    }

    public List<TemplatePartDTO> getTemplateParts() {
        return templateParts;
    }

    public void setTemplateParts(List<TemplatePartDTO> templateParts) {
        this.templateParts = templateParts;
    }

    @Override
    public String toString() {
        return "TemplateCreateRequestDTO{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", dataCollectionName='" + dataCollectionName + '\'' +
                ", templateParts=" + templateParts +
                '}';
    }
}

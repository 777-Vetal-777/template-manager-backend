package com.itextpdf.dito.manager.dto.datasample.create;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;

public class DataSampleCreateRequestDTO {
    @NotBlank
    @Schema(example = "My-template")
    private String name;
    @NotBlank
    @Schema(example = "My-template-file")
    private String fileName;
    @NotBlank
    @Schema(example = "{data collection JSON object}")
    private String sample;
    @Schema(example = "some-data-collection coment")
    private String comment;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSample() {
        return sample;
    }

    public void setSample(String sample) {
        this.sample = sample;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}

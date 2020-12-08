package com.itextpdf.dito.manager.dto.workspace;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;

public class WorkspaceDTO {
    @NotBlank
    @Schema(example = "My-workspace", hidden = true)
    private String name;
    @NotBlank
    @Schema(example = "America/Sao_Paulo")
    private String timezone;
    @NotBlank
    @Schema(example = "ENG")
    private String language;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}

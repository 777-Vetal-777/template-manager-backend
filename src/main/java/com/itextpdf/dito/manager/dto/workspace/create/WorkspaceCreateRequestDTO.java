package com.itextpdf.dito.manager.dto.workspace.create;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;

public class WorkspaceCreateRequestDTO {
    @NotBlank
    @Schema(example = "My-workspace")
    String name;
    @NotBlank
    @Schema(example = "America/Sao_Paulo")
    String timezone;
    @NotBlank
    @Schema(example = "ENG")
    String language;
    @NotBlank
    @Schema(example = "main-dev-instance")
    String mainDevelopmentInstanceName;

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

    public String getMainDevelopmentInstanceName() {
        return mainDevelopmentInstanceName;
    }

    public void setMainDevelopmentInstanceName(String mainDevelopmentInstanceName) {
        this.mainDevelopmentInstanceName = mainDevelopmentInstanceName;
    }
}

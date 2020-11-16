package com.itextpdf.dito.manager.dto.workspace.create;

import javax.validation.constraints.NotBlank;

public class WorkspaceCreateRequestDTO {
    @NotBlank
    String name;
    @NotBlank
    String timezone;
    @NotBlank
    String language;

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

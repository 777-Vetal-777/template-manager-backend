package com.itextpdf.dito.manager.dto.template.setting;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TemplateImportNameModel {
    private String name;
    @JsonProperty("new_version")
    private Boolean allowedNewVersion;

    public Boolean getAllowedNewVersion() {
        return allowedNewVersion;
    }

    public void setAllowedNewVersion(Boolean allowedNewVersion) {
        this.allowedNewVersion = allowedNewVersion;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}

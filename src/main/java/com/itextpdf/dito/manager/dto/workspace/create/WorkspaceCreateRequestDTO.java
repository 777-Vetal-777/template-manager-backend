package com.itextpdf.dito.manager.dto.workspace.create;

import com.itextpdf.dito.manager.dto.instance.create.InstanceRememberRequestDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

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
    boolean adjustForDaylight;
    @NotBlank
    String mainDevelopInstance;
    @NotEmpty
    private List<@Valid InstanceRememberRequestDTO> instances;

    public Boolean getAdjustForDaylight() {
        return adjustForDaylight;
    }

    public void setAdjustForDaylight(Boolean adjustForDaylight) {
        this.adjustForDaylight = adjustForDaylight;
    }

    public String getMainDevelopInstance() {
        return mainDevelopInstance;
    }

    public void setMainDevelopInstance(String mainDevelopInstance) {
        this.mainDevelopInstance = mainDevelopInstance;
    }

    public List<InstanceRememberRequestDTO> getInstances() {
        return instances;
    }

    public void setInstances(List<InstanceRememberRequestDTO> instances) {
        this.instances = instances;
    }

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

    @Override
    public String toString() {
        return "WorkspaceCreateRequestDTO{" +
                "name='" + name + '\'' +
                ", timezone='" + timezone + '\'' +
                ", language='" + language + '\'' +
                ", adjustForDaylight=" + adjustForDaylight +
                ", mainDevelopInstance='" + mainDevelopInstance + '\'' +
                ", instances=" + instances +
                '}';
    }
}

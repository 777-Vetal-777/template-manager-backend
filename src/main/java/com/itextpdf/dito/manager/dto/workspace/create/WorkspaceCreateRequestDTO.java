package com.itextpdf.dito.manager.dto.workspace.create;

import javax.validation.constraints.NotBlank;

public class WorkspaceCreateRequestDTO {
    @NotBlank
    String name;
    @NotBlank
    String timezone;
    @NotBlank
    String language;
}

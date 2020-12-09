package com.itextpdf.dito.manager.dto.instance;


import org.springframework.lang.NonNull;
import javax.validation.constraints.NotBlank;

public class InstanceDTO {
    @NotBlank
    private String name;
    @NotBlank
    private String socket;
    @NonNull
    private PromotionPathPositionType pathPositionType;

    public PromotionPathPositionType getPathPositionType() {
        return pathPositionType;
    }

    public void setPathPositionType(PromotionPathPositionType pathPositionType) {
        this.pathPositionType = pathPositionType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSocket() {
        return socket;
    }

    public void setSocket(String socket) {
        this.socket = socket;
    }


}

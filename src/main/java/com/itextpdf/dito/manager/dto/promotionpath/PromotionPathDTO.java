package com.itextpdf.dito.manager.dto.promotionpath;

import com.itextpdf.dito.manager.dto.stage.StageDTO;

import javax.validation.constraints.NotNull;

public class PromotionPathDTO {
    @NotNull
    private StageDTO stage;

    public StageDTO getStage() {
        return stage;
    }

    public void setStage(StageDTO stage) {
        this.stage = stage;
    }
}

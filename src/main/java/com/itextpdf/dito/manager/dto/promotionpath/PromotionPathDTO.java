package com.itextpdf.dito.manager.dto.promotionpath;

import com.itextpdf.dito.manager.dto.stage.StageDTO;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

public class PromotionPathDTO {
    @NotEmpty
    private List<@Valid StageDTO> stages;

    public List<StageDTO> getStages() {
        return stages;
    }

    public void setStages(List<StageDTO> stages) {
        this.stages = stages;
    }

    @Override
    public String toString() {
        return "PromotionPathDTO{" +
                "stages=" + stages +
                '}';
    }
}

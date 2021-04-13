package com.itextpdf.dito.manager.dto.instance;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InstanceSummaryStatusDTO {
    private Integer count;
    @JsonProperty("need_attention")
    private Integer needAttention;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getNeedAttention() {
        return needAttention;
    }

    public void setNeedAttention(Integer needAttention) {
        this.needAttention = needAttention;
    }
}

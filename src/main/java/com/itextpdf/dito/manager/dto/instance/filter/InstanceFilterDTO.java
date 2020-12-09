package com.itextpdf.dito.manager.dto.instance.filter;

import com.itextpdf.dito.manager.dto.instance.PromotionPathPositionType;

import java.sql.Date;

public class InstanceFilterDTO {
    private String name;
    private PromotionPathPositionType pathPositionType;
    private String socket;
    private String createdBy;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PromotionPathPositionType getPathPositionType() {
        return pathPositionType;
    }

    public void setPathPositionType(PromotionPathPositionType pathPositionType) {
        this.pathPositionType = pathPositionType;
    }

    public String getSocket() {
        return socket;
    }

    public void setSocket(String socket) {
        this.socket = socket;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    private Date createdOn;
}

package com.itextpdf.dito.manager.dto.instance;


import com.itextpdf.dito.manager.dto.template.version.TemplateInstanceVersionDTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InstanceDTO {
    private String name;
    private String socket;
    private String createdBy;
    private Date createdOn;
    private String stage;
    private List<TemplateInstanceVersionDTO> templates = new ArrayList<>();

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

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public List<TemplateInstanceVersionDTO> getTemplates() {
        return templates;
    }

    public void setTemplates(List<TemplateInstanceVersionDTO> templates) {
        this.templates = templates;
    }

    @Override
    public String toString() {
        return "InstanceDTO{" +
                "name='" + name + '\'' +
                ", socket='" + socket + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", createdOn=" + createdOn +
                ", stage='" + stage + '\'' +
                ", templates=" + templates +
                '}';
    }

}

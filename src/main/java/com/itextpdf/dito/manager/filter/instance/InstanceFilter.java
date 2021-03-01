package com.itextpdf.dito.manager.filter.instance;

import java.util.List;

public class InstanceFilter {
    private String name;
    private String socket;
    private String createdBy;
    private List<String> stage;
    //Always array with two elements from frontend
    private List<String> createdOn;


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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public List<String> getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(List<String> createdOn) {
        this.createdOn = createdOn;
    }

    public List<String> getStage() {
        return stage;
    }

    public void setStage(List<String> stage) {
        this.stage = stage;
    }

    @Override
    public String toString() {
        return "InstanceFilter{" +
                "name='" + name + '\'' +
                ", socket='" + socket + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", stage=" + stage +
                ", createdOn=" + createdOn +
                '}';
    }
}

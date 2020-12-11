package com.itextpdf.dito.manager.dto.instance;


import java.util.Date;
import javax.validation.constraints.NotBlank;

public class InstanceDTO {
    @NotBlank
    private String name;
    @NotBlank
    private String socket;
    private String createdBy;
    private Date createdOn;

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


}

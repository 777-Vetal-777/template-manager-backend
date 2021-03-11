package com.itextpdf.dito.manager.model.resource;

import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;

import java.util.Date;

public interface ResourceModel {
    Long getId();

    String getResourceName();

    String getComment();

    String getDescription();

    ResourceTypeEnum getType();

    String getAuthorFirstName();

    String getAuthorLastName();

    Date getCreatedOn();

    String getModifiedBy();

    Date getModifiedOn();

    Long getVersion();

    Boolean getDeployed();

}

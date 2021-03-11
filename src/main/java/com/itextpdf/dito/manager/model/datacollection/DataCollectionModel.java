package com.itextpdf.dito.manager.model.datacollection;

import com.itextpdf.dito.manager.dto.datacollection.DataCollectionType;

import java.util.Date;

public interface DataCollectionModel {
    Long getId();

    String getDataName();

    DataCollectionType getType();

    String getModifiedBy();

    Date getModifiedOn();

    Date getCreatedOn();

    String getAuthorFirstName();

    String getAuthorLastName();

    String getDescription();
}

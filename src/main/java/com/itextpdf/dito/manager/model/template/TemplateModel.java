package com.itextpdf.dito.manager.model.template;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.itextpdf.dito.manager.entity.TemplateTypeEnum;

import java.util.Date;

public interface TemplateModel {
    Long getId();

    String getTemplateName();

    String getDataCollection();

    String getAuthor();

    String getCreatedBy();

    String getComment();

    Date getLastUpdate();

    Date getCreatedOn();

    Long getVersion();

    TemplateTypeEnum getType();

}

package com.itextpdf.dito.manager.model.file;

import java.util.Date;

public interface FileVersionModel {
    Long getVersion();

    String getModifiedBy();

    Date getModifiedOn();

    String getComment();

    String getStage();
}

package com.itextpdf.dito.manager.integration.editor.service.data;

import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;

public interface DataCollectionManagementService {
    DataCollectionEntity getByUuid(String id);
}

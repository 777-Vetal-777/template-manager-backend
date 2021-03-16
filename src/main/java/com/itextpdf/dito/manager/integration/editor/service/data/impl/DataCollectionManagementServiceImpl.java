package com.itextpdf.dito.manager.integration.editor.service.data.impl;

import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.integration.editor.service.data.DataCollectionManagementService;
import com.itextpdf.dito.manager.service.datacollection.DataCollectionService;
import org.springframework.stereotype.Service;

@Service
public class DataCollectionManagementServiceImpl implements DataCollectionManagementService {

    private final DataCollectionService dataCollectionService;

    public DataCollectionManagementServiceImpl(DataCollectionService dataCollectionService) {
        this.dataCollectionService = dataCollectionService;
    }

    @Override
    public DataCollectionEntity get(String id) {
        return dataCollectionService.get(id);
    }
}

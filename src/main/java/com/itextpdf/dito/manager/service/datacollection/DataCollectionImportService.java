package com.itextpdf.dito.manager.service.datacollection;

import com.itextpdf.dito.editor.server.common.elements.entity.DataSampleElement;
import com.itextpdf.dito.manager.model.template.duplicates.DuplicatesList;
import com.itextpdf.dito.manager.dto.template.setting.TemplateImportNameModel;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface DataCollectionImportService {

    DataCollectionEntity importDataCollectionAndSamples(String fileName, List<DataSampleElement> dataSamples,
                                                        Map<String, TemplateImportNameModel> dataCollectionSettings,
                                                        DuplicatesList duplicatesList,
                                                        String email) throws IOException;
}

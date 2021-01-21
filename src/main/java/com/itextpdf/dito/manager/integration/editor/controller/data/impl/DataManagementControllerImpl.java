package com.itextpdf.dito.manager.integration.editor.controller.data.impl;

import com.itextpdf.dito.editor.server.common.core.descriptor.DataSampleDescriptor;
import com.itextpdf.dito.manager.integration.editor.controller.data.DataManagementController;

import java.io.InputStream;
import java.util.List;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DataManagementControllerImpl implements DataManagementController {
    @Override
    public DataSampleDescriptor getDataSampleById(String dataSampleId) {
        return null;
    }

    @Override
    public InputStream fetchDataSampleById(String dataSampleId) {
        return null;
    }

    @Override
    public DataSampleDescriptor createOrUpdate(String dataSampleId, DataSampleDescriptor descriptor, InputStream data) {
        return null;
    }

    @Override
    public DataSampleDescriptor add(DataSampleDescriptor descriptor, InputStream data) {
        return null;
    }

    @Override
    public DataSampleDescriptor deleteDataSampleById(String dataSampleId) {
        return null;
    }

    @Override
    public List<DataSampleDescriptor> getDataSamplesByCollectionId(String collectionId) {
        return null;
    }
}

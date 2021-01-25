package com.itextpdf.dito.manager.integration.editor.controller.data;

import com.itextpdf.dito.editor.server.common.core.descriptor.DataSampleDescriptor;
import java.io.InputStream;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

public interface DataManagementController {
    String CREATE_DATA_SAMPLE_URL = "/data";
    String DATA_SAMPLE_URL = "data/{sample-id}";
    String DATA_SAMPLE_DESCRIPTOR_URL = "data/{sample-id}/descriptor";
    String COLLECTION_DATA_SAMPLES_URL = "/collection/{collection-id}";

    @GetMapping(DATA_SAMPLE_DESCRIPTOR_URL)
    DataSampleDescriptor getDataSampleById(String dataSampleId);

    @GetMapping(DATA_SAMPLE_URL)
    InputStream fetchDataSampleById(String dataSampleId);

    @PutMapping(DATA_SAMPLE_URL)
    DataSampleDescriptor createOrUpdate(String dataSampleId, DataSampleDescriptor descriptor, InputStream data);

    @PostMapping(CREATE_DATA_SAMPLE_URL)
    DataSampleDescriptor add(DataSampleDescriptor descriptor, InputStream data);

    @DeleteMapping(DATA_SAMPLE_URL)
    DataSampleDescriptor deleteDataSampleById(String dataSampleId);

    @GetMapping(COLLECTION_DATA_SAMPLES_URL)
    List<DataSampleDescriptor> getDataSamplesByCollectionId(String collectionId);
}

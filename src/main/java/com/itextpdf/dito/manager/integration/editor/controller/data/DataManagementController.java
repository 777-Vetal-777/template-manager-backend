package com.itextpdf.dito.manager.integration.editor.controller.data;

import com.itextpdf.dito.editor.server.common.core.descriptor.DataSampleDescriptor;

import java.io.InputStream;
import java.security.Principal;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestPart;

public interface DataManagementController {
    String CREATE_DATA_SAMPLE_URL = "/data";
    String DATA_SAMPLE_URL = "data/{sample-id}";
    String DATA_SAMPLE_DESCRIPTOR_URL = "data/{sample-id}/descriptor";
    String COLLECTION_DATA_SAMPLES_URL = "/collection/{collection-id}";

    @GetMapping(DATA_SAMPLE_DESCRIPTOR_URL)
    DataSampleDescriptor getDataSampleById(@PathVariable("sample-id") String dataSampleId);

    @GetMapping(DATA_SAMPLE_URL)
    InputStream fetchDataSampleById(@PathVariable("sample-id") String dataSampleId);

    @PutMapping(DATA_SAMPLE_URL)
    DataSampleDescriptor createOrUpdate(Principal principal, @PathVariable("sample-id") String dataSampleId,
            @RequestPart DataSampleDescriptor descriptor, @RequestPart InputStream data);

    @PostMapping(CREATE_DATA_SAMPLE_URL)
    DataSampleDescriptor add(Principal principal, @RequestPart DataSampleDescriptor descriptor,
            @RequestPart InputStream data);

    @DeleteMapping(DATA_SAMPLE_URL)
    DataSampleDescriptor deleteDataSampleById(@PathVariable("sample-id") String dataSampleId);

    @GetMapping(COLLECTION_DATA_SAMPLES_URL)
    List<DataSampleDescriptor> getDataSamplesByCollectionId(@PathVariable("collection-id") String collectionId);
}

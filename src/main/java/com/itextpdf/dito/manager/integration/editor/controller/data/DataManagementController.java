package com.itextpdf.dito.manager.integration.editor.controller.data;

import com.itextpdf.dito.editor.server.common.core.descriptor.DataSampleDescriptor;
import com.itextpdf.dito.manager.config.OpenApiConfig;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestPart;

import java.security.Principal;
import java.util.List;

public interface DataManagementController {
    String CREATE_DATA_SAMPLE_URL = "/data";
    String DATA_SAMPLE_URL = "data/{sample-id}";
    String DATA_SAMPLE_DESCRIPTOR_URL = "data/{sample-id}/descriptor";
    String COLLECTION_DATA_SAMPLES_URL = "/collection/{collection-id}";

    @GetMapping(DATA_SAMPLE_DESCRIPTOR_URL)
    @Operation(security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    DataSampleDescriptor getDataSampleById(@PathVariable("sample-id") String dataSampleId);

    @GetMapping(DATA_SAMPLE_URL)
    @Operation(security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    byte[] fetchDataSampleById(@PathVariable("sample-id") String dataSampleId);

    @PutMapping(DATA_SAMPLE_URL)
    @Operation(security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    DataSampleDescriptor createOrUpdate(Principal principal, @PathVariable("sample-id") String dataSampleId,
                                        @RequestPart(required = false) DataSampleDescriptor descriptor, @RequestPart String data);

    @PostMapping(CREATE_DATA_SAMPLE_URL)
    @Operation(security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    DataSampleDescriptor add(Principal principal, @RequestPart DataSampleDescriptor descriptor,
            @RequestPart String data);

    @DeleteMapping(DATA_SAMPLE_URL)
    @Operation(security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    DataSampleDescriptor deleteDataSampleById(@PathVariable("sample-id") String dataSampleId);

    @GetMapping(COLLECTION_DATA_SAMPLES_URL)
    @Operation(security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    List<DataSampleDescriptor> getDataSamplesByCollectionId(@PathVariable("collection-id") String collectionId);
}

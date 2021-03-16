package com.itextpdf.dito.manager.integration.editor.controller.data;

import com.itextpdf.dito.editor.server.common.core.descriptor.DataSampleDescriptor;
import com.itextpdf.dito.manager.config.OpenApiConfig;

import com.itextpdf.dito.manager.integration.editor.dto.DataCollectionDescriptor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestPart;

import java.security.Principal;
import java.util.List;

@Tag(name = "editor-data", description = "editor integration API")
public interface DataManagementController {
    String CREATE_DATA_SAMPLE_URL = "/data";
    String DATA_SAMPLE_URL = "data/{sample-id}";
    String DATA_SAMPLE_DESCRIPTOR_URL = "data/{sample-id}/descriptor";
    String COLLECTION_DATA_SAMPLES_URL = "/collection/{collection-id}";
    String COLLECTION_DESCRIPTOR_URL = "/collection/{collection-id}/descriptor";

    @GetMapping(value = DATA_SAMPLE_DESCRIPTOR_URL, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "returns JSON descriptor of data sample", summary = "Get a dataSample by its ID ",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DataSampleDescriptor.class)))
    @ApiResponse(responseCode = "404", description = "DataSample not found by id", content = @Content)
    @ApiResponse(responseCode = "400", description = "Bad request, for example, null id is passed", content = @Content)
    DataSampleDescriptor getDataSampleById(@PathVariable("sample-id") String dataSampleId);

    @GetMapping(DATA_SAMPLE_URL)
    @Operation(description = "retrieve data sample content", summary = "Get the contents of a data sample using its personal identifier ", security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE))
    @ApiResponse(responseCode = "404", description = "DataSample not found by id", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    byte[] fetchDataSampleById(@PathVariable("sample-id") String dataSampleId);

    @PutMapping(value = DATA_SAMPLE_URL, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("@permissionHandlerImpl.checkDataCollectionPermissions(#principal.getName(), @permissionHandlerImpl.decodeBase64(#descriptor.getCollectionIdList().get(0)), 'E6_US35_CREATE_A_NEW_VERSION_OF_DATA_COLLECTION_USING_JSON')")
    @Operation(description = "Create a new version of data sample", summary = "updates existing data sample or creates new one, returns data sample descriptor", security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DataSampleDescriptor.class)))
    @ApiResponse(responseCode = "404", description = "DataSample not found by id", content = @Content)
    @ApiResponse(responseCode = "400", description = "Bad request, for example, null id is passed", content = @Content)
    DataSampleDescriptor createNewDataSampleVersion(Principal principal, @PathVariable("sample-id") String dataSampleId,
                                                    @RequestPart(required = false) DataSampleDescriptor descriptor, @RequestPart String data);

    @PostMapping(value = CREATE_DATA_SAMPLE_URL, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("@permissionHandlerImpl.checkDataCollectionPermissions(#principal.getName(), @permissionHandlerImpl.decodeBase64(#descriptor.getCollectionIdList().get(0)), 'E7_US45_CREATE_DATA_SAMPLE_BASED_ON_DATA_COLLECTION_JSON_FILE')")
    @Operation(description = "create new datasample",summary = "Create a new sample date using the specified data. ", security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DataSampleDescriptor.class)))
    @ApiResponse(responseCode = "409", description = "DataSample already exist", content = @Content)
    @ApiResponse(responseCode = "400", description = "Bad request, for example, null id is passed", content = @Content)
    DataSampleDescriptor add(Principal principal, @RequestPart DataSampleDescriptor descriptor,
            @RequestPart String data);

    @DeleteMapping(value = DATA_SAMPLE_URL, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('E7_US50_DELETE_DATA_SAMPLE')")
    @Operation(description = "delete data sample by id", summary = "deletes sample, returns descriptor of deleted sample. ", security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DataSampleDescriptor.class)))
    @ApiResponse(responseCode = "404", description = "DataSample doesn't exist", content = @Content)
    @ApiResponse(responseCode = "400", description = "Bad request, for example, null id is passed", content = @Content)
    DataSampleDescriptor deleteDataSampleById(@PathVariable("sample-id") String dataSampleId);

    @GetMapping(value = COLLECTION_DATA_SAMPLES_URL, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Get data sample by datacollection id.", summary = "Get a list of data samples that are in a datacollection using the datacollection identifier ", security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = DataSampleDescriptor.class))))
    @ApiResponse(responseCode = "404", description = "DataSample doesn't exist", content = @Content)
    @ApiResponse(responseCode = "400", description = "Bad request, for example, null id is passed", content = @Content)
    List<DataSampleDescriptor> getDataSamplesByCollectionId(@PathVariable("collection-id") String collectionId);

    @GetMapping(value = COLLECTION_DESCRIPTOR_URL, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "returns JSON descriptor of data collection", summary = "Get a dataCollection by its ID ",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DataCollectionDescriptor.class)))
    @ApiResponse(responseCode = "404", description = "Data Collection not found by id", content = @Content)
    @ApiResponse(responseCode = "400", description = "Bad request, for example, null id is passed", content = @Content)
    DataCollectionDescriptor getDataCollectionById(@PathVariable("collection-id") String collectionId);

}

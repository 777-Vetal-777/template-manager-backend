package com.itextpdf.dito.manager.controller.datacollection;

import com.itextpdf.dito.manager.config.OpenApiConfig;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionDTO;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionType;
import com.itextpdf.dito.manager.dto.datacollection.update.DataCollectionUpdateRequestDTO;
import com.itextpdf.dito.manager.dto.datasample.DataSampleDTO;
import com.itextpdf.dito.manager.dto.datasample.create.DataSampleCreateRequestDTO;
import com.itextpdf.dito.manager.dto.datasample.update.DataSampleUpdateRequestDTO;
import com.itextpdf.dito.manager.dto.dependency.DependencyDTO;
import com.itextpdf.dito.manager.dto.dependency.filter.DependencyFilter;
import com.itextpdf.dito.manager.dto.file.FileVersionDTO;
import com.itextpdf.dito.manager.dto.permission.DataCollectionPermissionDTO;
import com.itextpdf.dito.manager.dto.resource.update.ApplyRoleRequestDTO;
import com.itextpdf.dito.manager.filter.datacollection.DataCollectionFilter;
import com.itextpdf.dito.manager.filter.datacollection.DataCollectionPermissionFilter;
import com.itextpdf.dito.manager.filter.datasample.DataSampleFilter;
import com.itextpdf.dito.manager.filter.version.VersionFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

import javax.validation.Valid;

@RequestMapping(DataCollectionController.BASE_NAME)
@Tag(name = "data collection", description = "data collection API")
public interface DataCollectionController {
    String MAJOR_VERSION = "/v1";
    String BASE_NAME = MAJOR_VERSION + "/datacollections";
    String DATA_COLLECTION_PATH_VARIABLE = "data-collection-name";
    String ROLE_PATH_VARIABLE = "role-name";
    String DATA_SAMPLE_PATH_VARIABLE = "data-sample-name";
    String VERSIONS_ENDPOINT = "/versions";
    String DATA_SAMPLE_ENDPOINT = "/datasamples";
    String DATA_SAMPLE_WITH_PATH_VARIABLE = "/{" + DATA_SAMPLE_PATH_VARIABLE + "}";
    String ROLE_ENDPOINT_WITH_PATH_VARIABLE = "/{" + ROLE_PATH_VARIABLE + "}";
    String DATA_COLLECTION_WITH_PATH_VARIABLE = "/{" + DATA_COLLECTION_PATH_VARIABLE + "}";
    String DATA_COLLECTION_DEPENDENCIES_WITH_PATH_VARIABLE = DATA_COLLECTION_WITH_PATH_VARIABLE + "/dependencies";
    String DATA_COLLECTION_DATA_SAMPLES_WITH_PATH_VARIABLE = DATA_COLLECTION_WITH_PATH_VARIABLE + "/datasamples";
    String DATA_COLLECTION_DATA_SAMPLE_WITH_PATH_VARIABLE = DATA_COLLECTION_DATA_SAMPLES_WITH_PATH_VARIABLE + DATA_SAMPLE_WITH_PATH_VARIABLE;
    String DATA_COLLECTION_DATA_SAMPLE_WITH_PATH_VARIABLE_SET_AS_DEFAULT = DATA_COLLECTION_DATA_SAMPLES_WITH_PATH_VARIABLE + DATA_SAMPLE_WITH_PATH_VARIABLE + "/setasdefault";
    String DATA_COLLECTION_DATA_SAMPLES_ALL_WITH_PATH_VARIABLE = DATA_COLLECTION_DATA_SAMPLES_WITH_PATH_VARIABLE + "/all";
    String DATA_COLLECTION_DEPENDENCIES_WITH_PATH_VARIABLE_PAGEABLE = DATA_COLLECTION_DEPENDENCIES_WITH_PATH_VARIABLE + "/pageable";
    String RESOURCE_APPLIED_ROLES_ENDPOINT = "/roles";
    String DATA_COLLECTION_VERSIONS_ENDPOINT_WITH_PATH_VARIABLE = DATA_COLLECTION_WITH_PATH_VARIABLE + VERSIONS_ENDPOINT;
    String DATA_COLLECTION_APPLIED_ROLES_ENDPOINT_WITH_DATA_COLLECTION_PATH_VARIABLE = DATA_COLLECTION_WITH_PATH_VARIABLE + RESOURCE_APPLIED_ROLES_ENDPOINT;
    String DATA_COLLECTION_APPLIED_ROLES_ENDPOINT_WITH_DATA_COLLECTION_AND_ROLE_PATH_VARIABLES = DATA_COLLECTION_APPLIED_ROLES_ENDPOINT_WITH_DATA_COLLECTION_PATH_VARIABLE + ROLE_ENDPOINT_WITH_PATH_VARIABLE;
    String DATA_SAMPLE_VERSIONS_WITH_PATH_VARIABLE = DATA_COLLECTION_WITH_PATH_VARIABLE + DATA_SAMPLE_ENDPOINT + VERSIONS_ENDPOINT;
    String DATA_SAMPLES_VERSIONS_WITH_PATH_VARIABLE = DATA_COLLECTION_WITH_PATH_VARIABLE + DATA_SAMPLE_ENDPOINT + DATA_SAMPLE_WITH_PATH_VARIABLE + VERSIONS_ENDPOINT;
    
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create data collection",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<DataCollectionDTO> create(@Parameter(description = "The datacollections name.", required = true, style = ParameterStyle.FORM) @RequestPart String name,
                                             @Parameter(description = "The datacollections type, ex. JSON..", required = true, style = ParameterStyle.FORM, schema = @Schema(implementation = DataCollectionType.class)) @RequestPart("type") String dataCollectionType,
                                             @Parameter(description = "Data collections file", required = true, style = ParameterStyle.FORM) @RequestPart("attachment") MultipartFile multipartFile, Principal principal);

    @GetMapping(DATA_COLLECTION_DEPENDENCIES_WITH_PATH_VARIABLE)
    @Operation(summary = "Get a list of dependencies of one data collection", security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponse(responseCode = "200", description = "Information about one data collection dependencies is prepared according to the specified conditions.")
    ResponseEntity<List<DependencyDTO>> list(@Parameter(name = "name", description = "Encoded with base64 data collection name", required = true) @PathVariable(DATA_COLLECTION_PATH_VARIABLE) String name);

    @PostMapping(path = VERSIONS_ENDPOINT, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create new version of data collection", description = "Make a new version of a data collection: upload a new json and a comment for the new version.",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data collection version is created"),
            @ApiResponse(responseCode = "400", description = "This file exceeds the file limit."),
            @ApiResponse(responseCode = "400", description = "This file is not valid. Please try again"),
            @ApiResponse(responseCode = "403", description = "You don't have permissions to create new version."),
    })
    ResponseEntity<DataCollectionDTO> create(Principal principal,
                                       @Parameter(description = "The data collection name.", required = true, style = ParameterStyle.FORM) @RequestPart String name,
                                       @Parameter(description = "The data collection type, ex. JSON..", required = true, style = ParameterStyle.FORM, schema = @Schema(implementation = DataCollectionType.class)) @RequestPart("type") String dataCollectionType,
                                       @Parameter(description = "Data collections file", required = true, style = ParameterStyle.FORM) @RequestPart("attachment") MultipartFile multipartFile,
                                       @Parameter(description = "Optional comment to the new data collection version", name = "comment", style = ParameterStyle.FORM) @RequestPart(required = false) String comment);

    @GetMapping
    @Operation(summary = "Get list of data collections",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<Page<DataCollectionDTO>> list(Pageable pageable, @ParameterObject DataCollectionFilter filter,
                                                 @RequestParam(name = "search", required = false) String searchParam);

    @GetMapping(DATA_COLLECTION_WITH_PATH_VARIABLE)
    @Operation(summary = "Get data collection", description = "Get data collection",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<DataCollectionDTO> get(@Parameter(description = "Data collections name encoded with base64.") @PathVariable(DATA_COLLECTION_PATH_VARIABLE) String name);

    @PatchMapping(DATA_COLLECTION_WITH_PATH_VARIABLE)
    @Operation(summary = "Update data collection", description = "Update data collection",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<DataCollectionDTO> update(@Parameter(description = "Data collections name encoded with base64.") @PathVariable(DATA_COLLECTION_PATH_VARIABLE) String name,
                                             @RequestBody DataCollectionUpdateRequestDTO dataCollectionUpdateRequestDTO,
                                             Principal principal);

    @DeleteMapping(DATA_COLLECTION_WITH_PATH_VARIABLE)
    @Operation(summary = "Delete data collection", description = "Delete data collection",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data collection is deleted", content = @Content),
            @ApiResponse(responseCode = "409", description = "Data collection has dependencies and cannot be removed", content = @Content),
            @ApiResponse(responseCode = "404", description = "Data collection not found", content = @Content),
    })
    ResponseEntity<Void> delete(@Parameter(description = "Data collections name encoded with base64.") @PathVariable(name = DATA_COLLECTION_PATH_VARIABLE) String name, Principal principal);

    @GetMapping(DATA_COLLECTION_VERSIONS_ENDPOINT_WITH_PATH_VARIABLE)
    @Operation(summary = "Get a list of versions of data collection by name", description = "Get a list of data collection versions using the data collection name, sorting and filters.",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<Page<FileVersionDTO>> getVersions(Pageable pageable,
                                                     @Parameter(description = "Data collections name encoded with base64.") @PathVariable(DATA_COLLECTION_PATH_VARIABLE) String name,
                                                     @ParameterObject VersionFilter versionFilter,
                                                     @Parameter(description = "Universal search string.") @RequestParam(name = "search", required = false) String searchParam);

    @GetMapping(DATA_COLLECTION_APPLIED_ROLES_ENDPOINT_WITH_DATA_COLLECTION_PATH_VARIABLE)
    @Operation(summary = "Get resource's roles", description = "Retrieved attached roles.", security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<Page<DataCollectionPermissionDTO>> getRoles(Pageable pageable,
                                                               @Parameter(name = "data_collection-name", description = "Encoded with base 64 name of dataCollection") @PathVariable(DATA_COLLECTION_PATH_VARIABLE) String name,
                                                               @ParameterObject DataCollectionPermissionFilter filter,
                                                               @Parameter(description = "Universal search string.") @RequestParam(name = "search", required = false) String searchParam);

    @PostMapping(DATA_COLLECTION_APPLIED_ROLES_ENDPOINT_WITH_DATA_COLLECTION_PATH_VARIABLE)
    @Operation(summary = "Add role to a dataCollection", description = "Apply custom to a dataCollection", security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<DataCollectionDTO> applyRole(
            @Parameter(name = "data-collection-name", description = "Encoded with base64 new name of dataCollection", required = true) @PathVariable(DATA_COLLECTION_PATH_VARIABLE) String name,
            @RequestBody ApplyRoleRequestDTO applyRoleRequestDTO);

    @DeleteMapping(DATA_COLLECTION_APPLIED_ROLES_ENDPOINT_WITH_DATA_COLLECTION_AND_ROLE_PATH_VARIABLES)
    @Operation(summary = "Remove role from a dataCollection", description = "Detach custom from a resource", security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<DataCollectionDTO> deleteRole(
            @Parameter(name = "data-collection-name", description = "Encoded with base64 new name of dataCollection", required = true) @PathVariable(DATA_COLLECTION_PATH_VARIABLE) String name,
            @Parameter(name = "role-name", description = "Encoded with base64 role name", required = true) @PathVariable(ROLE_PATH_VARIABLE) String roleName);

    @GetMapping(DATA_COLLECTION_DEPENDENCIES_WITH_PATH_VARIABLE_PAGEABLE)
    @Operation(summary = "Get list of data collection dependencies",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<Page<DependencyDTO>> listDependencies(Pageable pageable,
                                                         @Parameter(description = "Data collections name encoded with base64.") @PathVariable("data-collection-name") String name,
                                                         @ParameterObject DependencyFilter filter,
                                                         @RequestParam(name = "search", required = false) String searchParam);

	@PostMapping(DATA_COLLECTION_DATA_SAMPLES_WITH_PATH_VARIABLE)
	@Operation(summary = "Create data sample", description = "Create new data sample", security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Success! File is uploaded", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = DataSampleDTO.class)) }),
			@ApiResponse(responseCode = "409", description = "Invalid input or datasample already exists", content = @Content),

            @ApiResponse(responseCode = "400", description = "Invalid data sample structure", content = @Content)})
    ResponseEntity<DataSampleDTO> create(
            @Parameter(description = "Data collections name encoded with base64.", required = true) @PathVariable(DATA_COLLECTION_PATH_VARIABLE) String dataCollectionName ,
            @RequestBody DataSampleCreateRequestDTO dataSampleCreateRequestDTO, Principal principal);

	@GetMapping(DATA_COLLECTION_DATA_SAMPLES_WITH_PATH_VARIABLE)
	@Operation(summary = "Get list of data samples", security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
	ResponseEntity<Page<DataSampleDTO>> list(
			@Parameter(description = "Base64-encoded name of the data collection", required = true) @PathVariable(DATA_COLLECTION_PATH_VARIABLE) String dataCollectionName,
			Pageable pageable, @ParameterObject DataSampleFilter filter,
			@RequestParam(name = "search", required = false) String searchParam);

	@GetMapping(DATA_COLLECTION_DATA_SAMPLE_WITH_PATH_VARIABLE)
	@Operation(summary = "Get data sample by name", description = "Get data sample by name", security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
	ResponseEntity<DataSampleDTO> getDataSample(
			@Parameter(description = "Base64-encoded name of the data collection", required = true) @PathVariable(DATA_COLLECTION_PATH_VARIABLE) String dataCollectionName,
			@Parameter(description = "Data sample name encoded with base64.") @PathVariable(DATA_SAMPLE_PATH_VARIABLE) String dataSampleName);

	@DeleteMapping(DATA_COLLECTION_DATA_SAMPLES_WITH_PATH_VARIABLE)
	@Operation(summary = "Delete list of data samples", description = "Delete list of data samples", security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Data samples is deleted", content = @Content),
			@ApiResponse(responseCode = "409", description = "Some data samples has dependencies and cannot be removed", content = @Content),
			@ApiResponse(responseCode = "404", description = "Some data samples not found", content = @Content) 
	})
	ResponseEntity<Void> deleteDataSampleList(
			@Parameter(description = "Data collections name encoded with base64.") @PathVariable(DATA_COLLECTION_PATH_VARIABLE) String dataCollectionName,
			@RequestBody List<String> dataSampleNames, Principal principal);
	
	@DeleteMapping(DATA_COLLECTION_DATA_SAMPLES_ALL_WITH_PATH_VARIABLE)
	@Operation(summary = "Delete all data samples of data collection", description = "Delete all data samples of data collection", security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Data samples is deleted", content = @Content),
			@ApiResponse(responseCode = "409", description = "Data samples has dependencies and cannot be removed", content = @Content),
			@ApiResponse(responseCode = "404", description = "Data samples not found", content = @Content) 
	})
	ResponseEntity<Void> deleteAllDataSamples(
			@Parameter(description = "Data collections name encoded with base64.") @PathVariable(DATA_COLLECTION_PATH_VARIABLE) String dataCollectionName,
			 Principal principal);
	
	@PutMapping(DATA_COLLECTION_DATA_SAMPLE_WITH_PATH_VARIABLE_SET_AS_DEFAULT)
	@Operation(summary = "Update data sample, set as default", description = "Update data sample, set data sample as default for data collection", security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Data sample updated successfully", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = DataSampleDTO.class)) }),
			@ApiResponse(responseCode = "404", description = "Data sample not found", content = @Content) })
	ResponseEntity<DataSampleDTO> setDataSampleAsDefault(
			@Parameter(description = "Base64-encoded name of the data collection", required = true) @PathVariable(DATA_COLLECTION_PATH_VARIABLE) String dataCollectionName,
			@Parameter(description = "Data sample name encoded with base64.", required = true) @PathVariable(DATA_SAMPLE_PATH_VARIABLE) String dataSampleName,
			Principal principal);

	@PatchMapping(DATA_COLLECTION_DATA_SAMPLE_WITH_PATH_VARIABLE)
	@Operation(summary = "Update data sample", description = "Update data sample", security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
	ResponseEntity<DataSampleDTO> updateDataSample(
			@Parameter(description = "Data collections name encoded with base64.") @PathVariable(DATA_COLLECTION_PATH_VARIABLE) String name,
			@Parameter(description = "Data sample name encoded with base64.") @PathVariable(DATA_SAMPLE_PATH_VARIABLE) String dataSampleName,
			@RequestBody DataSampleUpdateRequestDTO dataSampleUpdateRequestDTO, Principal principal);
	
	@PostMapping(DATA_SAMPLE_VERSIONS_WITH_PATH_VARIABLE)
	@Operation(summary = "Create new version data sample", description = "Create new version data sample", security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Success! File is uploaded", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = DataSampleDTO.class)) }),
			@ApiResponse(responseCode = "409", description = "Invalid input or datasample already exists", content = @Content),

			@ApiResponse(responseCode = "400", description = "Invalid data sample structure", content = @Content) })
	ResponseEntity<DataSampleDTO> createDataSampleNewVersion(
			@Parameter(description = "Data collections name encoded with base64.", required = true) @PathVariable(DATA_COLLECTION_PATH_VARIABLE) String dataCollectionName,
			@RequestBody DataSampleCreateRequestDTO dataSampleCreateRequestDTO, Principal principal);

	@GetMapping(DATA_SAMPLES_VERSIONS_WITH_PATH_VARIABLE)
	@Operation(summary = "Get a list of versions of data sample by name", description = "Get a list of data sample versions using the data sample name, sorting and filters.", security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
	ResponseEntity<Page<FileVersionDTO>> getDataSampleVersions(Pageable pageable,
			@Parameter(description = "Data collections name encoded with base64.", required = true) @PathVariable(DATA_COLLECTION_PATH_VARIABLE) String dataCollectionName,
			@PathVariable(DATA_SAMPLE_PATH_VARIABLE) String name, VersionFilter versionFilter,
			@RequestParam(value = "search", required = false) String searchParam);

}

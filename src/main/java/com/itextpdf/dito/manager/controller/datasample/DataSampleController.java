package com.itextpdf.dito.manager.controller.datasample;

import com.itextpdf.dito.manager.config.OpenApiConfig;
import com.itextpdf.dito.manager.dto.datasample.DataSampleDTO;
import com.itextpdf.dito.manager.dto.datasample.create.DataSampleCreateRequestDTO;
import com.itextpdf.dito.manager.filter.datasample.DataSampleFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@RequestMapping(DataSampleController.BASE_NAME)
@Tag(name = "data sample", description = "data sample API")
public interface DataSampleController {
    String MAJOR_VERSION = "/v1";
    String BASE_NAME = MAJOR_VERSION + "/datasamples";

    String DATA_SAMPLE_PATH_VARIABLE = "data-sample-name";
    String DATA_SAMPLE_WITH_PATH_VARIABLE = "/{" + DATA_SAMPLE_PATH_VARIABLE + "}";

    @PostMapping
    @Operation(summary = "Create data sample", description = "Create new data sample",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success! File is uploaded", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = DataSampleDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input or template already exists", content = @Content)})
    ResponseEntity<DataSampleDTO> create(@RequestBody DataSampleCreateRequestDTO templateCreateRequestDTO,
                                             Principal principal);

    @GetMapping
    @Operation(summary = "Get list of data samples",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<Page<DataSampleDTO>> list(Pageable pageable, @ParameterObject DataSampleFilter filter,
                                                 @RequestParam(name = "search", required = false) String searchParam);

    @GetMapping(DATA_SAMPLE_WITH_PATH_VARIABLE)
    @Operation(summary = "Get data sample by name", description = "Get data sample by name",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<DataSampleDTO> get(@Parameter(description = "Data sample name encoded with base64.") @PathVariable(DATA_SAMPLE_WITH_PATH_VARIABLE) String name);

}

package com.itextpdf.dito.manager.controller.datacollection;

import com.itextpdf.dito.manager.config.OpenApiConfig;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionDTO;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionType;
import com.itextpdf.dito.manager.dto.datacollection.update.DataCollectionUpdateRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RequestMapping(DataCollectionController.BASE_NAME)
@Tag(name = "data collection", description = "data collection API")
public interface DataCollectionController {
    String MAJOR_VERSION = "/v1";
    String BASE_NAME = MAJOR_VERSION + "/datacollections";
    String DATA_COLLECTION_PATH_VARIABLE = "name";
    String DATA_COLLECTION_WITH_PATH_VARIABLE = "/{" + DATA_COLLECTION_PATH_VARIABLE + "}";

    @PostMapping (consumes =  MediaType.MULTIPART_FORM_DATA_VALUE )
    @Operation(summary = "Create data collection",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<DataCollectionDTO> create(@Parameter(description = "The datacollections name.",required = true,style = ParameterStyle.FORM) @RequestParam String name,
                                             @Parameter(description = "The datacollections type, ex. JSON..",required = true, style = ParameterStyle.FORM)@RequestParam("type") DataCollectionType dataCollectionType,
                                             @Parameter(description = "Data collections file",required = true,style = ParameterStyle.FORM)@RequestPart("attachment") MultipartFile multipartFile,
                                             Principal principal);

    @GetMapping
    @Operation(summary = "Get list of data collections",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<Page<DataCollectionDTO>> list(Pageable pageable,
            @RequestParam(name = "search", required = false) String searchParam);

    @GetMapping(DATA_COLLECTION_WITH_PATH_VARIABLE)
    @Operation(summary = "Get data collection", description = "Get data collection",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<DataCollectionDTO> get(@Parameter(description = "Data collections name encoded with base64.") @PathVariable("name") String name);

    @PatchMapping(DATA_COLLECTION_WITH_PATH_VARIABLE)
    @Operation(summary = "Update data collection", description = "Update data collection",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    ResponseEntity<DataCollectionDTO> update(@Parameter(description = "Data collections name encoded with base64.") @PathVariable("name") String name,
                                             @RequestBody DataCollectionUpdateRequestDTO dataCollectionUpdateRequestDTO,
                                             Principal principal);

    @DeleteMapping(DATA_COLLECTION_WITH_PATH_VARIABLE)
    @Operation(summary = "Delete data collection", description = "Delete data collection",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted data collection", content = @Content),
            @ApiResponse(responseCode = "409", description = "File with this name already exists", content = @Content),
            @ApiResponse(responseCode = "404", description = "Data collection not found", content = @Content),
    })
    ResponseEntity<Void> delete(@Parameter(description = "Data collections name encoded with base64.") @PathVariable("name") String name);
}

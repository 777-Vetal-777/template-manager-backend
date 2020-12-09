package com.itextpdf.dito.manager.controller.instance;

import com.itextpdf.dito.manager.config.OpenApiConfig;
import com.itextpdf.dito.manager.dto.instance.InstanceDTO;
import com.itextpdf.dito.manager.dto.instance.create.InstanceCreateRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

@RequestMapping(InstanceController.BASE_NAME)
public interface InstanceController {
    String MAJOR_VERSION = "/v1";
    String BASE_NAME = MAJOR_VERSION + "/instances";

    String INSTANCE_ENDPOINT = "/{socket}";
    String INSTANCE_STATUS = INSTANCE_ENDPOINT + "/status";

    @GetMapping(INSTANCE_STATUS)
    @Operation(summary = "Get instance status", description = "Get instance status by socket", security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Instance available, ready to connect"),
            @ApiResponse(responseCode = "504",description = "No connection to the instance")
    })
    ResponseEntity<Void> getInstanceStatus(@Parameter(description = "encoded with base64 socket, with which you can check the status of the instance") @NonNull @PathVariable("socket") String socket);

    @PostMapping
    @Operation(summary = "Save instances", description = "Save a set of instances", security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Instances successfully saved"),
            @ApiResponse(responseCode = "400",description = "Instance already exist")
    })
    ResponseEntity<List<InstanceDTO>> saveInstances(@Valid @RequestBody InstanceCreateRequestDTO createRequestDTO);

}

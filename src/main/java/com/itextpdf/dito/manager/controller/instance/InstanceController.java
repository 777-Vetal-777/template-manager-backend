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
import org.springframework.web.bind.annotation.DeleteMapping;
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

    String INSTANCE_NAME_PATH_VARIABLE = "name";
    String INSTANCE_NAME_ENDPOINT_WITH_PATH_VARIABLE = "/{" + INSTANCE_NAME_PATH_VARIABLE + "}";

    String INSTANCE_SOCKET_PATH_VARIABLE = "socket";
    String INSTANCE_SOCKET_ENDPOINT_WITH_PATH_VARIABLE = "/{" + INSTANCE_SOCKET_PATH_VARIABLE + "}";
    String INSTANCE_STATUS_ENDPOINT = INSTANCE_SOCKET_ENDPOINT_WITH_PATH_VARIABLE + "/status";

    @GetMapping(INSTANCE_STATUS_ENDPOINT)
    @Operation(summary = "Get instance status", description = "Get instance status by socket", security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Instance available, ready to connect"),
            @ApiResponse(responseCode = "504", description = "No connection to the instance")
    })
    ResponseEntity<Void> getInstanceStatus(@Parameter(description = "encoded with base64 socket, with which you can check the status of the instance") @PathVariable("socket") String socket);

    @PostMapping
    @Operation(summary = "Save instances", description = "Save a set of instances", security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Instances successfully saved"),
            @ApiResponse(responseCode = "400", description = "Instance already exist")
    })
    ResponseEntity<List<InstanceDTO>> saveInstances(@Valid @RequestBody InstanceCreateRequestDTO createRequestDTO);

    @DeleteMapping(INSTANCE_NAME_ENDPOINT_WITH_PATH_VARIABLE)
    @Operation(summary = "Disconnect instance", description = "Break communication with an instance.")
    @ApiResponse(responseCode = "200", description = "Instance disconnected successfully.")
    ResponseEntity<Void> deleteInstance(@Parameter(description = "Encoded with base64 instance name, by which the instance will be disconnected.") @PathVariable final String name);
}

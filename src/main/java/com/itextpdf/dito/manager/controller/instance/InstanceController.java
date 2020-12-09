package com.itextpdf.dito.manager.controller.instance;

import com.itextpdf.dito.manager.config.OpenApiConfig;
import com.itextpdf.dito.manager.dto.instance.InstanceDTO;
import com.itextpdf.dito.manager.dto.instance.create.InstanceCreateRequestDTO;
import com.itextpdf.dito.manager.dto.instance.filter.InstanceFilterDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "instance", description = "instance API")
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
    @Operation(summary = "Get instance status",
            description = "Get instance status by socket",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Instance available, ready to connect"),
            @ApiResponse(responseCode = "504", description = "No connection to the instance")
    })
    ResponseEntity<Void> getInstanceStatus(
            @Parameter(description = "encoded with base64 socket, with which you can check the status of the instance")
            @PathVariable(INSTANCE_SOCKET_PATH_VARIABLE) String socket);

    @PostMapping
    @Operation(summary = "Save instances",
            description = "Save a set of instances",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Instances successfully saved"),
            @ApiResponse(responseCode = "400", description = "Instance already exist")
    })
    ResponseEntity<List<InstanceDTO>> saveInstances(@RequestBody InstanceCreateRequestDTO createRequestDTO);

    @DeleteMapping(INSTANCE_NAME_ENDPOINT_WITH_PATH_VARIABLE)
    @Operation(summary = "Disconnect instance", description = "Break communication with an instance.")
    @ApiResponse(responseCode = "200", description = "Instance disconnected successfully.")
    ResponseEntity<Void> deleteInstance(
            @Parameter(description = "Encoded with base64 instance name, by which the instance will be disconnected.")
            @PathVariable(INSTANCE_NAME_PATH_VARIABLE) final String name);

    @GetMapping()
    @Operation(summary = "Get information about instances",
            description = "Retrieving list of information about instances using sorting and filters.",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Information about instances is prepared according to the specified conditions."),
    })
    ResponseEntity<Page<InstanceDTO>> getInstanceStatus(Pageable pageable,
            @ParameterObject InstanceFilterDTO instanceFilterDTO,
            @Parameter(description = "Universal search string which filter instance name, author name  and socket")
            @RequestParam(name = "searchParam", required = false) String searchParam);

}


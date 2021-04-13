package com.itextpdf.dito.manager.controller.instance;

import com.itextpdf.dito.manager.config.OpenApiConfig;
import com.itextpdf.dito.manager.dto.instance.InstanceDTO;
import com.itextpdf.dito.manager.dto.instance.InstanceSummaryStatusDTO;
import com.itextpdf.dito.manager.dto.instance.create.InstanceHeaderRequestDTO;
import com.itextpdf.dito.manager.dto.instance.create.InstancesRememberRequestDTO;
import com.itextpdf.dito.manager.dto.instance.update.InstanceUpdateRequestDTO;
import com.itextpdf.dito.manager.filter.instance.InstanceFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Tag(name = "instance", description = "instance API")
@RequestMapping(InstanceController.BASE_NAME)
public interface InstanceController {

    String MAJOR_VERSION = "/v1";
    String BASE_NAME = MAJOR_VERSION + "/instances";

    String PAGEABLE_ENDPOINT = "/pageable";
    String STATUS_ENDPOINT = "/status";

    String INSTANCE_NAME_PATH_VARIABLE = "name";
    String INSTANCE_NAME_ENDPOINT_WITH_PATH_VARIABLE = "/{" + INSTANCE_NAME_PATH_VARIABLE + "}";

    String INSTANCE_SOCKET_PATH_VARIABLE = "socket";
    String INSTANCE_SOCKET_ENDPOINT_WITH_PATH_VARIABLE = "/{" + INSTANCE_SOCKET_PATH_VARIABLE + "}";
    String INSTANCE_STATUS_ENDPOINT = INSTANCE_SOCKET_ENDPOINT_WITH_PATH_VARIABLE + STATUS_ENDPOINT;

    @GetMapping(INSTANCE_STATUS_ENDPOINT)
    @Operation(summary = "Get instance status",
            description = "Get instance status by socket",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Instance available, ready to connect"),
            @ApiResponse(responseCode = "502", description = "No connection to the instance")
    })
    ResponseEntity<Void> ping(
            @Parameter(description = "encoded with base64 socket, with which you can check the status of the instance") @PathVariable(INSTANCE_SOCKET_PATH_VARIABLE) String socket,
            @Parameter(description = "custom header name and value") @ParameterObject InstanceHeaderRequestDTO headers);

    @PostMapping
    @PreAuthorize("hasAuthority('E5_US28_CONNECT_NEW_INSTANCE')")
    @Operation(summary = "Save instances",
            description = "Save a set of instances",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Instances successfully saved"),
            @ApiResponse(responseCode = "400", description = "Instance already exist")
    })
    ResponseEntity<List<InstanceDTO>> remember(@RequestBody InstancesRememberRequestDTO instancesRememberRequestDTO,
            Principal principal);

    @DeleteMapping(INSTANCE_NAME_ENDPOINT_WITH_PATH_VARIABLE)
    @PreAuthorize("hasAuthority('E5_US29_DISCONNECT_INSTANCE')")
    @Operation(summary = "Disconnect instance", description = "Break communication with an instance.",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponse(responseCode = "200", description = "Instance disconnected successfully.")
    ResponseEntity<Void> forget(
            @Parameter(description = "Encoded with base64 instance name, by which the instance will be disconnected.")
            @PathVariable(INSTANCE_NAME_PATH_VARIABLE) final String name);

    @GetMapping(PAGEABLE_ENDPOINT)
    @PreAuthorize("hasAuthority('E4_US27_SEE_THE_TABLE_OF_INSTANCES')")
    @Operation(summary = "Get information about instances",
            description = "Retrieving list of information about instances using sorting and filters.",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Information about instances is prepared according to the specified conditions."),
    })
    ResponseEntity<Page<InstanceDTO>> getInstances(Pageable pageable,
            @ParameterObject InstanceFilter instanceFilter,
            @Parameter(description = "Universal search string which filter instance name, author name  and socket")
            @RequestParam(name = "search", required = false) String searchParam);

    @GetMapping
    @PreAuthorize("hasAnyAuthority('E4_US27_SEE_THE_TABLE_OF_INSTANCES', 'E2_US6_SETTINGS_PANEL')")
    @Operation(summary = "Get information about instances",
            description = "Retrieving list of information about instances using sorting and filters.",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Information about instances is prepared according to the specified conditions."),
    })
    ResponseEntity<List<InstanceDTO>> getInstances();

    @PatchMapping(INSTANCE_NAME_ENDPOINT_WITH_PATH_VARIABLE)
    @Operation(summary = "Update instance", description = "Update instance's name or socket.",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponse(responseCode = "200", description = "Instance updated successfully.")
    ResponseEntity<InstanceDTO> update(@Parameter(description = "Encoded with base64 instance name") @PathVariable(INSTANCE_NAME_PATH_VARIABLE) final String name,
            @RequestBody InstanceUpdateRequestDTO instanceUpdateRequestDTO);

    @PreAuthorize("hasAnyAuthority('E4_US27_SEE_THE_TABLE_OF_INSTANCES', 'E2_US6_SETTINGS_PANEL')")
    @GetMapping(STATUS_ENDPOINT)
    @Operation(summary = "Get instances status", description = "Get count of instances need attention",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponse(responseCode = "200", description = "Instance status retrieved succesfully")
    ResponseEntity<InstanceSummaryStatusDTO> getSummary();

}


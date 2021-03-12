package com.itextpdf.dito.manager.integration.editor.controller.template;

import com.itextpdf.dito.editor.server.common.core.descriptor.TemplateAddDescriptor;
import com.itextpdf.dito.editor.server.common.core.descriptor.TemplateCommitDescriptor;
import com.itextpdf.dito.editor.server.common.core.descriptor.TemplateDescriptor;
import com.itextpdf.dito.editor.server.common.core.descriptor.TemplateUpdateDescriptor;
import com.itextpdf.dito.manager.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.web.bind.annotation.RequestPart;

import java.security.Principal;
import java.util.List;

@Tag(name = "editor", description = "editor integration API")
public interface TemplateManagementController {
    // Endpoints naming is used from the strict requirements for editor integration.
    String CREATE_TEMPLATE_URL = "/workspace/{workspace-id}/templates";
    String TEMPLATE_LIST_URL = "/workspace/{workspace-id}/templates";
    String TEMPLATE_URL = "/templates/{template-id}";
    String TEMPLATE_DESCRIPTOR_URL = "/templates/{template-id}/descriptor";

    @PostMapping(value = CREATE_TEMPLATE_URL, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create template", description = "creates new template, returns descriptor of template",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponse(responseCode = "200", description = "Template created", content = {
            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = TemplateDescriptor.class))
    })
    @ApiResponse(responseCode = "400", description = "data and/or descriptor have wrong format", content = @Content)
    TemplateDescriptor add(Principal principal,
                           @PathVariable("workspace-id") String workspaceId,
                           @Parameter(description = "descriptor", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = TemplateAddDescriptor.class))) @RequestPart TemplateAddDescriptor descriptor,
                           @Parameter(description = "template content stream", content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)) @RequestPart byte[] data);

    @GetMapping(value = TEMPLATE_URL, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Operation(summary = "Get template content", description = "retrieve template content",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE))
    @ApiResponse(responseCode = "404", description = "Template not found by id", content = @Content)
    @ApiResponse(responseCode = "400", description = "Bad request, for example, null id is passed", content = @Content)
    byte[] get(@PathVariable("template-id") String templateId);

    @GetMapping(value = TEMPLATE_DESCRIPTOR_URL, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Fetch template descriptor", description = "returns JSON descriptor of template",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = TemplateDescriptor.class)))
    @ApiResponse(responseCode = "404", description = "Template not found by id", content = @Content)
    @ApiResponse(responseCode = "400", description = "Bad request, for example, null id is passed", content = @Content)
    TemplateDescriptor getDescriptor(@PathVariable("template-id") String templateId);

    @GetMapping(value = TEMPLATE_LIST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Fetch template descriptor list", description = "returns JSON list of template descriptors",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = TemplateDescriptor.class))))
    @ApiResponse(responseCode = "400", description = "Bad request, for example, null workspace id is passed", content = @Content)
    List<TemplateDescriptor> getAllDescriptors(@PathVariable("workspace-id") String workspaceId);

    @PostMapping(value = TEMPLATE_URL, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "update template", description = "updates existing template, returns descriptor of template",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = TemplateDescriptor.class)))
    @ApiResponse(responseCode = "404", description = "Template not found by id", content = @Content)
    @ApiResponse(responseCode = "400", description = "Bad request, for example, null id is passed", content = @Content)
    TemplateDescriptor update(Principal principal,
                              @PathVariable("template-id") String templateId,
                              @Parameter(description = "update descriptor", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = TemplateUpdateDescriptor.class))) @RequestPart(required = false) TemplateUpdateDescriptor descriptor,
                              @Parameter(description = "commit descriptor", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = TemplateCommitDescriptor.class))) @RequestPart(required = false) TemplateCommitDescriptor commitDescriptor,
                              @RequestPart byte[] data);

    @PreAuthorize("@permissionHandlerImpl.checkTemplateDeletePermissions(authentication, @permissionHandlerImpl.decodeBase64(#templateId))")
    @DeleteMapping(value = TEMPLATE_URL, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "delete template", description = "deletes template, returns descriptor of deleted template",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH_SECURITY_SCHEME_NAME))
    @ApiResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = TemplateDescriptor.class)))
    @ApiResponse(responseCode = "404", description = "Template not found by id", content = @Content)
    @ApiResponse(responseCode = "400", description = "Bad request, for example, null id is passed", content = @Content)
    TemplateDescriptor delete(@PathVariable("template-id") String templateId);
}

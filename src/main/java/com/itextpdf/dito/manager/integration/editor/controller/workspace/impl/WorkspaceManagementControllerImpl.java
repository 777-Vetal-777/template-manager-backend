package com.itextpdf.dito.manager.integration.editor.controller.workspace.impl;

import com.itextpdf.dito.editor.server.common.core.descriptor.WorkspaceInfoDescriptor;
import com.itextpdf.dito.manager.controller.AbstractController;
import com.itextpdf.dito.manager.entity.WorkspaceEntity;
import com.itextpdf.dito.manager.integration.editor.controller.workspace.WorkspaceManagementController;
import com.itextpdf.dito.manager.integration.editor.mapper.workspace.WorkspaceInfoDescriptorMapper;
import com.itextpdf.dito.manager.service.workspace.WorkspaceService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WorkspaceManagementControllerImpl extends AbstractController implements WorkspaceManagementController {
    private static final Logger log = LogManager.getLogger(WorkspaceManagementControllerImpl.class);

    private final WorkspaceService workspaceService;
    private final WorkspaceInfoDescriptorMapper workspaceInfoDescriptorMapper;

    public WorkspaceManagementControllerImpl(final WorkspaceService workspaceService,
                                             final WorkspaceInfoDescriptorMapper workspaceInfoDescriptorMapper) {
        this.workspaceService = workspaceService;
        this.workspaceInfoDescriptorMapper = workspaceInfoDescriptorMapper;
    }

    @Override
    public WorkspaceInfoDescriptor fetch(final String workspaceId) {
        log.info("Request to get workspace descriptor by workspace id {}.", workspaceId);
        final WorkspaceEntity entity = workspaceService.getAll().get(0);
        //TODO: uncomment the strings below after fix the integration between editor and front-end
        //final String decodedWorkspaceId = decodeBase64(workspaceId);
        //final WorkspaceEntity entity = workspaceService.get(decodedWorkspaceId);
        final WorkspaceInfoDescriptor workspaceInfoDescriptor = workspaceInfoDescriptorMapper.map(entity);
        log.info("Request to get workspace descriptor by workspace id {} finished successfully.", workspaceId);
        return workspaceInfoDescriptor;
    }
}

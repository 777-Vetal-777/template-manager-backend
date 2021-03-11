package com.itextpdf.dito.manager.integration.editor.controller.workspace.impl;

import com.itextpdf.dito.editor.server.common.core.descriptor.WorkspaceInfoDescriptor;
import com.itextpdf.dito.manager.controller.AbstractController;
import com.itextpdf.dito.manager.entity.WorkspaceEntity;
import com.itextpdf.dito.manager.integration.editor.controller.workspace.WorkspaceManagementController;
import com.itextpdf.dito.manager.integration.editor.mapper.workspace.WorkspaceInfoDescriptorMapper;
import com.itextpdf.dito.manager.service.workspace.WorkspaceService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WorkspaceManagementControllerImpl extends AbstractController implements WorkspaceManagementController {

    private final WorkspaceService workspaceService;
    private final WorkspaceInfoDescriptorMapper workspaceInfoDescriptorMapper;

    public WorkspaceManagementControllerImpl(final WorkspaceService workspaceService,
                                             final WorkspaceInfoDescriptorMapper workspaceInfoDescriptorMapper) {
        this.workspaceService = workspaceService;
        this.workspaceInfoDescriptorMapper = workspaceInfoDescriptorMapper;
    }

    @Override
    public WorkspaceInfoDescriptor fetch(final String workspaceId) {
        final WorkspaceEntity entity = workspaceService.getAll().get(0);
        //TODO: uncomment the strings below after fix the integration between editor and front-end
        //final String decodedWorkspaceId = decodeBase64(workspaceId);
        //final WorkspaceEntity entity = workspaceService.get(decodedWorkspaceId);
        return workspaceInfoDescriptorMapper.map(entity);
    }
}

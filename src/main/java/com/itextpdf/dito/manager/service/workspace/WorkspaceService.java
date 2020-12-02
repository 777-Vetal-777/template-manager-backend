package com.itextpdf.dito.manager.service.workspace;

import com.itextpdf.dito.manager.entity.WorkspaceEntity;


public interface WorkspaceService {
    WorkspaceEntity get();

    WorkspaceEntity create(WorkspaceEntity workspace);

    WorkspaceEntity update(WorkspaceEntity workspace);
}

package com.itextpdf.dito.manager.integration.editor.mapper.workspace;

import com.itextpdf.dito.editor.server.common.core.descriptor.WorkspaceInfoDescriptor;
import com.itextpdf.dito.manager.entity.WorkspaceEntity;

public interface WorkspaceInfoDescriptorMapper {

    WorkspaceInfoDescriptor map(WorkspaceEntity entity);

}

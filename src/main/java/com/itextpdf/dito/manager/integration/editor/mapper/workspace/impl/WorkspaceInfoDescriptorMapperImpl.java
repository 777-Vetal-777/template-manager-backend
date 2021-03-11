package com.itextpdf.dito.manager.integration.editor.mapper.workspace.impl;

import com.itextpdf.dito.editor.server.common.core.descriptor.WorkspaceInfoDescriptor;
import com.itextpdf.dito.manager.entity.WorkspaceEntity;
import com.itextpdf.dito.manager.integration.editor.mapper.workspace.WorkspaceInfoDescriptorMapper;
import org.springframework.stereotype.Component;

@Component
public class WorkspaceInfoDescriptorMapperImpl implements WorkspaceInfoDescriptorMapper {

    @Override
    public WorkspaceInfoDescriptor map(final WorkspaceEntity entity) {
        final WorkspaceInfoDescriptor descriptor = new WorkspaceInfoDescriptor();

        descriptor.setDisplayName(entity.getName());
        descriptor.setLanguage(entity.getLanguage());
        descriptor.setTimeZone(entity.getTimezone());

        return descriptor;
    }
}

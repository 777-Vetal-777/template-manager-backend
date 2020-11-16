package com.itextpdf.dito.manager.service.workspace.impl;

import com.itextpdf.dito.manager.component.mapper.workspace.WorkspaceMapper;
import com.itextpdf.dito.manager.dto.workspace.create.WorkspaceCreateRequestDTO;
import com.itextpdf.dito.manager.dto.workspace.create.WorkspaceCreateResponseDTO;
import com.itextpdf.dito.manager.entity.WorkspaceEntity;
import com.itextpdf.dito.manager.repository.workspace.WorkspaceRepository;
import com.itextpdf.dito.manager.service.workspace.WorkspaceService;
import org.springframework.stereotype.Service;

@Service
public class WorkspaceServiceImpl implements WorkspaceService {
    private final WorkspaceMapper workspaceMapper;
    private final WorkspaceRepository workspaceRepository;

    public WorkspaceServiceImpl(final WorkspaceMapper workspaceMapper,
                                final WorkspaceRepository workspaceRepository) {
        this.workspaceMapper = workspaceMapper;
        this.workspaceRepository = workspaceRepository;
    }

    @Override
    public WorkspaceCreateResponseDTO create(final WorkspaceCreateRequestDTO workspaceCreateRequest) {
        final WorkspaceEntity entity = workspaceMapper.map(workspaceCreateRequest);
        return workspaceMapper.map(workspaceRepository.save(entity));
    }
}

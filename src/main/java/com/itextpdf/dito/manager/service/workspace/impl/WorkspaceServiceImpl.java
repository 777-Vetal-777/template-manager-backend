package com.itextpdf.dito.manager.service.workspace.impl;

import com.itextpdf.dito.manager.entity.WorkspaceEntity;
import com.itextpdf.dito.manager.exception.WorkspaceNameAlreadyExistsException;
import com.itextpdf.dito.manager.exception.WorkspaceNotFoundException;
import com.itextpdf.dito.manager.repository.workspace.WorkspaceRepository;
import com.itextpdf.dito.manager.service.workspace.WorkspaceService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkspaceServiceImpl implements WorkspaceService {
    private final WorkspaceRepository workspaceRepository;

    public WorkspaceServiceImpl(final WorkspaceRepository workspaceRepository) {
        this.workspaceRepository = workspaceRepository;
    }

    @Override
    public WorkspaceEntity get() {
        // TODO figure out how workspaces will be stored? Single or multiple workspaces per database?
        List<WorkspaceEntity> workspaces = workspaceRepository.findAll();
        if (workspaces.isEmpty()) {
            throw new WorkspaceNotFoundException("workspace does not exist");
        }
        return workspaces.get(0);
    }

    @Override
    public WorkspaceEntity create(final WorkspaceEntity workspace) {
        throwExceptionIfNameIsAlreadyInUse(workspace.getName());
        return workspaceRepository.save(workspace);
    }

    @Override
    public WorkspaceEntity update(final WorkspaceEntity workspace) {
        WorkspaceEntity oldWorkspace = get();

        if (!workspace.getName().equals(oldWorkspace.getName())) {
            throwExceptionIfNameIsAlreadyInUse(workspace.getName());
        }

        workspace.setId(oldWorkspace.getId());
        return workspaceRepository.save(workspace);
    }

    private void throwExceptionIfNameIsAlreadyInUse(final String workspaceName) {
        if (workspaceRepository.existsByName(workspaceName)) {
            throw new WorkspaceNameAlreadyExistsException("workspace with name " + workspaceName + " already exists");
        }
    }
}

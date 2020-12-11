package com.itextpdf.dito.manager.service.workspace.impl;

import com.itextpdf.dito.manager.entity.InstanceEntity;
import com.itextpdf.dito.manager.entity.PromotionPathEntity;
import com.itextpdf.dito.manager.entity.StageEntity;
import com.itextpdf.dito.manager.entity.WorkspaceEntity;
import com.itextpdf.dito.manager.exception.workspace.WorkspaceNameAlreadyExistsException;
import com.itextpdf.dito.manager.exception.workspace.WorkspaceNotFoundException;
import com.itextpdf.dito.manager.repository.stage.StageRepository;
import com.itextpdf.dito.manager.repository.workspace.WorkspaceRepository;
import com.itextpdf.dito.manager.service.instance.InstanceService;
import com.itextpdf.dito.manager.service.stage.StageService;
import com.itextpdf.dito.manager.service.workspace.WorkspaceService;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class WorkspaceServiceImpl implements WorkspaceService {
    private final InstanceService instanceService;
    private final StageService stageService;

    private final WorkspaceRepository workspaceRepository;

    public WorkspaceServiceImpl(final InstanceService instanceService, final StageService stageService,
            final WorkspaceRepository workspaceRepository,
            final StageRepository stageRepository) {
        this.instanceService = instanceService;
        this.stageService = stageService;
        this.workspaceRepository = workspaceRepository;
    }

    @Override
    public WorkspaceEntity get(final String name) {
        return workspaceRepository.findByName(name).orElseThrow(() -> new WorkspaceNotFoundException(name));
    }

    @Override
    public WorkspaceEntity create(final WorkspaceEntity workspace, final String mainDevelopmentInstanceName) {
        throwExceptionIfNameIsAlreadyInUse(workspace.getName());

        final PromotionPathEntity promotionPathEntity = buildDefaultPromotionPath(mainDevelopmentInstanceName);
        workspace.setPromotionPath(promotionPathEntity);

        return workspaceRepository.save(workspace);
    }

    private PromotionPathEntity buildDefaultPromotionPath(final String instanceName) {
        final PromotionPathEntity promotionPathEntity;

        final InstanceEntity instanceEntity = instanceService.get(instanceName);

        final StageEntity stageEntity = new StageEntity();
        stageEntity.setName("Development");
        stageEntity.setSequenceOrder(Integer.valueOf(0));
        stageEntity.addInstance(instanceEntity);

        promotionPathEntity = new PromotionPathEntity();
        promotionPathEntity.addStage(stageEntity);

        return promotionPathEntity;
    }

    @Override
    public WorkspaceEntity update(final String name, final WorkspaceEntity newWorkspace) {
        WorkspaceEntity oldWorkspace = get(name);

        if (!newWorkspace.getName().equals(oldWorkspace.getName())) {
            throwExceptionIfNameIsAlreadyInUse(newWorkspace.getName());
        }

        oldWorkspace.setName(newWorkspace.getName());
        oldWorkspace.setLanguage(newWorkspace.getLanguage());
        oldWorkspace.setTimezone(newWorkspace.getTimezone());

        return workspaceRepository.save(oldWorkspace);
    }

    @Override
    public PromotionPathEntity getPromotionPath(String workspace) {
        return get(workspace).getPromotionPath();
    }

    @Override
    public PromotionPathEntity updatePromotionPath(String workspace, PromotionPathEntity newPromotionPathEntity) {
        final WorkspaceEntity workspaceEntity = get(workspace);
        final PromotionPathEntity oldPromotionPathEntity = workspaceEntity.getPromotionPath();

        stageService.delete(oldPromotionPathEntity.getStages());
        oldPromotionPathEntity.getStages().clear();

        final List<StageEntity> newStages = fillStages(newPromotionPathEntity.getStages());
        oldPromotionPathEntity.addStages(newStages);

        return workspaceRepository.save(workspaceEntity).getPromotionPath();
    }

    private List<StageEntity> fillStages(final List<StageEntity> thinStageEntities) {
        final List<StageEntity> filledStageEntities = new ArrayList<>();

        for (int i = 0; i < thinStageEntities.size(); i++) {
            final StageEntity filledStageEntity = new StageEntity();

            final StageEntity thinStageEntity = thinStageEntities.get(i);
            filledStageEntity.setName(thinStageEntity.getName());
            for (final InstanceEntity thinInstanceEntity : thinStageEntity.getInstances()) {
                filledStageEntity.addInstance(instanceService.get(thinInstanceEntity.getName()));
            }
            filledStageEntity.setSequenceOrder(Integer.valueOf(i));

            filledStageEntities.add(filledStageEntity);
        }

        return filledStageEntities;
    }

    private void throwExceptionIfNameIsAlreadyInUse(final String workspaceName) {
        if (workspaceRepository.existsByName(workspaceName)) {
            throw new WorkspaceNameAlreadyExistsException(workspaceName);
        }
    }
}

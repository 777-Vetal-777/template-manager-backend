package com.itextpdf.dito.manager.service.workspace.impl;

import com.itextpdf.dito.manager.entity.InstanceEntity;
import com.itextpdf.dito.manager.entity.PromotionPathEntity;
import com.itextpdf.dito.manager.entity.StageEntity;
import com.itextpdf.dito.manager.entity.WorkspaceEntity;
import com.itextpdf.dito.manager.exception.workspace.OnlyOneWorkspaceAllowedException;
import com.itextpdf.dito.manager.exception.workspace.WorkspaceHasNoDevelopmentStageException;
import com.itextpdf.dito.manager.exception.workspace.WorkspaceNameAlreadyExistsException;
import com.itextpdf.dito.manager.exception.workspace.WorkspaceNotFoundException;
import com.itextpdf.dito.manager.repository.workspace.WorkspaceRepository;
import com.itextpdf.dito.manager.service.instance.InstanceService;
import com.itextpdf.dito.manager.service.stage.StageService;
import com.itextpdf.dito.manager.service.workspace.WorkspaceService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class WorkspaceServiceImpl implements WorkspaceService {
    private final InstanceService instanceService;
    private final StageService stageService;

    private final WorkspaceRepository workspaceRepository;

    public WorkspaceServiceImpl(final InstanceService instanceService, final StageService stageService,
                                final WorkspaceRepository workspaceRepository) {
        this.instanceService = instanceService;
        this.stageService = stageService;
        this.workspaceRepository = workspaceRepository;
    }

    @Override
    public WorkspaceEntity get(final String name) {
        return workspaceRepository.findByName(name).orElseThrow(() -> new WorkspaceNotFoundException(name));
    }

    @Override
    public List<WorkspaceEntity> getAll() {
        return workspaceRepository.findAll();
    }

    @Override
    public WorkspaceEntity create(final WorkspaceEntity workspace) {
        // TODO: 'if' block below will be removed in the future. Added in order to provide singular workspace support.
        if (!workspaceRepository.findAll().isEmpty()) {
            throw new OnlyOneWorkspaceAllowedException();
        }

        throwExceptionIfNameIsAlreadyInUse(workspace.getName());
        return workspaceRepository.save(workspace);
    }

    @Override
    public WorkspaceEntity setInstanceAsDefault(final String workspaceName, final String instanceName, final String userEmail) {
        final WorkspaceEntity workspaceEntity = getWorkspace(workspaceName);
        final PromotionPathEntity promotionPathEntity = buildDefaultPromotionPath(instanceService.get(instanceName));
        workspaceEntity.setPromotionPath(promotionPathEntity);
        return workspaceRepository.save(workspaceEntity);
    }

    private PromotionPathEntity buildDefaultPromotionPath(final InstanceEntity instanceEntity) {
        final StageEntity stageEntity = new StageEntity();
        stageEntity.setName("Development");
        stageEntity.setSequenceOrder(Integer.valueOf(0));
        stageEntity.addInstance(instanceEntity);

        final PromotionPathEntity promotionPathEntity = new PromotionPathEntity();
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
    public PromotionPathEntity getPromotionPath(final String workspace) {
        return get(workspace).getPromotionPath();
    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public PromotionPathEntity updatePromotionPath(final String workspace, final PromotionPathEntity newPromotionPathEntity) {

        if (Optional.ofNullable(newPromotionPathEntity)
                .map(PromotionPathEntity::getStages)
                .map(stages -> stages.get(0))
                .map(StageEntity::getInstances)
                .filter(instances -> instances.size() == 1).isEmpty()) {
            throw new WorkspaceHasNoDevelopmentStageException();
        }

        final WorkspaceEntity workspaceEntity = get(workspace);
        final PromotionPathEntity oldPromotionPathEntity = workspaceEntity.getPromotionPath();

        final List<StageEntity> oldPromotionPathEntityStages = oldPromotionPathEntity.getStages();
        stageService.delete(oldPromotionPathEntityStages);
        oldPromotionPathEntityStages.clear();

        final List<StageEntity> newStages = fillStages(newPromotionPathEntity.getStages(), oldPromotionPathEntity);
        oldPromotionPathEntity.addStages(newStages);

        return workspaceRepository.save(workspaceEntity).getPromotionPath();
    }

    @Override
    public List<String> getStageNames(final String workspaceName) {
        return workspaceRepository.getStageNames(workspaceName);
    }

    private List<StageEntity> fillStages(final List<StageEntity> thinStageEntities, final PromotionPathEntity promotionPathEntity) {
        final List<StageEntity> filledStageEntities = new ArrayList<>();

        for (int i = 0; i < thinStageEntities.size(); i++) {
            final StageEntity thinStageEntity = thinStageEntities.get(i);

            final StageEntity filledStageEntity = getFilledStageEntity(thinStageEntity, promotionPathEntity, i);

            filledStageEntities.add(filledStageEntity);
        }

        return filledStageEntities;
    }

    private StageEntity getFilledStageEntity(final StageEntity thinStageEntity, final PromotionPathEntity promotionPathEntity, final int i) {
        final StageEntity filledStageEntity = new StageEntity();

        filledStageEntity.setName(thinStageEntity.getName());
        filledStageEntity.setPromotionPath(promotionPathEntity);
        filledStageEntity.setSequenceOrder(Integer.valueOf(i));
        for (final InstanceEntity thinInstanceEntity : thinStageEntity.getInstances()) {
            filledStageEntity.addInstance(instanceService.get(thinInstanceEntity.getName()));
        }
        return filledStageEntity;
    }

    private void throwExceptionIfNameIsAlreadyInUse(final String workspaceName) {
        if (workspaceRepository.existsByName(workspaceName)) {
            throw new WorkspaceNameAlreadyExistsException(workspaceName);
        }
    }

    private WorkspaceEntity getWorkspace(final String workspace) {
        return workspaceRepository.findByName(workspace).orElseThrow(() -> new WorkspaceNotFoundException(workspace));
    }
}
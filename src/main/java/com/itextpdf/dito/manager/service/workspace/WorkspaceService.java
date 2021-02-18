package com.itextpdf.dito.manager.service.workspace;

import com.itextpdf.dito.manager.entity.InstanceEntity;
import com.itextpdf.dito.manager.entity.PromotionPathEntity;
import com.itextpdf.dito.manager.entity.WorkspaceEntity;

import java.util.List;


public interface WorkspaceService {
    WorkspaceEntity get(String name);

    List<WorkspaceEntity> getAll();

    WorkspaceEntity create(WorkspaceEntity workspace, byte[] data, List<InstanceEntity> instanceEntities, String fileName, String author, String defaultDevelopInstance);

    WorkspaceEntity update(String name, WorkspaceEntity workspace);

    PromotionPathEntity getPromotionPath(String workspace);

    PromotionPathEntity updatePromotionPath(String workspace, PromotionPathEntity promotionPathEntity);

    List<String> getStageNames(String workspaceName);

    Boolean checkIsWorkspaceWithNameExist(String workspaceName);
}

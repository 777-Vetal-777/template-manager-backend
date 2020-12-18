package com.itextpdf.dito.manager.service.workspace;

import com.itextpdf.dito.manager.entity.PromotionPathEntity;
import com.itextpdf.dito.manager.entity.WorkspaceEntity;

import java.util.List;


public interface WorkspaceService {
    WorkspaceEntity get(String name);

    List<WorkspaceEntity> getAll();

    WorkspaceEntity create(WorkspaceEntity workspace, String mainDevelopmentInstanceName);

    WorkspaceEntity update(String name, WorkspaceEntity workspace);

    PromotionPathEntity getPromotionPath(String workspace);

    PromotionPathEntity updatePromotionPath(String workspace, PromotionPathEntity promotionPathEntity);
}

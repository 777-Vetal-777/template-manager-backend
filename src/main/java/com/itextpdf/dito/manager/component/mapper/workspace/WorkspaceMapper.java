package com.itextpdf.dito.manager.component.mapper.workspace;

import com.itextpdf.dito.manager.dto.promotionpath.PromotionPathDTO;
import com.itextpdf.dito.manager.dto.stage.StageDTO;
import com.itextpdf.dito.manager.dto.workspace.WorkspaceDTO;
import com.itextpdf.dito.manager.dto.workspace.create.WorkspaceCreateRequestDTO;
import com.itextpdf.dito.manager.entity.PromotionPathEntity;
import com.itextpdf.dito.manager.entity.StageEntity;
import com.itextpdf.dito.manager.entity.WorkspaceEntity;

import java.util.List;

public interface WorkspaceMapper {
    WorkspaceEntity map(WorkspaceCreateRequestDTO dto);

    WorkspaceEntity map(WorkspaceDTO dto);

    WorkspaceDTO map(WorkspaceEntity entity);

    PromotionPathDTO map(PromotionPathEntity entity);

    PromotionPathEntity map(PromotionPathDTO dto);

    StageDTO map(StageEntity entity);

    StageEntity map(StageDTO dto);

    List<WorkspaceDTO> map(List<WorkspaceEntity> entities);
}

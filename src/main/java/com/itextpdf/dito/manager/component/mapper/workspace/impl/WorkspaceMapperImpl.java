package com.itextpdf.dito.manager.component.mapper.workspace.impl;

import com.itextpdf.dito.manager.component.mapper.instance.InstanceMapper;
import com.itextpdf.dito.manager.component.mapper.workspace.WorkspaceMapper;
import com.itextpdf.dito.manager.dto.promotionpath.PromotionPathDTO;
import com.itextpdf.dito.manager.dto.stage.StageDTO;
import com.itextpdf.dito.manager.dto.workspace.WorkspaceDTO;
import com.itextpdf.dito.manager.entity.PromotionPathEntity;
import com.itextpdf.dito.manager.entity.StageEntity;
import com.itextpdf.dito.manager.entity.WorkspaceEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class WorkspaceMapperImpl implements WorkspaceMapper {
    private static final Logger log = LogManager.getLogger(WorkspaceMapperImpl.class);
    private final InstanceMapper instanceMapper;

    public WorkspaceMapperImpl(final InstanceMapper instanceMapper) {
        this.instanceMapper = instanceMapper;
    }

    @Override
    public WorkspaceEntity map(final WorkspaceDTO dto) {
        log.info("Convert {} to workspace was started", dto);
        final WorkspaceEntity result = new WorkspaceEntity();

        result.setName(dto.getName());
        result.setLanguage(dto.getLanguage());
        result.setTimezone(dto.getTimezone());
        result.setAdjustForDaylight(dto.getAdjustForDaylight());
        log.info("Convert {} to workspace was finished successfully", dto);
        return result;
    }

    @Override
    public WorkspaceDTO map(final WorkspaceEntity entity) {
        log.info("Convert workspace: {} to workspaceDto was started", entity.getId());
        final WorkspaceDTO result = new WorkspaceDTO();

        result.setName(entity.getName());
        result.setLanguage(entity.getLanguage());
        result.setTimezone(entity.getTimezone());
        result.setAdjustForDaylight(entity.getAdjustForDaylight());
        result.setUuid(entity.getUuid());
        log.info("Convert workspace: {} to workspaceDto was finished successfully", entity.getId());
        return result;
    }

    @Override
    public PromotionPathDTO map(final PromotionPathEntity entity) {
        final PromotionPathDTO promotionPathDTO = new PromotionPathDTO();

        final List<StageDTO> stages = new ArrayList<>();
        for (final StageEntity stage : entity.getStages()) {
            stages.add(map(stage));
        }
        promotionPathDTO.setStages(stages);

        return promotionPathDTO;
    }

    @Override
    public PromotionPathEntity map(final PromotionPathDTO dto) {
        log.info("Convert {} to promotionPathEntity was started", dto);
        final PromotionPathEntity promotionPathEntity = new PromotionPathEntity();

        final List<StageEntity> stages = dto.getStages().stream().map(this::map).collect(Collectors.toList());
        promotionPathEntity.setStages(stages);

        return promotionPathEntity;
    }

    @Override
    public StageDTO map(final StageEntity entity) {
        log.info("Convert stage: {} to dto was started", entity.getId());
        final StageDTO stageDTO = new StageDTO();
        stageDTO.setName(entity.getName());
        stageDTO.setInstances(instanceMapper.mapEntities(entity.getInstances()));
        log.info("Convert stage: {} to dto was finished successfully", entity.getId());
        return stageDTO;
    }

    @Override
    public StageEntity map(final StageDTO dto) {
        log.info("Convert dto: {} to stageEntity was started", dto);
        final StageEntity stageEntity = new StageEntity();
        stageEntity.setName(dto.getName());
        stageEntity.setInstances(instanceMapper.mapDTOs(dto.getInstances()));
        log.info("Convert dto: {} to stageEntity was finished successfully", dto);
        return stageEntity;
    }

    @Override
    public List<WorkspaceDTO> map(final List<WorkspaceEntity> entities) {
        return entities.stream().map(this::map).collect(Collectors.toList());
    }

    @Override
    public WorkspaceEntity map(final String name, final String language, final String timezone, final boolean adjustForDaylight) {
        final WorkspaceEntity result = new WorkspaceEntity();
        result.setName(name);
        result.setLanguage(language);
        result.setTimezone(timezone);
        result.setAdjustForDaylight(adjustForDaylight);
        return result;
    }
}

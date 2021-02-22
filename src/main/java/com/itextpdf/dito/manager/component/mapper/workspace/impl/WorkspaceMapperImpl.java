package com.itextpdf.dito.manager.component.mapper.workspace.impl;

import com.itextpdf.dito.manager.component.mapper.instance.InstanceMapper;
import com.itextpdf.dito.manager.component.mapper.workspace.WorkspaceMapper;
import com.itextpdf.dito.manager.dto.promotionpath.PromotionPathDTO;
import com.itextpdf.dito.manager.dto.stage.StageDTO;
import com.itextpdf.dito.manager.dto.workspace.WorkspaceDTO;
import com.itextpdf.dito.manager.dto.workspace.create.WorkspaceCreateRequestDTO;
import com.itextpdf.dito.manager.entity.PromotionPathEntity;
import com.itextpdf.dito.manager.entity.StageEntity;
import com.itextpdf.dito.manager.entity.WorkspaceEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class WorkspaceMapperImpl implements WorkspaceMapper {
    private final InstanceMapper instanceMapper;

    public WorkspaceMapperImpl(final InstanceMapper instanceMapper) {
        this.instanceMapper = instanceMapper;
    }

    @Override
    public WorkspaceEntity map(final WorkspaceCreateRequestDTO dto) {
        final WorkspaceEntity result = new WorkspaceEntity();

        result.setLanguage(dto.getLanguage());
        result.setName(dto.getName());
        result.setTimezone(dto.getTimezone());
        result.setAdjustForDaylight(dto.getAdjustForDaylight());

        return result;
    }

    @Override
    public WorkspaceEntity map(final WorkspaceDTO dto) {
        final WorkspaceEntity result = new WorkspaceEntity();

        result.setName(dto.getName());
        result.setLanguage(dto.getLanguage());
        result.setTimezone(dto.getTimezone());
        result.setAdjustForDaylight(dto.getAdjustForDaylight());

        return result;
    }

    @Override
    public WorkspaceDTO map(final WorkspaceEntity entity) {
        final WorkspaceDTO result = new WorkspaceDTO();

        result.setName(entity.getName());
        result.setLanguage(entity.getLanguage());
        result.setTimezone(entity.getTimezone());
        result.setAdjustForDaylight(entity.getAdjustForDaylight());

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
        final PromotionPathEntity promotionPathEntity = new PromotionPathEntity();

        final List<StageEntity> stages = dto.getStages().stream().map(this::map).collect(Collectors.toList());
        promotionPathEntity.setStages(stages);

        return promotionPathEntity;
    }

    @Override
    public StageDTO map(final StageEntity entity) {
        final StageDTO stageDTO = new StageDTO();
        stageDTO.setName(entity.getName());
        stageDTO.setInstances(instanceMapper.mapEntities(entity.getInstances()));
        return stageDTO;
    }

    @Override
    public StageEntity map(final StageDTO dto) {
        final StageEntity stageEntity = new StageEntity();
        stageEntity.setName(dto.getName());
        stageEntity.setInstances(instanceMapper.mapDTOs(dto.getInstances()));
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

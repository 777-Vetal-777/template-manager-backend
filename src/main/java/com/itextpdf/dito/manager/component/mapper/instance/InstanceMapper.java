package com.itextpdf.dito.manager.component.mapper.instance;

import com.itextpdf.dito.manager.dto.instance.InstanceDTO;
import com.itextpdf.dito.manager.entity.InstanceEntity;

import java.util.List;

public interface InstanceMapper {
    InstanceEntity map(InstanceDTO dto);

    InstanceDTO map(InstanceEntity entity);

    List<InstanceEntity> mapDTOs(List<InstanceDTO> dtos);

    List<InstanceDTO> mapEntities(List<InstanceEntity> entities);
}

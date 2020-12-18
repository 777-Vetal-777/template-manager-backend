package com.itextpdf.dito.manager.component.mapper.instance;

import com.itextpdf.dito.manager.dto.instance.InstanceDTO;
import com.itextpdf.dito.manager.dto.instance.update.InstanceUpdateRequestDTO;
import com.itextpdf.dito.manager.dto.instance.create.InstanceRememberRequestDTO;
import com.itextpdf.dito.manager.entity.InstanceEntity;

import java.util.List;
import org.springframework.data.domain.Page;

public interface InstanceMapper {
    InstanceEntity map(InstanceDTO dto);

    InstanceDTO map(InstanceEntity entity);

    InstanceEntity map(InstanceRememberRequestDTO dto);

    List<InstanceEntity> map(List<InstanceRememberRequestDTO> dtos);

    List<InstanceDTO> mapEntities(List<InstanceEntity> entities);

    List<InstanceEntity> mapDTOs(List<InstanceDTO> dtos);

    Page<InstanceDTO> map(Page<InstanceEntity> entities);

    InstanceEntity map(InstanceUpdateRequestDTO dto);

}

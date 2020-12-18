package com.itextpdf.dito.manager.component.mapper.instance.impl;

import com.itextpdf.dito.manager.component.mapper.instance.InstanceMapper;
import com.itextpdf.dito.manager.dto.instance.InstanceDTO;
import com.itextpdf.dito.manager.dto.instance.update.InstanceUpdateRequestDTO;
import com.itextpdf.dito.manager.dto.instance.create.InstanceRememberRequestDTO;
import com.itextpdf.dito.manager.entity.InstanceEntity;
import com.itextpdf.dito.manager.entity.TemplateEntity;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class InstanceMapperImpl implements InstanceMapper {
    @Override
    public InstanceEntity map(final InstanceDTO dto) {
        final InstanceEntity instanceEntity = new InstanceEntity();
        instanceEntity.setName(dto.getName());
        instanceEntity.setSocket(dto.getSocket());
        return instanceEntity;
    }

    @Override
    public InstanceDTO map(final InstanceEntity entity) {
        final InstanceDTO instanceDTO = new InstanceDTO();
        instanceDTO.setName(entity.getName());
        instanceDTO.setSocket(entity.getSocket());
        instanceDTO.setCreatedBy(entity.getCreatedBy().getEmail());
        instanceDTO.setCreatedOn(entity.getCreatedOn());
        final List<TemplateEntity> templateEntities = entity.getTemplates();
        if (templateEntities != null && !templateEntities.isEmpty()) {
            instanceDTO.setTemplates(templateEntities.stream().map(templateEntity -> templateEntity.getName())
                    .collect(Collectors.toList()));
        }
        return instanceDTO;
    }

    @Override
    public InstanceEntity map(final InstanceRememberRequestDTO dto) {
        final InstanceEntity entity = new InstanceEntity();
        entity.setName(dto.getName());
        entity.setSocket(dto.getSocket());
        return entity;
    }

    @Override
    public List<InstanceEntity> map(final List<InstanceRememberRequestDTO> dto) {
        return dto.stream().map(this::map).collect(Collectors.toList());
    }

    @Override
    public List<InstanceDTO> mapEntities(final List<InstanceEntity> entities) {
        return entities.stream().map(this::map).collect(Collectors.toList());
    }

    @Override
    public List<InstanceEntity> mapDTOs(List<InstanceDTO> dtos) {
        return dtos.stream().map(this::map).collect(Collectors.toList());
    }

    @Override
    public Page<InstanceDTO> map(Page<InstanceEntity> entities) {
        return entities.map(this::map);
    }

    @Override
    public InstanceEntity map(InstanceUpdateRequestDTO dto) {
        final InstanceEntity entity = new InstanceEntity();
        entity.setName(dto.getName());
        entity.setSocket(dto.getSocket());
        return entity;
    }
}

package com.itextpdf.dito.manager.component.mapper.instance.impl;

import com.itextpdf.dito.manager.component.mapper.instance.InstanceMapper;
import com.itextpdf.dito.manager.dto.instance.InstanceDTO;
import com.itextpdf.dito.manager.dto.instance.create.InstanceRememberRequestDTO;
import com.itextpdf.dito.manager.dto.instance.update.InstanceUpdateRequestDTO;
import com.itextpdf.dito.manager.dto.template.version.TemplateInstanceVersionDTO;
import com.itextpdf.dito.manager.entity.InstanceEntity;
import com.itextpdf.dito.manager.entity.StageEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class InstanceMapperImpl implements InstanceMapper {
    private static final Logger log = LogManager.getLogger(InstanceMapperImpl.class);

    @Override
    public InstanceEntity map(final InstanceDTO dto) {
        log.info("Convert dto: {} to entity was started", dto);
        final InstanceEntity instanceEntity = new InstanceEntity();
        instanceEntity.setName(dto.getName());
        instanceEntity.setSocket(dto.getSocket());
        log.info("Convert dto: {} to entity was finished successfully", dto);
        return instanceEntity;
    }

    @Override
    public InstanceDTO map(final InstanceEntity entity) {
        log.info("Convert instance:{} to dto was started", entity.getId());
        final InstanceDTO instanceDTO = new InstanceDTO();
        instanceDTO.setName(entity.getName());
        instanceDTO.setSocket(entity.getSocket());
        instanceDTO.setCreatedBy(new StringBuilder().append(entity.getCreatedBy().getFirstName())
                .append(" ")
                .append(entity.getCreatedBy().getLastName())
                .toString());
        instanceDTO.setCreatedOn(entity.getCreatedOn());
        final List<TemplateFileEntity> templateFileEntities = entity.getTemplateFile();
        if (!CollectionUtils.isEmpty(templateFileEntities)) {
            instanceDTO.setTemplates(templateFileEntities.stream().map(templateFileEntity -> {
                final TemplateInstanceVersionDTO templateInstanceVersionDTO = new TemplateInstanceVersionDTO();
                templateInstanceVersionDTO.setName(templateFileEntity.getTemplate().getName());
                templateInstanceVersionDTO.setVersion(templateFileEntity.getVersion());
                return templateInstanceVersionDTO;
            }).collect(Collectors.toList()));
        }
        final StageEntity stageEntity = entity.getStage();
        if (stageEntity != null) {
            instanceDTO.setStage(stageEntity.getName());
        }
        log.info("Convert instance: {} to dto was finished successfully", entity.getId());
        return instanceDTO;
    }

    @Override
    public InstanceEntity map(final InstanceRememberRequestDTO dto) {
        log.info("Convert {} to entity was started", dto);
        final InstanceEntity entity = new InstanceEntity();
        entity.setName(dto.getName());
        entity.setSocket(dto.getSocket());
        log.info("Convert {} to entity was finished successfully", dto);
        return entity;
    }

    @Override
    public List<InstanceEntity> map(final List<InstanceRememberRequestDTO> dto) {
        return dto.stream().map(this::map).collect(Collectors.toList());
    }

    @Override
    public List<InstanceDTO> mapEntities(final List<InstanceEntity> entities) {
        return !CollectionUtils.isEmpty(entities)
                ? entities.stream().map(this::map).collect(Collectors.toList())
                : Collections.emptyList();
    }

    @Override
    public List<InstanceEntity> mapDTOs(final List<InstanceDTO> dtos) {
        return dtos.stream().map(this::map).collect(Collectors.toList());
    }

    @Override
    public Page<InstanceDTO> map(final Page<InstanceEntity> entities) {
        return entities.map(this::map);
    }

    @Override
    public InstanceEntity map(final InstanceUpdateRequestDTO dto) {
        log.info("Convert {} to entity was started", dto);
        final InstanceEntity entity = new InstanceEntity();
        entity.setName(dto.getName());
        entity.setSocket(dto.getSocket());
        log.info("Convert {} to entity was finished successfully", dto);
        return entity;
    }
}

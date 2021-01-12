package com.itextpdf.dito.manager.component.mapper.resource;

import com.itextpdf.dito.manager.dto.resource.ResourceDTO;
import com.itextpdf.dito.manager.dto.resource.ResourceFileDTO;
import com.itextpdf.dito.manager.dto.resource.update.ResourceUpdateRequestDTO;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceFileEntity;
import org.springframework.data.domain.Page;

public interface ResourceMapper {
    ResourceDTO map(ResourceEntity entity);

    ResourceDTO mapWithFile(ResourceEntity entity);

    ResourceEntity map(ResourceUpdateRequestDTO dto);

    Page<ResourceDTO> map(Page<ResourceEntity> entities);

    ResourceFileDTO map(ResourceFileEntity entity);

    Page<ResourceFileDTO> mapVersions(Page<ResourceFileEntity> entities);
}

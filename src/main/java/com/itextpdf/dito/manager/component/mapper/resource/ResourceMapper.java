package com.itextpdf.dito.manager.component.mapper.resource;

import com.itextpdf.dito.manager.dto.dependency.DependencyDTO;
import com.itextpdf.dito.manager.dto.resource.ResourceDTO;
import com.itextpdf.dito.manager.dto.resource.ResourceFileDTO;
import com.itextpdf.dito.manager.dto.resource.update.ResourceUpdateRequestDTO;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceFileEntity;
import com.itextpdf.dito.manager.model.resource.ResourceDependencyModel;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ResourceMapper {
    ResourceDTO map(ResourceEntity entity);

    ResourceDTO mapWithFile(ResourceEntity entity);

    ResourceEntity map(ResourceUpdateRequestDTO dto);

    Page<ResourceDTO> map(Page<ResourceEntity> entities);

    ResourceFileDTO map(ResourceFileEntity entity);

    Page<ResourceFileDTO> mapVersions(Page<ResourceFileEntity> entities);

    DependencyDTO map(ResourceDependencyModel model);

    Page<DependencyDTO> mapDependencies(Page<ResourceDependencyModel> models);

    List<DependencyDTO> map(List<ResourceDependencyModel> entities);
}

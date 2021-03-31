package com.itextpdf.dito.manager.component.mapper.resource;

import com.itextpdf.dito.manager.model.resource.ResourceModelWithRoles;
import com.itextpdf.dito.manager.dto.resource.ResourceDTO;
import com.itextpdf.dito.manager.dto.resource.update.ResourceUpdateRequestDTO;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import org.springframework.data.domain.Page;

public interface ResourceMapper {
    ResourceDTO map(ResourceEntity entity, String email);

    ResourceEntity map(ResourceUpdateRequestDTO dto);

    Page<ResourceDTO> mapModels(Page<ResourceModelWithRoles> models, String email);

    ResourceDTO mapModel(ResourceModelWithRoles model, String email);

}

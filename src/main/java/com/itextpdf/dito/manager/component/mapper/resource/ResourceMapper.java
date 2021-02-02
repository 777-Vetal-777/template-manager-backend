package com.itextpdf.dito.manager.component.mapper.resource;

import com.itextpdf.dito.manager.dto.resource.ResourceDTO;
import com.itextpdf.dito.manager.dto.resource.ResourceIdDTO;
import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.dto.resource.update.ResourceUpdateRequestDTO;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import org.springframework.data.domain.Page;

public interface ResourceMapper {
    ResourceDTO map(ResourceEntity entity);

    ResourceEntity map(ResourceUpdateRequestDTO dto);

    Page<ResourceDTO> map(Page<ResourceEntity> entities);

    //TODO REPLACE TO INTEGRATION ResourceLeafDescriptorMapperImpl
    String encodeId(String name, ResourceTypeEnum type);

    //TODO REPLACE TO INTEGRATION ResourceLeafDescriptorMapperImpl
    ResourceIdDTO deserialize(String data);

    //TODO REPLACE TO INTEGRATION ResourceLeafDescriptorMapperImpl
    String encode(String name);

    //TODO REPLACE TO INTEGRATION ResourceLeafDescriptorMapperImpl
    String serialize(Object data);

    //TODO REPLACE TO INTEGRATION ResourceLeafDescriptorMapperImpl
    String decode(String name);
}

package com.itextpdf.dito.manager.service.resource;

import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.filter.resource.ResourceFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ResourceService {
    ResourceEntity create(String name, ResourceTypeEnum type, byte[] data, String fileName, String email);

    ResourceEntity get(String name, ResourceTypeEnum type);

    ResourceEntity update(String name, ResourceEntity entity, String mail);

    Page<ResourceEntity> list(Pageable pageable, ResourceFilter filter, String searchParam);

}

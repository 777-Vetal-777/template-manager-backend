package com.itextpdf.dito.manager.service.resource;

import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.entity.resource.ResourceFileEntity;
import com.itextpdf.dito.manager.filter.version.VersionFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ResourceVersionsService {
    Page<ResourceFileEntity> list(Pageable pageable, String name, ResourceTypeEnum type, VersionFilter filter, String searchParam);

}

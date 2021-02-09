package com.itextpdf.dito.manager.service.resource;

import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.filter.version.VersionFilter;
import com.itextpdf.dito.manager.model.file.FileVersionModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ResourceVersionsService {
    Page<FileVersionModel> list(Pageable pageable, String name, ResourceTypeEnum type, VersionFilter filter, String searchParam);

    ResourceEntity rollbackVersion(String resourceName, ResourceTypeEnum resourceType, String userEmail, Long version);
}

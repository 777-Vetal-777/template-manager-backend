package com.itextpdf.dito.manager.service.resource;

import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.filter.resource.ResourcePermissionFilter;
import com.itextpdf.dito.manager.model.resource.ResourcePermissionModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ResourcePermissionService {
    Page<ResourcePermissionModel> getRoles(Pageable pageable, String name, ResourceTypeEnum type, ResourcePermissionFilter filter, String search);
}

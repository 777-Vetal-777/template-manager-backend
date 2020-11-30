package com.itextpdf.dito.manager.service.permission;

import com.itextpdf.dito.manager.entity.PermissionEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PermissionService {
    Page<PermissionEntity> list(Pageable pageable, String searchParam);
}

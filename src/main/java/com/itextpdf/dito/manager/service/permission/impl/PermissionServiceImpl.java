package com.itextpdf.dito.manager.service.permission.impl;

import com.itextpdf.dito.manager.entity.PermissionEntity;
import com.itextpdf.dito.manager.repository.permission.PermissionRepository;
import com.itextpdf.dito.manager.service.permission.PermissionService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class PermissionServiceImpl implements PermissionService {
    private final PermissionRepository permissionRepository;

    public PermissionServiceImpl(final PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    public Page<PermissionEntity> list(Pageable pageable, String searchParam) {
        return StringUtils.isEmpty(searchParam)
                ? permissionRepository.findAll(pageable)
                : permissionRepository.search(pageable, searchParam);
    }
}

package com.itextpdf.dito.manager.repository.permission;

import com.itextpdf.dito.manager.entity.PermissionEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<PermissionEntity, Long> {
}

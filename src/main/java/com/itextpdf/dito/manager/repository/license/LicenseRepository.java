package com.itextpdf.dito.manager.repository.license;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.itextpdf.dito.manager.entity.LicenseEntity;
import com.itextpdf.dito.manager.entity.WorkspaceEntity;

@Repository
public interface LicenseRepository extends JpaRepository<LicenseEntity, Long>{
	 Optional<LicenseEntity> findByWorkspace(WorkspaceEntity workspace);
}

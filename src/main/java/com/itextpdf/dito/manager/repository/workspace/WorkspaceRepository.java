package com.itextpdf.dito.manager.repository.workspace;

import com.itextpdf.dito.manager.entity.WorkspaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkspaceRepository extends JpaRepository<WorkspaceEntity, Long> {
}

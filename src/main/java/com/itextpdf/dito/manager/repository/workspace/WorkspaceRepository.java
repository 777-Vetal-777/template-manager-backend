package com.itextpdf.dito.manager.repository.workspace;

import com.itextpdf.dito.manager.entity.WorkspaceEntity;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkspaceRepository extends JpaRepository<WorkspaceEntity, Long> {
    Optional<WorkspaceEntity> findByName(String name);

    boolean existsByName(String name);
}

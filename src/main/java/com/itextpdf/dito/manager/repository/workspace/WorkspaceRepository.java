package com.itextpdf.dito.manager.repository.workspace;

import com.itextpdf.dito.manager.entity.WorkspaceEntity;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkspaceRepository extends JpaRepository<WorkspaceEntity, Long> {
    Optional<WorkspaceEntity> findByName(String name);

    Optional<WorkspaceEntity> findFirstByUuid(String uuid);

    boolean existsByName(String name);

    @Query(value = "select distinct stage.name from WorkspaceEntity workspace " +
            "join workspace.promotionPath.stages stage")
    List<String> getStageNames(@Param("workspaceName") String workspaceName);
}

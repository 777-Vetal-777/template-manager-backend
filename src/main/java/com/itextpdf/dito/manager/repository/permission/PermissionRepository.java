package com.itextpdf.dito.manager.repository.permission;

import com.itextpdf.dito.manager.entity.PermissionEntity;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<PermissionEntity, Long> {
    Optional<PermissionEntity> findByName(String name);

    @Query(value = "select p from PermissionEntity p "
            + "where LOWER(p.name) like  LOWER(CONCAT('%',:value,'%'))")
    Page<PermissionEntity> search(Pageable pageable, @Param("value") String searchParam);

    List<PermissionEntity> findByNameIn(List<String> names);
}

package com.itextpdf.dito.manager.repository.role;

import com.itextpdf.dito.manager.entity.RoleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    Optional<RoleEntity> findByName(String name);

    @Query(value = "select r from RoleEntity r "
            + "where r.name like '%'||:value||'%'")
    Page<RoleEntity> search(Pageable pageable, @Param("value") String searchParam);
}

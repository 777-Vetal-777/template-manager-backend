package com.itextpdf.dito.manager.repository.role;

import com.itextpdf.dito.manager.entity.RoleType;
import com.itextpdf.dito.manager.entity.RoleTypeEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleTypeRepository extends JpaRepository<RoleTypeEntity, Long> {
    RoleTypeEntity findByName(RoleType name);
}

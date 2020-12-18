package com.itextpdf.dito.manager.repository.stage;

import com.itextpdf.dito.manager.entity.StageEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StageRepository extends JpaRepository<StageEntity, Long> {
}

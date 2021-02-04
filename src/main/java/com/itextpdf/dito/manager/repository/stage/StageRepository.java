package com.itextpdf.dito.manager.repository.stage;

import com.itextpdf.dito.manager.entity.StageEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StageRepository extends JpaRepository<StageEntity, Long> {

    @Query("select s from stage s where s.sequenceOrder=0")
    Optional<StageEntity> findDefaultStage();
}
